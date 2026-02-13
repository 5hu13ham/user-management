package in.trendsnag.user_management.security;

import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import in.trendsnag.user_management.model.User;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;
    // 1 hour validity
    private final long EXPIRATION = 60 * 60 * 1000;

    // Generate JWT token
    public String generateToken(Authentication authentication) {
    	
    	User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", user.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // Extract username/email/phone from token
    public String extractIdentifier(String token) {
        return extractClaims(token).getSubject();
    }

    // validate token
    public boolean isTokenValid(String token, String identifier) {
        String extracted = extractIdentifier(token);
        return (extracted.equals(identifier) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
