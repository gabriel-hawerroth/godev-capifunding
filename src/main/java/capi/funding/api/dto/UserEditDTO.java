package capi.funding.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserEditDTO(
        @NotNull
        @NotBlank
        @Size(max = 100)
        String name
) {
}
