package com.javaclass.game.utility;

import com.javaclass.game.constants.AuthDefiner;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtility {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private Date buildExpirationTime() {
        return Date.from(
            LocalDateTime.now()
                .plusHours(AuthDefiner.TOKEN_VALID_HOURS)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );
    }

    public String generateToken(Long adminId, String role) {
        return Jwts.builder()
            .claim("admin_id", adminId)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(buildExpirationTime())
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String generatePlayerToken(Long playerId) {
        return Jwts.builder()
            .claim("player_id", playerId)
            .setIssuedAt(new Date())
            .setExpiration(buildExpirationTime())
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    private static final String BEARER_PREFIX = "Bearer ";

    public String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public Long extractAdminId(String token) {
        Claims claims = parseToken(token);
        return claims.get("admin_id", Long.class);
    }

    public String extractRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    public Long extractPlayerId(String token) {
        Claims claims = parseToken(token);
        return claims.get("player_id", Long.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception exception) {
            return true;
        }
    }
}