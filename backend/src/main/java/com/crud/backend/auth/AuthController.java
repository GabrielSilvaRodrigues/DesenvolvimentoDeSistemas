package com.crud.backend.auth;

import com.crud.backend.usuario.UsuarioEntity;
import com.crud.backend.usuario.UsuarioService;
import com.crud.backend.token.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    public AuthController(UsuarioService usuarioService, TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioEntity> register(@Valid @RequestBody UsuarioRegisterRequest req) {
        // monta entidade mínima
        UsuarioEntity u = new UsuarioEntity();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setSenha(req.senha());

        UsuarioEntity criado = usuarioService.cadastrar(u, req.dispositivo(), req.ip());
        return ResponseEntity.ok(criado);
    }

    /**
     * Ativa conta usando token enviado por e-mail.
     * Ex.: POST /auth/ativar?token=abc&dispositivo=web-1
     */
    @PostMapping("/ativar")
    public ResponseEntity<?> ativarConta(@RequestParam String token, @RequestParam String dispositivo) {
        try {
            UsuarioEntity usuario = usuarioService.ativarConta(token, dispositivo);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Logout: revoga token enviado no header Authorization (Bearer ...)
     * ou aceita token via query param "token". Também tenta req.logout() para encerrar sessão.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, @RequestParam(required = false) String token) {
        try {
            // 1) prefer header Authorization
            String auth = req.getHeader("Authorization");
            String valor = null;
            if (auth != null && auth.startsWith("Bearer ")) {
                valor = auth.substring(7);
            } else if (token != null && !token.isBlank()) {
                valor = token;
            }

            if (valor != null && !valor.isBlank()) {
                tokenService.revogarToken(valor);
            }

            // 2) encerra sessão servlet (se houver)
            try {
                req.logout();
            } catch (ServletException e) {
                // não fatal
            }

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao encerrar sessão: " + e.getMessage());
        }
    }

    /**
     * Logout do Google (frontend deve abrir esta URL em mesma janela ou popup).
     * O endpoint:
     *  - revoga o token local (se enviado via Authorization header ou query param 'token')
     *  - tenta req.logout() na sessão do servlet
     *  - retorna uma página HTML que navega para accounts.google.com/logout e depois redireciona ao frontend (returnTo)
     *
     * Uso: GET /auth/logout-google?returnTo=http://localhost:5173/
     */
    @GetMapping("/logout-google")
    public ResponseEntity<String> logoutGoogle(HttpServletRequest req, @RequestParam(required = false) String returnTo) {
        try {
            // revoga token se enviado via header Authorization ou query param token
            String auth = req.getHeader("Authorization");
            String valor = null;
            if (auth != null && auth.startsWith("Bearer ")) {
                valor = auth.substring(7);
            } else if (req.getParameter("token") != null && !req.getParameter("token").isBlank()) {
                valor = req.getParameter("token");
            }

            if (valor != null && !valor.isBlank()) {
                tokenService.revogarToken(valor);
            }

            // encerra sessão servlet (se houver)
            try {
                req.logout();
            } catch (ServletException e) {
                // não fatal
            }

            String frontend = (returnTo != null && !returnTo.isBlank()) ? returnTo : "http://localhost:5173/";
            // encode do frontend para incluir nos parâmetros de continue
            String encodedFront = URLEncoder.encode(frontend, StandardCharsets.UTF_8);

            // Monta URL de logout do Google que passa pelo appengine logout,
            // o que costuma limpar melhor a sessão e forçar prompt de login.
            // Exemplo:
            // https://accounts.google.com/logout?continue=https://appengine.google.com/_ah/logout?continue=<encodedFront>
            String logoutUrl = "https://accounts.google.com/logout?continue=https://appengine.google.com/_ah/logout?continue=" + encodedFront;

            // Escapa strings para injeção segura dentro do script JS
            String logoutUrlEscaped = escapeForJs(logoutUrl);
            String frontendEscaped = escapeForJs(frontend);

            // Página que pede ao browser para abrir o logout do Google (via logoutUrl)
            // e depois, se não redirecionar automaticamente, volta para o frontend.
            String html = "<!doctype html><html><head><meta charset='utf-8'><title>Sair</title></head><body>"
                    + "<script>"
                    + "try{"
                    + "  // abre fluxo de logout do Google que encadeia para appengine/_ah/logout e finalmente para o frontend"
                    + "  window.location.href = '" + logoutUrlEscaped + "';"
                    + "}catch(e){};"
                    + "setTimeout(function(){"
                    + "  // após breve espera, tenta garantir retorno ao frontend"
                    + "  window.location.href = '" + frontendEscaped + "';"
                    + "}, 2200);"
                    + "</script>"
                    + "<p>Encerrando sessão no Google... Se não for redirecionado, <a href='" + frontendEscaped + "'>clique aqui</a>.</p>"
                    + "</body></html>";

            return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(html);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao encerrar sessão Google: " + e.getMessage());
        }
    }

    // helper moved para nível de classe — usado para escapar strings injetadas no script HTML
    private static String escapeForJs(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
