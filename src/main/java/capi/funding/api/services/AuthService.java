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
import lombok.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCrypt;

    private final TokenService tokenService;
    private final EmailService emailService;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, BCryptPasswordEncoder bCrypt, TokenService tokenService, EmailService emailService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.bCrypt = bCrypt;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.userService = userService;
    }

    public LoginResponseDTO doLogin(AuthenticationDTO authDTO) {
        final var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.email(), authDTO.password());

        final Authentication auth;
        try {
            auth = authenticationManager.authenticate(usernamePassword);
        } catch (BadCredentialsException ex) {
            throw new AuthException("bad credentials");
        } catch (DisabledException ex) {
            throw new AuthException("inactive user");
        }

        final String token = tokenService.generateToken((User) auth.getPrincipal());

        return new LoginResponseDTO((User) auth.getPrincipal(), token);
    }

    public User createNewUser(CreateUserDTO userDTO) {
        User user = userDTO.toUser();

        user.setPassword(bCrypt.encode(user.getPassword()));

        user = userService.save(user);

        sendActivateAccountEmail(user.getEmail(), user.getId());

        return user;
    }

    public void activateAccount(long userId, @NonNull String token) {
        final User user = userService.findById(userId);

        if (!bCrypt.matches(user.getEmail(), token)) {
            throw new WithoutPermissionException("invalid token");
        }

        user.setActive(true);

        userService.save(user);
    }

    private void sendActivateAccountEmail(@NonNull String userMail, long userId) {
        try {
            final String token = bCrypt.encode(userMail);
            final String mailContent = emailService.buildEmailTemplate(EmailType.ACTIVATE_ACCOUNT, userId, token);

            emailService.sendMail(
                    new EmailDTO(
                            userMail,
                            "Ativação da conta CapiFunding",
                            mailContent
                    )
            );
        } catch (AuthenticationFailedException authenticationFailedException) {
            userService.deleteById(userId);
            throw new EmailSendException("failed to authenticate to email");
        } catch (MessagingException messagingException) {
            userService.deleteById(userId);
            throw new EmailSendException("failed to send account activation email");
        }
    }
}
