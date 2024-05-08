package capi.funding.api.infra.handlers;

import capi.funding.api.dto.InvalidFieldsDTO;
import capi.funding.api.dto.ResponseError;
import capi.funding.api.infra.exceptions.*;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception ex) {
        return ResponseEntity.internalServerError().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<InvalidFieldsDTO>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getFieldErrors();

        return ResponseEntity.badRequest().body(
                errors.stream().map(InvalidFieldsDTO::new).toList()
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handlerMethodValidationException(HandlerMethodValidationException ex) {
        List<InvalidFieldsDTO> invalidFields = new LinkedList<>();

        for (ParameterValidationResult error : ex.getAllValidationResults()) {
            for (MessageSourceResolvable sourceResolvable : error.getResolvableErrors()) {
                invalidFields.add(
                        new InvalidFieldsDTO(
                                error.getMethodParameter().getParameterName(),
                                sourceResolvable.getDefaultMessage()
                        )
                );
            }
        }

        return ResponseEntity.badRequest().body(
                invalidFields
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<List<InvalidFieldsDTO>> validationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(
                ex.getErrors()
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseError> authException(AuthException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseError> notFoundException(NotFoundException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(WithoutPermissionException.class)
    public ResponseEntity<ResponseError> withoutPermissionException() {
        return ResponseEntity.status(403).body(
                new ResponseError("without permission to perform this action")
        );
    }

    @ExceptionHandler(TokenGenerateException.class)
    public ResponseEntity<ResponseError> tokenGenerateException(TokenGenerateException ex) {
        return ResponseEntity.internalServerError().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(InvalidParametersException.class)
    public ResponseEntity<ResponseError> invalidParametersException(InvalidParametersException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ResponseError> emailSendException(EmailSendException ex) {
        return ResponseEntity.internalServerError().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(MilestoneSequenceException.class)
    public ResponseEntity<ResponseError> milestoneSequenceException(MilestoneSequenceException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(ProjectEditabilityException.class)
    public ResponseEntity<ResponseError> projectEditabilityException(ProjectEditabilityException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }
}
