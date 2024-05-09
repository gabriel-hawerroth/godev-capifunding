package capi.funding.api.dto;

import capi.funding.api.entity.User;
import jakarta.validation.constraints.*;

public record CreateUserDTO(
        @NotNull
        @NotBlank
        @Email
        String email,

        @NotNull
        @NotBlank
        @Size(min = 8, message = "a senha deve conter pelo menos 8 caracteres")
        @Pattern(regexp = ".*[a-z].*", message = "a senha deve conter pelo menos 1 letra minúscula")
        @Pattern(regexp = ".*[A-Z].*", message = "a senha deve conter pelo menos 1 letra maiúscula")
        @Pattern(regexp = ".*\\d.*", message = "a senha deve conter pelo menos 1 número")
        @Pattern(regexp = ".*[!@#$%^&*()_+{}\\[\\]:;,.<>/?~\\\\].*", message = "a senha deve conter pelo menos 1 caracter especial")
        String password,

        @NotNull
        @NotBlank
        @Size(max = 100)
        String name
) {
    public User toUser() {
        return new User(this);
    }
}
