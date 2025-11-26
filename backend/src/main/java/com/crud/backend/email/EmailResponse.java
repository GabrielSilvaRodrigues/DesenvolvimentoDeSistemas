package com.crud.backend.email;

import com.crud.backend.token.TokenEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    private String para;
    private String assunto;
    private String corpo;
    private String token;
    private String tipo;
    private String usuarioGerou;

    public static EmailResponse from(EmailDTO dto, TokenEntity token, String corpoFinal) {
        EmailResponse resp = new EmailResponse();
        resp.setPara(dto.getPara());
        resp.setAssunto(dto.getAssunto());
        resp.setCorpo(corpoFinal);
        resp.setToken(token.getValor());
        resp.setTipo(token.getTipo().name());
        resp.setUsuarioGerou(token.getUsuario() != null ? token.getUsuario().getEmail() : "desconhecido");
        return resp;
    }
}
