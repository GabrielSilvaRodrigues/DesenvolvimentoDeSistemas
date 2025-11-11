package com.crud.backend.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario cadastrar(Usuario usuario){
        return usuarioRepository.save(usuario);
    }
    public Usuario atualizar(Usuario usuario){
        autenticar(usuario);
        return usuarioRepository.save(usuario);
    }
    public Usuario autenticar(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    // coloquei desativar, para ele "deletar" a conta depois de 30 dias
    // (mandar para a table excluidos junto de tudo que tenha esse id, em arquivo json)
    public Usuario desativar(Usuario usuario){
        autenticar(usuario);
        return usuarioRepository.save(usuario);
    }

    public Usuario ativar(Usuario usuario){
        autenticar(usuario);
        return usuarioRepository.save(usuario);
    }
}