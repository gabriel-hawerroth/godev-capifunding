package capi.funding.api.controllers;

import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.dto.UserEditDTO;
import capi.funding.api.models.User;
import capi.funding.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/get-token-user")
    private ResponseEntity<User> getTokenUser() {
        return ResponseEntity.ok(
                service.getTokenUser()
        );
    }

    @PutMapping("/change-password")
    private ResponseEntity<User> changePassword(@RequestParam @Valid NewPasswordDTO newPassword) {
        return ResponseEntity.ok(
                service.changePassword(newPassword)
        );
    }

    @PutMapping("/{id}")
    private ResponseEntity<User> editUser(@RequestBody @Valid UserEditDTO userEditDTO) {
        return ResponseEntity.ok(
                service.editUser(userEditDTO)
        );
    }

    @PatchMapping("/change-image")
    private ResponseEntity<User> changeProfileImage(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(
                service.changeProfileImage(file)
        );
    }

    @PatchMapping("/remove-image")
    private ResponseEntity<User> removeProfileImage() {
        return ResponseEntity.ok(
                service.removeProfileImage()
        );
    }
}
