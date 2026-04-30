package com.javaclass.game.utility;

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

	private static final int TOKEN_VALID_HOURS = 8;

	@Value("${jwt.secret}")
	private String jwtSecret;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateToken(Long adminId, String role) {
		Date issuedAt = new Date();
		Date expirationTime = Date
				.from(LocalDateTime.now().plusHours(TOKEN_VALID_HOURS).atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder().claim("admin_id", adminId).claim("role", role).setIssuedAt(issuedAt)
				.setExpiration(expirationTime).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public Claims parseToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public Long extractAdminId(String token) {
		Claims claims = parseToken(token);
		return claims.get("admin_id", Long.class);
	}

	public String extractRole(String token) {
		Claims claims = parseToken(token);
		return claims.get("role", String.class);
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
