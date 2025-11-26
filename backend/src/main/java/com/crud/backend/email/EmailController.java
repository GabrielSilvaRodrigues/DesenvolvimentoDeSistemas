package com.crud.backend.email;

import com.crud.backend.token.TokenEntity;
import com.crud.backend.token.TokenEnum;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

	private final EmailService emailService;

	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * Envia um e-mail usando o EmailService. Parâmetros simples via RequestParam para manter o controller enxuto.
	 * Exemplo: POST /api/email/enviar?tipo=AUTENTICACAO&tokenValor=abc123&para=dest@ex.com&assunto=Oi&fromAddress=a@b.com&fromName=Sistema
	 */
	@PostMapping("/enviar")
	public EmailResponse enviar(@RequestParam String tipo,
								@RequestParam String tokenValor,
								@RequestParam String para,
								@RequestParam String assunto,
								@RequestParam String fromAddress,
								@RequestParam String fromName) {

		// monta token mínimo (usuario opcional)
		TokenEntity token = TokenEntity.builder()
				.valor(tokenValor)
				.tipo(TokenEnum.valueOf(tipo))
				.build();

		EmailDTO dto = new EmailDTO(para, assunto, "", fromAddress, fromName);

		return emailService.enviarEmail(token, dto);
	}

}
