package capi.funding.api.entity;

import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.EditUserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    @DisplayName("user - no args constructor")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> new User());
    }

    @Test
    @DisplayName("user - all args constructor")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> new User(
                1L,
                "test@gmail.com",
                "encryptedPassword",
                "testing",
                true,
                LocalDateTime.now().minusDays(528),
                null
        ));
    }

    @Test
    @DisplayName("user - CreateUserDTO constructor")
    void testCreateUserDTOConstructor() {
        final CreateUserDTO createUserDTO = new CreateUserDTO(
                "test@gmail.com",
                "Testing#01",
                "testing"
        );
        assertDoesNotThrow(() -> new User(createUserDTO));
    }

    @Test
    @DisplayName("user - getters and setters")
    void testGettersAndSetters() {
        final LocalDateTime now = LocalDateTime.now();
        final byte[] profileImage = new byte[1000];
        final User user = new User();

        assertDoesNotThrow(() -> {
            user.setId(1L);
            user.setEmail("test@gmail.com");
            user.setPassword("encryptedPassword");
            user.setName("testing");
            user.setActive(true);
            user.setCreation_date(now);
            user.setProfile_image(profileImage);
        });

        assertAll("test getters",
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("test@gmail.com", user.getEmail()),
                () -> assertEquals("encryptedPassword", user.getPassword()),
                () -> assertEquals("testing", user.getName()),
                () -> assertTrue(user.isActive()),
                () -> assertEquals(now, user.getCreation_date()),
                () -> assertEquals(profileImage, user.getProfile_image())
        );
    }

    @Test
    @DisplayName("user - update values")
    void testUpdateValues() {
        final User user = new User(
                1L,
                "test@gmail.com",
                "encryptedPassword",
                "testing",
                true,
                LocalDateTime.now().minusDays(528),
                null
        );

        final EditUserDTO editUserDTO = new EditUserDTO(
                "new user name"
        );

        assertDoesNotThrow(() -> user.updateValues(editUserDTO));

        assertEquals("new user name", user.getName());
    }

    @Test
    @DisplayName("getAuthorities - returns an empty list")
    void testGetAuthoritiesReturnAnEmptyList() {
        final User user = new User();

        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("user - getUsername should return email")
    void testGetUsernameShouldReturnEmail() {
        final User user = new User();
        user.setEmail("test@gmail.com");

        assertEquals("test@gmail.com", user.getUsername());
    }

    @Test
    @DisplayName("user - isAccountNonExpired should return true")
    void testIsAccountNonExpiredShouldReturnTrue() {
        final User user = new User();

        assertTrue(user.isAccountNonExpired());
    }

    @Test
    @DisplayName("user - isAccountNonLocked should return true")
    void testIsAccountNonLockedShouldReturnTrue() {
        final User user = new User();

        assertTrue(user.isAccountNonLocked());
    }

    @Test
    @DisplayName("user - isCredentialsNonExpired should return true")
    void testIsCredentialsNonExpiredShouldReturnTrue() {
        final User user = new User();

        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("user - isEnabled should return active attribute")
    void testIsEnabledShouldReturnActiveAttribute() {
        final User user = new User();

        user.setActive(true);
        assertTrue(user.isEnabled());

        user.setActive(false);
        assertFalse(user.isEnabled());
    }
}
