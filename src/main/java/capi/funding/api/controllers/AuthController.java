package capi.funding.api.controllers;

import capi.funding.api.dto.AuthenticationDTO;
import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.LoginResponseDTO;
import capi.funding.api.models.User;
import capi.funding.api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authDTO) {
        return ResponseEntity.ok(
                authService.doLogin(authDTO)
        );
    }

    @PostMapping("/new-user")
    public ResponseEntity<User> createNewUser(@RequestBody @Valid CreateUserDTO user) {
        return ResponseEntity.status(201).body(
                authService.createNewUser(user)
        );
    }

    @GetMapping("/activate-account/{userId}/{token}")
    public ResponseEntity<Void> activateAccount(@PathVariable long userId, @PathVariable String token) {
        authService.activateAccount(userId, token);

        final URI uri = URI.create("http://localhost:4200/ativacao-da-conta");

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(uri)
                .build();
    }
}
