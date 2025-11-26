package com.crud.backend.usuario;

import com.crud.backend.email.EmailDTO;
import com.crud.backend.email.EmailService;
import com.crud.backend.google.GoogleDTO;
import com.crud.backend.token.TokenEntity;
import com.crud.backend.token.TokenEnum;
import com.crud.backend.token.TokenService;
import com.crud.backend.github.GithubDriveService;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final GithubDriveService githubDriveService; // novo

    // ============================================================
    // CADASTRAR USUÁRIO + ENVIAR TOKEN DE ATIVAÇÃO
    // ============================================================
    public UsuarioEntity cadastrar(UsuarioEntity usuario, String dispositivo, String ip) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já está em uso.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setStatus(UsuarioEnum.PENDENTE);

        UsuarioEntity salvo = usuarioRepository.save(usuario);

        // cria token de ativação
        TokenEntity token = tokenService.gerarToken(
                salvo, TokenEnum.AUTENTICACAO, dispositivo, ip
        );

        // envia e-mail (mesma lógica)
        emailService.enviarEmail(token, new EmailDTO(
                salvo.getEmail(),
                "Confirme seu cadastro",
                "",
                "fatecmeets@gmail.com",
                "Fatec Meets"
        ));

        // cria "pasta" no GitHub para o usuário (envia .gitkeep dentro de "<id>/perfil/")
        try {
            String gitkeepPath = salvo.getId() + "/perfil/.gitkeep";
            byte[] placeholder = "keep".getBytes();
            githubDriveService.uploadBytes(placeholder, gitkeepPath, "create user folders for user " + salvo.getId());
        } catch (Exception ex) {
            // não bloquear fluxo de cadastro -- apenas logar/ignorar
            // se preferir, lance runtime para forçar rollback do cadastro
            System.err.println("Aviso: falha ao criar pasta GitHub para usuário " + salvo.getId() + " -> " + ex.getMessage());
        }

        return salvo;
    }

    // ============================================================
    // ATIVAR CONTA VIA TOKEN
    // ============================================================
    public UsuarioEntity ativarConta(String tokenValor, String dispositivo) {

        TokenEntity token = tokenService.validarToken(tokenValor, dispositivo)
                .orElseThrow(() -> new RuntimeException("Token inválido."));

        UsuarioEntity usuario = token.getUsuario();
        usuario.setStatus(UsuarioEnum.ATIVO);

        // revoga token
        tokenService.revogarToken(tokenValor);

        return usuarioRepository.save(usuario);
    }

    // ============================================================
    // LOGIN (GERA TOKEN DE LOGIN + ENVIA E-MAIL)
    // ============================================================
    public UsuarioEntity autenticar(String email, String senha, String dispositivo, String ip) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }

        if (usuario.getStatus() != UsuarioEnum.ATIVO) {
            throw new RuntimeException("Conta não está ativa.");
        }

        // Gera token de login
        TokenEntity token = tokenService.gerarToken(
                usuario, TokenEnum.LOGIN, dispositivo, ip
        );

        // e-mail informando login
        emailService.enviarEmail(token, new EmailDTO(
                usuario.getEmail(),
                "Novo login detectado",
                "",
                "fatecmeets@gmail.com",
                "Fatec Meets"
        ));

        return usuario;
    }

    // ============================================================
    // INICIAR RECUPERAÇÃO DE SENHA
    // ============================================================
    public void solicitarRecuperacao(String email, String dispositivo, String ip) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        TokenEntity token = tokenService.gerarToken(
                usuario, TokenEnum.RECUPERACAO_SENHA, dispositivo, ip
        );

        emailService.enviarEmail(token, new EmailDTO(
                usuario.getEmail(),
                "Recuperação de senha",
                "",
                "fatecmeets@gmail.com",
                "Fatec Meets"
        ));
    }

    // ============================================================
    // FINALIZAR RECUPERAÇÃO DE SENHA
    // ============================================================
    public UsuarioEntity redefinirSenha(String tokenValor, String novaSenha, String dispositivo) {

        TokenEntity token = tokenService.validarToken(tokenValor, dispositivo)
                .orElseThrow(() -> new RuntimeException("Token inválido."));

        UsuarioEntity usuario = token.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));

        // desativa o token
        tokenService.revogarToken(tokenValor);

        return usuarioRepository.save(usuario);
    }

    // ============================================================
    // ATUALIZAR PERFIL
    // ============================================================
    public UsuarioEntity atualizar(Long id, UsuarioEntity novosDados) {

        UsuarioEntity usuario = buscarPorId(id);

        usuario.setNome(novosDados.getNome());

        if (novosDados.getSenha() != null && !novosDados.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(novosDados.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }

    // ============================================================
    // DESATIVAR (SOFT DELETE)
    // ============================================================
    public UsuarioEntity desativar(Long id) {
        UsuarioEntity usuario = buscarPorId(id);
        usuario.setStatus(UsuarioEnum.INATIVO);
        return usuarioRepository.save(usuario);
    }

    // ============================================================
    // ATIVAR (ADMIN)
    // ============================================================
    public UsuarioEntity reativar(Long id) {
        UsuarioEntity usuario = buscarPorId(id);
        usuario.setStatus(UsuarioEnum.ATIVO);
        return usuarioRepository.save(usuario);
    }

    // ============================================================
    // BUSCAR POR ID (interno)
    // ============================================================
    public UsuarioEntity buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Optional<UsuarioEntity> buscarPorEmail(String email) {
        if (email == null) return Optional.empty();
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Cria um usuário "somente Google": todos os campos de perfil null,
     * status = ATIVO e oauth2 = GOOGLE.
     */
    public UsuarioEntity criarUsuarioGoogle(GoogleDTO dto) {
        UsuarioEntity usuario = UsuarioEntity.builder()
                // ...todos os campos de perfil intencionalmente null...
                .nome(null)
                .email(null)
                .senha(null)
                // garante status e oauth2 conforme requerido
                .status(UsuarioEnum.ATIVO)
                .oauth2(OAuth2Enum.GOOGLE)
                .build();

        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity salvar(UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Inicia login com verificação em duas etapas: valida credenciais e envia token por e‑mail.
     * Retorna o TokenEntity gerado (valor do token será enviado por e‑mail também).
     */
    public TokenEntity iniciarLogin2FA(String email, String senha, String dispositivo, String ip) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha inválida.");
        }

        if (usuario.getStatus() != UsuarioEnum.ATIVO) {
            throw new RuntimeException("Conta não está ativa.");
        }

        // se a conta foi criada via OAuth2, force uso do provedor
        if (usuario.getOauth2() != null && usuario.getOauth2() != OAuth2Enum.FALSE) {
            throw new RuntimeException("Conta registrada via provedor OAuth2. Use o provedor para autenticar.");
        }

        // Gera token temporário para verificação (usando TokenEnum.LOGIN)
        TokenEntity token = tokenService.gerarToken(usuario, TokenEnum.LOGIN, dispositivo, ip);

        // Envia e‑mail pedindo confirmação do login
        emailService.enviarEmail(token, new EmailDTO(
                usuario.getEmail(),
                "Confirme seu login",
                "",
                "fatecmeets@gmail.com",
                "Fatec Meets"
        ));

        return token;
    }

    /**
     * Confirma o login após o usuário clicar/no inserir o token recebido por e‑mail.
     * Valida e revoga o token temporário e retorna o UsuarioEntity autenticado.
     */
    public UsuarioEntity confirmarLogin2FA(String tokenValor, String dispositivo) {

        TokenEntity token = tokenService.validarToken(tokenValor, dispositivo)
                .orElseThrow(() -> new RuntimeException("Token inválido."));

        UsuarioEntity usuario = token.getUsuario();

        // revoga o token temporário
        tokenService.revogarToken(tokenValor);

        // opcional: gerar token de sessão definitivo se desejar
        // TokenEntity session = tokenService.gerarToken(usuario, TokenEnum.LOGIN, dispositivo, token.getIp());
        // return usuarioRepository.save(usuario);

        return usuario;
    }
}
