package capi.funding.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewPasswordDTO(
        @NotNull
        @NotBlank
        @Size(min = 8, message = "a senha deve conter pelo menos 8 caracteres")
        @Pattern(regexp = ".*[a-z].*", message = "a senha deve conter pelo menos 1 letra minúscula")
        @Pattern(regexp = ".*[A-Z].*", message = "a senha deve conter pelo menos 1 letra maiúscula")
        @Pattern(regexp = ".*\\d.*", message = "a senha deve conter pelo menos 1 número")
        @Pattern(regexp = ".*[!@#$%^&*()_+{}\\[\\]:;,.<>/?~\\\\].*", message = "a senha deve conter pelo menos 1 caracter especial")
        String newPassword
) {
}
