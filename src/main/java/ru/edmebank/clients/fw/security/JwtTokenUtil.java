package ru.edmebank.clients.fw.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtTokenUtil {

    private final SecretKey secretKey;

    public JwtTokenUtil(@Value("${jwt.secret:defaultSecretKey123456789012345678901234567890}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Невалидный JWT токен: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Не удалось извлечь данные из JWT токена: {}", e.getMessage());
            return null;
        }
    }

    public String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}