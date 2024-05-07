package capi.funding.api.services;

import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.AuthException;
import capi.funding.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserDetailServiceTest {

    @InjectMocks
    private UserDetailService userDetailService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("loadUserByUsername - should fetch the user from database")
    void testShouldFetchTheUserFromDatabase() {
        final String userMail = "test@gmail.com";

        when(userRepository.findByEmail(userMail)).thenReturn(Optional.of(new User()));

        userDetailService.loadUserByUsername(userMail);

        verify(userRepository).findByEmail(userMail);
    }

    @Test
    @DisplayName("loadUserByUsername - should throw AuthException when user is not found")
    void testShouldThrowAuthExceptionWhenUserIsNotFound() {
        final String userMail = "test@gmail.com";

        when(userRepository.findByEmail(userMail)).thenReturn(Optional.empty());

        final AuthException exception = assertThrows(AuthException.class, () ->
                userDetailService.loadUserByUsername(userMail));

        assertEquals("bad credentials", exception.getMessage());
    }
}
