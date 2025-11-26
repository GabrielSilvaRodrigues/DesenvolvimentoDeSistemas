package com.crud.backend.security;

import com.crud.backend.google.GoogleService;
import com.crud.backend.auth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2LoginSuccessHandler oauth2SuccessHandler) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())

            // evita redirect para login em APIs: retorna 401 em vez de redirect
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            )

            .authorizeHttpRequests(auth -> auth
                // permite preflight e endpoints públicos
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/auth/**",
                        "/oauth2/**",          // permit OAuth2 endpoints
                        "/login/oauth2/**",
                        "/api/token/**",
                        "/api/viacep/**",
                        "/api/google/auth",
                        "/api/email/enviar",
                        "/api/usuario/email/**",
                        "/api/endereco",
                        "/api/endereco/**"
                ).permitAll()

                // qualquer outra rota exige login
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oauth2SuccessHandler) // usa nosso handler para processar o usuário e gerar token
            );

        // desativa o logout padrão do Spring Security (evita redirects inesperados)
        http.logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    public OAuth2LoginSuccessHandler oauth2LoginSuccessHandler(GoogleService googleService) {
        return new OAuth2LoginSuccessHandler(googleService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // permitimos as portas de dev mais comuns (Vite 5173 e create-react-app 3000)
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:3000",
            "https://meusitefrontend.com"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // inclui os headers usados no projeto (X-Device-Id / X-User-Id)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "id", "device", "X-Device-Id", "X-User-Id"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // RestTemplate necessário para ViaCepService
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // adiciona PasswordEncoder usado por UsuarioService e autenticação
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
