package com.example.authServerJwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;

@Component
public class JwtUtil {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    // Initialize the keys once
    static {
        try {
            // Generate the RSA Key Pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Use 2048-bit keys
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
 // Method to get the public key
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    
    // Generate a JWT token using the private key (signing)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                .signWith(privateKey, SignatureAlgorithm.RS256)  // Use RSA private key for signing
                .compact();
    }

    // Validate the JWT token
    public boolean validateToken(String token, String username) {
        String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // Extract the username from the JWT token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey) // Use the RSA public key for verification
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract the expiration date from the JWT token
    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey) // Use the RSA public key for verification
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // Get the username from the token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(publicKey) // Use the RSA public key for verification
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject();
    }

    // Validate the token with the public key
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)  // Use RSA public key for verification
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
