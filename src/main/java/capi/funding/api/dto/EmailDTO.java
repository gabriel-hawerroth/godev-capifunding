package capi.funding.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(
        @Email
        @NotBlank
        String address,

        @NotBlank
        String subject,

        @NotBlank
        String content
) {
}
