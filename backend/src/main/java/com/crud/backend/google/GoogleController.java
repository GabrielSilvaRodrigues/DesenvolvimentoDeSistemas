package com.crud.backend.google;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/google")
public class GoogleController {

    private final GoogleService googleService;

    public GoogleController(GoogleService googleService) {
        this.googleService = googleService;
    }

    /**
     * Endpoint simplificado para autenticação via Google.
     * Recebe dados mínimos (googleId, email, name, picture, locale) e monta um OAuth2User
     * para delegar ao `GoogleService.autenticarGoogle`.
     */
    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@Valid @RequestBody GoogleAuthRequest req) {
        try {
            return ResponseEntity.ok(googleService.autenticarGoogle(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao autenticar via Google");
        }
    }
}
