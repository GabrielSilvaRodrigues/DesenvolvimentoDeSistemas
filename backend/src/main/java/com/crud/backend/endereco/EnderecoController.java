package com.crud.backend.endereco;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/endereco")
public class EnderecoController {

	private final EnderecoService enderecoService;

	public EnderecoController(EnderecoService enderecoService) {
		this.enderecoService = enderecoService;
	}

	@PostMapping
	public EnderecoResponse criar(@RequestParam String cep,
								  @RequestParam Integer numero,
								  @RequestParam(required = false) String complemento) {
		return enderecoService.criarEndereco(cep, numero, complemento);
	}

	@GetMapping("/{id}")
	public EnderecoResponse buscar(@PathVariable Long id) {
		return enderecoService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	public EnderecoResponse atualizar(@PathVariable Long id,
									  @RequestParam String cep,
									  @RequestParam Integer numero,
									  @RequestParam(required = false) String complemento) {
		return enderecoService.atualizarEndereco(id, cep, numero, complemento);
	}

	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		enderecoService.deletar(id);
	}

}
