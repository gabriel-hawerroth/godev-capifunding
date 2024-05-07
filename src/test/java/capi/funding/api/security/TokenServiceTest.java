package capi.funding.api.security;

import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.TokenGenerateException;
import capi.funding.api.services.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final String SECRET_KEY = "capi.funding.secret-token";
    private static final String TOKEN_ISSUER = "capifunding-api";

    final User user = new User(
            1L,
            "test@gmail.com",
            "encryptedPassword",
            "testing",
            true,
            LocalDateTime.now().minusDays(82),
            null
    );

    private final TokenService tokenService;

    private final JwtService jwtService;

    public TokenServiceTest() {
        this.jwtService = mock(JwtService.class);
        this.tokenService = new TokenService(this.jwtService, SECRET_KEY);
    }

    @Test
    @DisplayName("generateToken - shouldn't accept null parameters")
    void testGenerateTokenShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                tokenService.generateToken(null));
    }

    @Test
    @DisplayName("generateToken - should throw TokenGenerateException when occurs an error while generating token")
    void testGenerateTokenShouldThrowTokenGenerateExceptionWhenOccursAnErrorWhileGeneratingToken() {
        doThrow(JWTCreationException.class).when(jwtService).generateToken(any(User.class), any(Algorithm.class));

        assertThrows(TokenGenerateException.class, () ->
                tokenService.generateToken(user));
    }

    @Test
    @DisplayName("generateToken - should generate the token")
    void testShouldGenerateTheToken() {
        when(jwtService.generateToken(any(User.class), any(Algorithm.class)))
                .thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjYXBpZnVuZGluZy1hcGkiLCJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTcxNTAyOTkwMn0.tRN5d-keE4XcOnMeUjnfv_-gmhztFxuQ6NcEKu8moo0");

        final String token = assertDoesNotThrow(() -> tokenService.generateToken(user));

        assertEquals(user.getEmail(), JWT.decode(token).getSubject());
        assertEquals(TOKEN_ISSUER, JWT.decode(token).getIssuer());
    }

    @Test
    @DisplayName("validateToken - shouldn't accept null parameters")
    void testValidateTokenShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                tokenService.validateToken(null));
    }

    @Test
    @DisplayName("validateToken - invalid token should return null")
    void testInvalidTokenShouldReturnNull() {
        final String token = "tokenTest";

        doThrow(JWTVerificationException.class).when(jwtService).validateToken(any(Algorithm.class), anyString());

        assertNull(tokenService.validateToken(token));
    }

    @Test
    @DisplayName("validateToken - should validate the token")
    void testShoulValidateTheToken() {
        final String token = "tokenTest";
        tokenService.validateToken(token);

        verify(jwtService).validateToken(any(Algorithm.class), anyString());
    }
}





