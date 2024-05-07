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
public class JwtServiceTest {

    private static final String SECRET_KEY = "capi.funding.secret-token";

    User user;

    @InjectMocks
    JwtService jwtService;
    @Mock
    Algorithm algorithm;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@gmail.com");
    }

    @Test
    @DisplayName("generateToken - shouldn't accept null parameters")
    public void testGenerateTokenShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                jwtService.generateToken(null, algorithm));

        assertThrows(NullPointerException.class, () ->
                jwtService.generateToken(user, null));
    }

    @Test
    @DisplayName("generateToken - should generate the token")
    public void testShouldGenerateTheToken() {
        final String token = assertDoesNotThrow(() ->
                jwtService.generateToken(user, Algorithm.HMAC256("secret_key")));

        assertEquals("capifunding-api", JWT.decode(token).getIssuer());
        assertEquals(user.getEmail(), JWT.decode(token).getSubject());
    }

    @Test
    @DisplayName("validateToken - shouldn't accept null paramaters")
    void testValidateTokenShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                jwtService.validateToken(null, "token"));

        assertThrows(NullPointerException.class, () ->
                jwtService.validateToken(algorithm, null));
    }

    @Test
    @DisplayName("validateToken - should validate the token")
    void testShouldValidateTheToken() {
        final String token = jwtService.generateToken(user, Algorithm.HMAC256(SECRET_KEY));
        assertDoesNotThrow(() -> jwtService.validateToken(Algorithm.HMAC256(SECRET_KEY), token));
    }
}
