package com.crud.backend.auth;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRegisterRequest(
    @NotBlank String nome,
    @NotBlank String email,
    @NotBlank String senha,
    @NotBlank String dispositivo,
    @NotBlank String ip
) {}
