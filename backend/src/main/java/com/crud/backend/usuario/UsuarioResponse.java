package com.crud.backend.usuario;

import com.crud.backend.google.GoogleDTO;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
public class UsuarioResponse {
    private UsuarioEntity usuario;
    private GoogleDTO google;
}
