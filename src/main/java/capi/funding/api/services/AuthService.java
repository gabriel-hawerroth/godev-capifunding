package capi.funding.api.services;

import capi.funding.api.dto.AuthenticationDTO;
import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.LoginResponseDTO;
import capi.funding.api.exceptions.AuthException;
import capi.funding.api.models.User;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.security.TokenService;
import jakarta.validation.Valid;
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

    private final UserRepository userRepository;

    public LoginResponseDTO doLogin(@Valid AuthenticationDTO authDTO) {
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
        final User user = userDTO.toUser();

        user.setPassword(bCrypt.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
