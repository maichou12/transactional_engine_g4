package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service pour la génération et la validation de tokens JWT.
 */
@Service
public class TokenService {

    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 heures

    @Value("${jwt.secret:MySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong}")
    private String jwtSecret;

    /**
     * Génère un token JWT pour un utilisateur.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
            .subject(user.getId())
            .claim("login", user.getLogin())
            .claim("telephone", user.getTelephone())
            .claim("authorities", user.getAuthorities())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    /**
     * Valide un token JWT.
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait l'ID de l'utilisateur depuis le token.
     */
    public String getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
