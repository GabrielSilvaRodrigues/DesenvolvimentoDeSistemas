package com.crud.backend.token;

import com.crud.backend.usuario.UsuarioEntity;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenResponse {
    private TokenEntity token;
    private UsuarioEntity usuario;
}
