package com.crud.backend.token;

import com.crud.backend.usuario.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final com.crud.backend.security.jwt.JwtService jwtService;

    public TokenEntity gerarToken(Usuario usuario, String dispositivo, String ip) {
        String jwt = jwtService.generateToken(usuario);
        TokenEntity token = TokenEntity.builder()
                .token(jwt)
                .usuario(usuario)
                .dispositivo(dispositivo)
                .ip(ip)
                .expiraEm(LocalDateTime.now().plusDays(7))
                .ativo(true)
                .build();
        return tokenRepository.save(token);
    }

    public Optional<TokenEntity> validarToken(String token, String dispositivo) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.isAtivo() && t.getDispositivo().equals(dispositivo));
    }

    public void revogarToken(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setAtivo(false);
            tokenRepository.save(t);
        });
    }
}