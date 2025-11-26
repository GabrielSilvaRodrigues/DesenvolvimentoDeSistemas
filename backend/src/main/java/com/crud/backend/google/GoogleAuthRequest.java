package com.crud.backend.google;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank String googleId,
        @NotBlank String email,
        @NotBlank String name,
        String picture,
        String locale,
        @NotBlank String dispositivo,
        @NotBlank String ip
) {}
