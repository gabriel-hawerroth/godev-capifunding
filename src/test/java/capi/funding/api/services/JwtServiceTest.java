package capi.funding.api.services;

import capi.funding.api.entity.User;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    String issuer;
    User user;
    Algorithm algorithm;

    @InjectMocks
    JwtService jwtService;

    @BeforeEach
    void setUp() {
        issuer = "issuer";

        user = new User();
        user.setEmail("test@gmail.com");

        algorithm = mock(Algorithm.class);
    }

    @Test
    @DisplayName("generateToken - shouldn't accept null parameters")
    public void testShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                jwtService.generateToken(null, user, algorithm));

        assertThrows(NullPointerException.class, () ->
                jwtService.generateToken(issuer, null, algorithm));

        assertThrows(NullPointerException.class, () ->
                jwtService.generateToken(issuer, user, null));
    }

    @Test
    @DisplayName("generateToken - should generate the token")
    public void testShouldGenerateTheToken() {
        assertDoesNotThrow(() ->
                jwtService.generateToken("issuer", user, Algorithm.HMAC256("secret_key")));
    }
}
