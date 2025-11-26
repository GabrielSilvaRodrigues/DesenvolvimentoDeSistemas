package com.crud.backend.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.crud.backend.token.TokenEntity;
import com.crud.backend.token.TokenEnum;

import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailResponse enviarEmail(TokenEntity token, EmailDTO email) {

        String corpoFinal = gerarMensagemPorTipo(
                token.getTipo(),
                token.getValor(),
                token.getUsuario() != null ? token.getUsuario().getEmail() : "usuário não informado",
                token.getDispositivo() // passa dispositivo do token para o link
        );

        try {
            SimpleMailMessage msg = new SimpleMailMessage();

            msg.setFrom(email.getFromName() + " <" + email.getFromAddress() + ">");
            msg.setTo(email.getPara());
            msg.setSubject(email.getAssunto());
            msg.setText(corpoFinal);

            mailSender.send(msg);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar o e-mail: " + e.getMessage());
        }

        return EmailResponse.from(email, token, corpoFinal);
    }

    // novo método público para gerar mensagem a partir do TokenEntity (uso por outros services)
    public String gerarMensagem(TokenEntity token) {
        if (token == null) return "";
        return gerarMensagemPorTipo(
                token.getTipo(),
                token.getValor(),
                token.getUsuario() != null ? token.getUsuario().getEmail() : "usuário não informado",
                token.getDispositivo()
        );
    }

    // ---------------------------------------------------
    // GERA MENSAGEM DO E-MAIL CONFORME TIPO DO TOKEN
    // ---------------------------------------------------

    private String gerarMensagemPorTipo(TokenEnum tipo, String token, String emailUsuario, String dispositivo) {

        // link aponta para frontend (SPA) que tratará o token, inclui dispositivo para validação no backend
        String base = "http://localhost:5173/token/";

        // encode token and dispositivo for safety
        String tokenEnc = token != null ? URLEncoder.encode(token, StandardCharsets.UTF_8) : "";
        String dispEnc = dispositivo != null ? URLEncoder.encode(dispositivo, StandardCharsets.UTF_8) : "";

        String withParams = base + "cadastro?token=" + tokenEnc + (dispEnc.isEmpty() ? "" : "&dispositivo=" + dispEnc);
        String denunciar = base + "denunciar?token=" + tokenEnc + (dispEnc.isEmpty() ? "" : "&dispositivo=" + dispEnc);

        return switch (tipo) {
            case AUTENTICACAO ->
                    "Olá " + emailUsuario + ",\n\n"
                            + "Confirme sua conta:\n"
                            + withParams
                            + "\n\nCaso não reconheça:\n"
                            + denunciar;

            case RECUPERACAO_SENHA ->
                    "Olá " + emailUsuario + ",\n\n"
                            + "Redefina sua senha:\n"
                            + base + "recuperacao?token=" + tokenEnc + (dispEnc.isEmpty() ? "" : "&dispositivo=" + dispEnc)
                            + "\n\nCaso não reconheça:\n"
                            + denunciar;

            case LOGIN ->
                    "Olá " + emailUsuario + ",\n\n"
                            + "Novo login detectado:\n"
                            + base + "login?token=" + tokenEnc + (dispEnc.isEmpty() ? "" : "&dispositivo=" + dispEnc)
                            + "\n\nSe não foi você:\n"
                            + denunciar;

            default ->
                    "Olá!\n\nAção não reconhecida. Token: " + tokenEnc
                    + "\n\nSe não foi você:\n"
                            + denunciar;
        };
    }
}
