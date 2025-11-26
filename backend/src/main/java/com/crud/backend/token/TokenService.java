package com.crud.backend.token;

import com.crud.backend.usuario.UsuarioEntity;
import com.crud.backend.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public TokenEntity gerarToken(UsuarioEntity usuario, TokenEnum tipo, String dispositivo, String ip) {
        String jwt = jwtService.generateToken(usuario);
        TokenEntity token = TokenEntity.builder()
                .valor(jwt)
                .usuario(usuario)
                .tipo(tipo)
                .dispositivo(dispositivo)
                .ip(ip)
                .expiraEm(LocalDateTime.now().plusDays(7))
                .ativo(true)
                .build();
        return tokenRepository.save(token);
    }

    public Optional<TokenEntity> validarToken(String token, String dispositivo) {
        return tokenRepository.findByValor(token)
                .filter(TokenEntity::isAtivo)
                .filter(t -> t.getDispositivo().equals(dispositivo))
                .filter(t -> t.getExpiraEm().isAfter(LocalDateTime.now()));
    }


    public void revogarToken(String token) {
        tokenRepository.findByValor(token).ifPresent(t -> {
            t.setAtivo(false);
            tokenRepository.save(t);
        });
    }
}