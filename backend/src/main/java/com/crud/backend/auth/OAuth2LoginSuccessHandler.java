package com.crud.backend.auth;

import com.crud.backend.google.GoogleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);
    private final GoogleService googleService;

    public OAuth2LoginSuccessHandler(GoogleService googleService) {
        this.googleService = googleService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = null;
        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            token = googleService.handleOAuth2Login(oauthUser, request);

            if (token == null || token.isBlank()) {
                throw new RuntimeException("Token JWT não foi gerado.");
            }

            logger.info("OAuth2 login successful - tokenPrefix={}", token.substring(0, Math.min(16, token.length())));

            // Gera HTML mínimo que postMessage ao opener e fecha a popup.
            String escapedTokenForJs = escapeForJs(token);
            String encodedTokenForUrl = URLEncoder.encode(token, StandardCharsets.UTF_8);
            String frontendFallback = "http://localhost:5173/token/oauth-callback?token=" + encodedTokenForUrl;

            String html = "<!doctype html><html><head><meta charset='utf-8'><title>Autenticando...</title></head><body>"
                    + "<script>"
                    + "(function(){"
                    + "  try{"
                    + "    if(window.opener && typeof window.opener.postMessage === 'function') {"
                    + "      window.opener.postMessage({ type: 'oauth_token', token: '" + escapedTokenForJs + "', device: 'oauth-google' }, '*');"
                    + "    }"
                    + "  }catch(e){};"
                    + "  try{ window.close(); return; }catch(e){};"
                    + "  // fallback: abrir callback da SPA na mesma janela caso não seja possível fechar"
                    + "  window.location.href = '" + escapeForJs(frontendFallback) + "';"
                    + "})();"
                    + "</script>"
                    + "<p>Finalizando autenticação... Se nada acontecer, <a href=\"" + escapeForJs(frontendFallback) + "\">clique aqui</a>.</p>"
                    + "</body></html>";

            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(html);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error("Erro no OAuth2 success handler: {}", e.getMessage(), e);
            String msg = e.getMessage() != null ? e.getMessage() : "Erro ao processar login OAuth2";
            String redirect = "http://localhost:5173/token/oauth-callback?error=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
            try {
                response.sendRedirect(redirect);
            } catch (IOException ioe) {
                logger.error("Falha ao redirecionar após erro OAuth2: {}", ioe.getMessage(), ioe);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar login OAuth2");
            }
        }
    }

    // helper para escapar strings que serão injetadas em literais JS/HTML
    private static String escapeForJs(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
