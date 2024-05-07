package capi.funding.api.controllers;

import capi.funding.api.dto.EditUserDTO;
import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.entity.User;
import capi.funding.api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @GetMapping("/get-token-user")
    public ResponseEntity<User> getAuthUser() {
        return ResponseEntity.ok(
                service.getAuthUser()
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<User> changePassword(@RequestParam @Valid NewPasswordDTO newPassword) {
        return ResponseEntity.ok(
                service.changePassword(newPassword)
        );
    }

    @PutMapping
    public ResponseEntity<User> editUser(@RequestBody @Valid EditUserDTO editUserDTO) {
        return ResponseEntity.ok(
                service.editUser(editUserDTO)
        );
    }

    @PatchMapping("/change-image")
    public ResponseEntity<User> changeProfileImage(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(
                service.changeProfileImage(file)
        );
    }

    @PatchMapping("/remove-image")
    public ResponseEntity<User> removeProfileImage() {
        return ResponseEntity.ok(
                service.removeProfileImage()
        );
    }

    @GetMapping("/teste")
    public void teste() {
        service.teste(new User());
    }
}
