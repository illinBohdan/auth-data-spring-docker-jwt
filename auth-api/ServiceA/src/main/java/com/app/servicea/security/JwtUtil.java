package com.app.servicea.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    // 1. Отримання секретного ключа з application.yml
    // Ключ має бути достатньо довгим (мінімум 256 біт)
    @Value("${jwt.secret}")
    private String secret;

    // 2. Встановлюємо час життя токена (наприклад, 24 години)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; //24 години

    // 3. Допоміжний метод для отримання Key об'єкта
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }

    /**
     * Генерує JWT-токен для успішно аутентифікованого користувача.
     * @param userId UUID користувача
     * @return Згенерований JWT-токен
     */
    public String generateToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();

        // Встановлюємо додаткові дані (Claims), тут - ID користувача
        claims.put("userId", userId.toString());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString()) // Головний ідентифікатор токена
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Валідує токен і повертає Claims, якщо він дійсний.
     * @param token JWT-токен
     * @return Claims (вміст токена)
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // У разі недійсності, закінчення терміну дії, або помилки підпису
            // буде викинуто виняток.
            System.err.println("JWT Validation Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Витягує ID користувача з токена.
     * @param token JWT-токен
     * @return UUID користувача
     */
    public UUID extractUserId(String token) {
        Claims claims = validateToken(token);
        if (claims != null && claims.containsKey("userId")) {
            return UUID.fromString(claims.get("userId").toString());
        }
        return null;
    }

}
