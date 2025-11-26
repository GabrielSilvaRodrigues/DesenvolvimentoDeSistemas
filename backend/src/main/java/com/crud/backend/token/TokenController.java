package com.crud.backend.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/validar")
    public ResponseEntity<TokenEntity> validarQuery(
            @RequestParam String token,
            @RequestParam String dispositivo
    ) {
        return tokenService.validarToken(token, dispositivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/revogar/{token}")
    public ResponseEntity<Void> revogar(@PathVariable String token) {
        tokenService.revogarToken(token);
        return ResponseEntity.noContent().build();
    }
}
