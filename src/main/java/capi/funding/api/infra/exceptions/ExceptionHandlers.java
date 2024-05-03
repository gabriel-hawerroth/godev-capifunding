package capi.funding.api.infra.exceptions;

import capi.funding.api.dto.InvalidFieldsDTO;
import capi.funding.api.dto.ResponseError;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception ex) {
        return ResponseEntity.badRequest().body(
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
    public ResponseEntity<ResponseError> withoutPermissionException(WithoutPermissionException ex) {
        return ResponseEntity.status(403).body(
                new ResponseError("without permission to perform this action")
        );
    }

    @ExceptionHandler(TokenGenerateException.class)
    public ResponseEntity<ResponseError> tokenCreationException(TokenGenerateException ex) {
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
    public ResponseEntity<ResponseError> unsendedEmailException(EmailSendException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(MilestoneSequenceException.class)
    public ResponseEntity<ResponseError> needToFollowOrderException(MilestoneSequenceException ex) {
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
