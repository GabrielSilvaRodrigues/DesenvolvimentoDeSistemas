package com.crud.backend.viaCep;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/viacep")
public class ViaCepController {

	private final ViaCepService viaCepService;

	public ViaCepController(ViaCepService viaCepService) {
		this.viaCepService = viaCepService;
	}

	@GetMapping("/{cep}")
	public ResponseEntity<ViaCepDTO> buscarCep(@PathVariable String cep) {
		if (cep == null) return ResponseEntity.badRequest().build();
		String sanitized = cep.replaceAll("\\D", "");
		ViaCepDTO dto = viaCepService.buscarCep(sanitized);
		if (dto == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(dto);
	}

}
