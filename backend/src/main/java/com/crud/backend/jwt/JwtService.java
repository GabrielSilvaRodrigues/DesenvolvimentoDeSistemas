package com.crud.backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import com.crud.backend.usuario.UsuarioEntity;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private static final String SECRET = "chaveSuperSecretaParaJWTDeveSerGrande123456789!@#";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(UsuarioEntity usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("email", usuario.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 dias
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }
}
