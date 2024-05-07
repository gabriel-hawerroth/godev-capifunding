package capi.funding.api.security;

import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.TokenGenerateException;
import capi.funding.api.services.JwtService;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtService jwtService;

    private final String secret;

    public TokenService(JwtService jwtService, @Value("${api.security.token.secret}") String secret) {
        this.jwtService = jwtService;
        this.secret = secret;
    }

    public String generateToken(@NonNull User user) {
        final Algorithm algorithm = Algorithm.HMAC256(secret);

        try {
            return jwtService.generateToken(user, algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerateException("error while generating token");
        }
    }

    public String validateToken(@NonNull String token) {
        final Algorithm algorithm = Algorithm.HMAC256(secret);

        try {
            return jwtService.validateToken(algorithm, token);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
