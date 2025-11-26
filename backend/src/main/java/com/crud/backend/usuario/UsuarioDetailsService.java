package com.crud.backend.usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        String senha = usuario.getSenha() != null ? usuario.getSenha() : "";

        // sem roles por enquanto; adaptar se precisar de autoridades
        List<GrantedAuthority> authorities = List.of();

        boolean enabled = usuario.getStatus() == UsuarioEnum.ATIVO;
        boolean accountNonLocked = usuario.getStatus() != UsuarioEnum.BLOQUEADO;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail() != null ? usuario.getEmail() : "")
                .password(senha)
                .authorities(authorities)
                .accountExpired(!accountNonExpired)
                .accountLocked(!accountNonLocked)
                .credentialsExpired(!credentialsNonExpired)
                .disabled(!enabled)
                .build();
    }
}
