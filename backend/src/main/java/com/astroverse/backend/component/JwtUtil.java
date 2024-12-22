package com.astroverse.backend.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(long id, String email, String username, String nome, String cognome, boolean isAdmin) throws IllegalArgumentException, JWTCreationException {
        long expirationTime = 90L * 24 * 60 * 60 * 1000; // 3 mesi
        return JWT.create()
                .withSubject("User Data")
                .withClaim("id", id)
                .withClaim("email", email)
                .withClaim("username", username)
                .withClaim("nome", nome)
                .withClaim("cognome", cognome)
                .withClaim("isAdmin", isAdmin)
                .withIssuedAt(new Date())
                .withIssuer("Astroverse")
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));   //Algoritmo di hashing a 256 bit
    }

    public boolean isTokenValid(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("Astroverse") // Deve corrispondere all'issuer del token generato
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public static DecodedJWT JwtDecode(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT;
    }
}