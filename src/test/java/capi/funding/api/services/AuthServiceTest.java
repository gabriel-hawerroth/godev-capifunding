package capi.funding.api.services;

import capi.funding.api.dto.AuthenticationDTO;
import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.EmailDTO;
import capi.funding.api.dto.LoginResponseDTO;
import capi.funding.api.entity.User;
import capi.funding.api.enums.EmailType;
import capi.funding.api.infra.exceptions.AuthException;
import capi.funding.api.infra.exceptions.EmailSendException;
import capi.funding.api.infra.exceptions.WithoutPermissionException;
import capi.funding.api.security.TokenService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private AuthenticationDTO authDTO;
    private CreateUserDTO createUserDTO;

    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BCryptPasswordEncoder bCrypt;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserService userService;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        authDTO = new AuthenticationDTO(
                "test@gmail.com",
                "Testing#01"
        );

        createUserDTO = new CreateUserDTO(
                "test@gmail.com",
                "Testing#01",
                "test"
        );
    }

    @Test
    @DisplayName("doLogin - should call authentication manager")
    void testShouldCallAuthenticationManager() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        authService.doLogin(authDTO);

        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("doLogin - should generate and return the token")
    void testShouldGenerateAndReturnTheToken() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateToken(any())).thenReturn("tokenTest");

        final LoginResponseDTO loginDTO = authService.doLogin(authDTO);

        verify(tokenService).generateToken(any());
        assertEquals("tokenTest", loginDTO.token());
    }

    @Test
    @DisplayName("doLogin - should throw auth exception with correct message when sended bad credentials")
    void testShouldThrowAuthExceptionWithCorrectMessageWhenSendedBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        AuthException exception = assertThrows(AuthException.class, () ->
                authService.doLogin(authDTO));

        assertEquals("bad credentials", exception.getMessage());
    }

    @Test
    @DisplayName("doLogin - should throw auth exception with correct message when user is inactive")
    void testShouldThrowAuthExceptionWithCorrectMessageWhenUserIsInactive() {
        when(authenticationManager.authenticate(any())).thenThrow(DisabledException.class);

        AuthException exception = assertThrows(AuthException.class, () ->
                authService.doLogin(authDTO));

        assertEquals("inactive user", exception.getMessage());
    }

    @Test
    @DisplayName("createNewUser - should encrypt the password")
    void testShouldEncryptThePassword() {
        final User user = createUserDTO.toUser();
        user.setId(1L);

        when(userService.save(any(User.class))).thenReturn(user);

        authService.createNewUser(createUserDTO);

        verify(bCrypt, times(2)).encode(any());
    }

    @Test
    @DisplayName("createNewUser - should send the activate account mail")
    void testShouldSendTheActivateAccountMail() throws MessagingException {
        final User user = createUserDTO.toUser();
        user.setId(1L);

        when(userService.save(any(User.class))).thenReturn(user);
        when(bCrypt.encode(anyString())).thenReturn("encryptedString");

        authService.createNewUser(createUserDTO);

        verify(emailService).sendMail(any(EmailDTO.class));
        verify(emailService).buildEmailTemplate(any(EmailType.class), anyLong(), anyString());
    }

    @Test
    @DisplayName("createNewUser - shouldn't accept null user mail")
    void testShoulntAcceptNullUserMail() {
        final User user = createUserDTO.toUser();
        user.setId(1L);
        user.setEmail(null);

        when(userService.save(any(User.class))).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () ->
                authService.createNewUser(createUserDTO));
    }

    @Test
    @DisplayName("sendActivateAccountEmail - should delete the created user when sendMail throw AuthenticationFailedException")
    void testShouldDeleteTheCreatedUserWhenSendMailThrowAuthenticationFailedException() throws MessagingException {
        final User user = createUserDTO.toUser();
        user.setId(1L);

        when(userService.save(any(User.class))).thenReturn(user);
        when(bCrypt.encode(anyString())).thenReturn("encryptedString");

        doThrow(new AuthenticationFailedException()).when(emailService).sendMail(any(EmailDTO.class));

        assertThrows(EmailSendException.class, () -> authService.createNewUser(createUserDTO));

        verify(userService).deleteById(anyLong());
    }

    @Test
    @DisplayName("sendActivateAccountEmail - should delete the created user when sendMail throw MessagingException")
    void testShouldDeleteTheCreatedUserWhenSendMailThrowMessagingException() throws MessagingException {
        final User user = createUserDTO.toUser();
        user.setId(1L);

        when(userService.save(any(User.class))).thenReturn(user);
        when(bCrypt.encode(anyString())).thenReturn("encryptedString");

        doThrow(new MessagingException()).when(emailService).sendMail(any(EmailDTO.class));

        assertThrows(EmailSendException.class, () -> authService.createNewUser(createUserDTO));

        verify(userService).deleteById(anyLong());
    }

    @Test
    @DisplayName("activateAccount - shouldn't accept null token")
    void testShouldntAcceptNullToken() {
        assertThrows(IllegalArgumentException.class, () ->
                authService.activateAccount(1, null));
    }

    @Test
    @DisplayName("activateAccount - should throw exception token doesn't match with the encrypted mail")
    void testShouldVerifyIfTokenMatchesWithTheEncryptedMail() {
        final long userId = 1;
        final String token = "tokenTest";

        when(userService.findById(userId)).thenReturn(createUserDTO.toUser());
        when(bCrypt.matches(createUserDTO.email(), token)).thenReturn(false);

        WithoutPermissionException exception = assertThrows(WithoutPermissionException.class, () ->
                authService.activateAccount(userId, token));

        verify(bCrypt).matches(createUserDTO.email(), token);
        assertEquals("invalid token", exception.getMessage());
    }

    @Test
    @DisplayName("activateAccount - should active the user")
    void testShouldActiveTheUser() {
        when(userService.findById(anyLong())).thenReturn(createUserDTO.toUser());
        when(bCrypt.matches(anyString(), anyString())).thenReturn(true);

        authService.activateAccount(1, "tokenTest");

        verify(userService).save(userCaptor.capture());

        final User user = userCaptor.getValue();
        assertTrue(user.isActive());
    }
}
