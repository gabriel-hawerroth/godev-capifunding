package capi.funding.api.services;

import capi.funding.api.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {

    private static final String TOKEN_ISSUER = "capifunding-api";

    public String generateToken(@NonNull User user, @NonNull Algorithm algorithm) throws JWTCreationException {
        return JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withSubject(user.getEmail())
                .withExpiresAt(genExpirationDate())
                .sign(algorithm);
    }

    public String validateToken(@NonNull Algorithm algorithm, @NonNull String token) throws JWTVerificationException {
        return JWT.require(algorithm)
                .withIssuer(TOKEN_ISSUER)
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
