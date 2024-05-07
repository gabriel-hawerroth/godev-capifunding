package capi.funding.api.services;

import capi.funding.api.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET_KEY = "capi.funding.secret-token";

    private User user;

    @InjectMocks
    private JwtService jwtService;
    @Mock
    private Algorithm algorithm;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@gmail.com");
    }

    @Test
    @DisplayName("generateToken - shouldn't accept null parameters")
    void testGenerateTokenShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                jwtService.generateToken(null, algorithm));

        assertThrows(IllegalArgumentException.class, () ->
                jwtService.generateToken(user, null));
    }

    @Test
    @DisplayName("generateToken - should generate the token")
    void testShouldGenerateTheToken() {
        final String token = assertDoesNotThrow(() ->
                jwtService.generateToken(user, Algorithm.HMAC256("secret_key")));

        assertEquals("capifunding-api", JWT.decode(token).getIssuer());
        assertEquals(user.getEmail(), JWT.decode(token).getSubject());
    }

    @Test
    @DisplayName("validateToken - shouldn't accept null paramaters")
    void testValidateTokenShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                jwtService.validateToken(null, "token"));

        assertThrows(IllegalArgumentException.class, () ->
                jwtService.validateToken(algorithm, null));
    }

    @Test
    @DisplayName("validateToken - should validate the token")
    void testShouldValidateTheToken() {
        final String token = jwtService.generateToken(user, Algorithm.HMAC256(SECRET_KEY));
        assertDoesNotThrow(() -> jwtService.validateToken(Algorithm.HMAC256(SECRET_KEY), token));
    }
}
