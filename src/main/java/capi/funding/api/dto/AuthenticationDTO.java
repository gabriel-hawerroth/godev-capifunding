package capi.funding.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(
        @NotNull
        @NotBlank
        String email,

        @NotNull
        @NotBlank
        String password
) {
}
