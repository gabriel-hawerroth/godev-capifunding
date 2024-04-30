package capi.funding.api.services;

import capi.funding.api.dto.AuthenticationDTO;
import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.EmailDTO;
import capi.funding.api.dto.LoginResponseDTO;
import capi.funding.api.enums.EmailType;
import capi.funding.api.exceptions.AuthException;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.exceptions.UnsendedEmailException;
import capi.funding.api.exceptions.WithoutPermissionException;
import capi.funding.api.models.User;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.security.TokenService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCrypt;

    private final TokenService tokenService;
    private final EmailService emailService;

    private final UserRepository userRepository;

    public LoginResponseDTO doLogin(AuthenticationDTO authDTO) {
        final var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.email(), authDTO.password());

        final Authentication auth;
        try {
            auth = authenticationManager.authenticate(usernamePassword);
        } catch (BadCredentialsException ex) {
            throw new AuthException("Bad credentials");
        } catch (DisabledException ex) {
            throw new AuthException("Inactive user");
        }

        final String token = tokenService.generateToken((User) auth.getPrincipal());

        return new LoginResponseDTO((User) auth.getPrincipal(), token);
    }

    public User createNewUser(CreateUserDTO userDTO) {
        User user = userDTO.toUser();

        user.setPassword(bCrypt.encode(user.getPassword()));

        user = userRepository.save(user);

        sendActivateAccountEmail(user.getEmail(), user.getId());

        return user;
    }

    public void activateAccount(long userId, String token) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (!bCrypt.matches(user.getEmail(), token)) {
            throw new WithoutPermissionException("invalid token");
        }

        user.setActive(true);

        userRepository.save(user);
    }

    private void sendActivateAccountEmail(String userMail, Long userId) {
        try {
            final String token = bCrypt.encode(userMail);

            emailService.sendMail(
                    new EmailDTO(
                            userMail,
                            "Ativação da conta CapiFunding",
                            emailService.buildEmailTemplate(EmailType.ACTIVATE_ACCOUNT, userId, token)
                    )
            );
        } catch (AuthenticationFailedException authenticationFailedException) {
            userRepository.deleteById(userId);
            throw new UnsendedEmailException("failed to authenticate to email");
        } catch (MessagingException messagingException) {
            userRepository.deleteById(userId);
            throw new UnsendedEmailException("failed to send account activation email");
        }
    }
}
