package com.crud.backend.google;

import com.crud.backend.token.TokenEntity;
import com.crud.backend.token.TokenEnum;
import com.crud.backend.token.TokenService;
import com.crud.backend.usuario.OAuth2Enum;
import com.crud.backend.usuario.UsuarioEntity;
import com.crud.backend.usuario.UsuarioEnum;
import com.crud.backend.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final GoogleRepository googleRepository;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    public TokenEntity autenticarGoogle(GoogleAuthRequest req) {
        var googleDto = new GoogleDTO(req.googleId(), req.name(), null, null, req.email(), req.picture(), req.locale());

        UsuarioEntity usuario = resolveUsuario(googleDto);
        persistGoogle(usuario, googleDto);

        return tokenService.gerarToken(usuario, TokenEnum.LOGIN, req.dispositivo(), req.ip());
    }

    /**
     * Processa um OAuth2User recebido pelo fluxo do Spring Security (login via Google).
     * Cria/associa o GoogleEntity + UsuarioEntity e gera um token (TokenEntity) para a sessão.
     * Retorna o valor do token (JWT) para redirecionamento ao frontend.
     */
    public String handleOAuth2Login(OAuth2User principal, HttpServletRequest request) {
        // monta DTO a partir do OAuth2User (atributos padrão do Google)
        GoogleDTO dto = new GoogleDTO(principal);
        // resolve/cria usuário e salva associação
        UsuarioEntity usuario = resolveUsuario(dto);
        persistGoogle(usuario, dto);

        String dispositivo = "oauth-google";
        String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";

        TokenEntity token = tokenService.gerarToken(usuario, TokenEnum.LOGIN, dispositivo, ip);
        return token.getValor();
    }

    private UsuarioEntity resolveUsuario(GoogleDTO dto) {
        return googleRepository.findByGoogleId(dto.getGoogleId())
                .map(GoogleEntity::getUsuario)
                .orElseGet(() -> usuarioService.buscarPorEmail(dto.getEmail())
                        .orElseGet(() -> usuarioService.criarUsuarioGoogle(dto)));
    }

    private void persistGoogle(UsuarioEntity usuario, GoogleDTO dto) {
        GoogleEntity entity = googleRepository.findByGoogleId(dto.getGoogleId())
                .orElse(GoogleEntity.builder().build());

        entity.setGoogleId(dto.getGoogleId());
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setPicture(dto.getPicture());
        entity.setLocale(dto.getLocale());
        entity.setUsuario(usuario);

        // Garante que o usuário vinculado esteja marcado como LOGIN via GOOGLE e ATIVO
        usuario.setOauth2(OAuth2Enum.GOOGLE);
        usuario.setStatus(UsuarioEnum.ATIVO);
        usuarioService.salvar(usuario);

        googleRepository.save(entity);
    }
}
