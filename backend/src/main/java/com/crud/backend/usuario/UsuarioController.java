package com.crud.backend.usuario;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.crud.backend.github.GithubDriveService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/*
  ...existing code...
  Este arquivo foi estendido para incluir upload de imagem de perfil.
*/

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	private final UsuarioService usuarioService;
	private final GithubDriveService githubDriveService;

	public UsuarioController(UsuarioService usuarioService, GithubDriveService githubDriveService) {
		this.usuarioService = usuarioService;
		this.githubDriveService = githubDriveService;
	}

	// permite pontos no path variable (ex.: user.name@domain.com)
	@GetMapping("/email/{email}")
	public ResponseEntity<UsuarioEntity> buscarPorEmail(@PathVariable String email) {
		return usuarioService.buscarPorEmail(email)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Recebe multipart file, renomeia para {id}{ext} e envia para {id}/perfil/{id}{ext}.
	 * Atualiza usuario.profileImage com a URL raw.githubusercontent e retorna a URL.
	 */
	@PostMapping("/{id}/profile-image")
	public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		try {
			UsuarioEntity usuario = usuarioService.buscarPorId(id); // lança RuntimeException se não encontrado

			String original = file.getOriginalFilename();
			String ext = "";
			if (original != null && original.contains(".")) {
				ext = original.substring(original.lastIndexOf('.'));
			}

			String filename = id + ext;
			String path = id + "/perfil/" + filename;

			String url = githubDriveService.uploadBytes(file.getBytes(), path, "upload profile image for user " + id);

			usuario.setProfileImage(url);
			usuarioService.salvar(usuario);

			return ResponseEntity.ok(Map.of("url", url));
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Erro ao processar arquivo: " + e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Erro ao enviar imagem para GitHub: " + e.getMessage());
		}
	}

}
