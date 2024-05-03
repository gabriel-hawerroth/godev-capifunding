package capi.funding.api.dto;

import org.springframework.validation.FieldError;

public record InvalidFieldsDTO(
        String field,
        String message
) {
    public InvalidFieldsDTO(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
