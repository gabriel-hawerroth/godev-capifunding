package capi.funding.api.infra.exceptions;

import capi.funding.api.dto.InvalidFieldsDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final transient List<InvalidFieldsDTO> errors;

    public ValidationException(List<InvalidFieldsDTO> errors) {
        this.errors = errors;
    }
}
