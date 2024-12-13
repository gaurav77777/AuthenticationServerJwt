package com.example.authServerJwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

	// private static final String SECRET_KEY = "my_secret_key"; // Example secret
	// key, better to store this securely
	// Generate a secure 256-bit key using the built-in method
	private static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// Generate a JWT token
	public String generateToken(String username) {
		// Convert the secret key string into a Key object
		System.out.println("inside generate Token");
		// Key key = new SecretKeySpec(SECRET_KEY.getBytes(),
		// SignatureAlgorithm.HS256.getJcaName());

		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
				.signWith(key, SignatureAlgorithm.HS256) // Use the Key object
				.compact();
	}

	// Validate the JWT token

	public boolean validateToken(String token, String username) {
		String tokenUsername = extractUsername(token);
		return (tokenUsername.equals(username) && !isTokenExpired(token));
	}

	// Extract the username from the JWT token
	/*
	 * public String extractUsername(String token) { // Use JwtParserBuilder to
	 * parse the token return Jwts.parserBuilder()
	 * .setSigningKey(SECRET_KEY.getBytes()) // Use the bytes of the secret key
	 * .build() .parseClaimsJws(token) .getBody() .getSubject(); }
	 */
	public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Use the generated secure key for parsing
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

	// Check if the token is expired
	/*
	 * private boolean isTokenExpired(String token) { return
	 * extractExpiration(token).before(new Date()); }
	 */
	private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

	// Extract the expiration date from the JWT token
	/*
	 * private Date extractExpiration(String token) { return Jwts.parserBuilder()
	 * .setSigningKey(SECRET_KEY.getBytes()) // Use the bytes of the secret key
	 * .build() .parseClaimsJws(token) .getBody() .getExpiration(); }
	 */
	private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Use the generated secure key for parsing
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
	
	
	// Get the username from the token
	/*
	 * public String getUsernameFromToken(String token) { Claims claims =
	 * Jwts.parserBuilder() .setSigningKey(SECRET_KEY.getBytes()) .build()
	 * .parseClaimsJws(token) .getBody(); return claims.getSubject(); }
	 */
	public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key) // Use the generated secure key for parsing
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }

	/*
	 * public boolean validateToken(String token) { try {
	 * Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().
	 * parseClaimsJws(token); return true; } catch (Exception ex) { return false; }
	 * }
	 */

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key) // Use the same key
					.build().parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
