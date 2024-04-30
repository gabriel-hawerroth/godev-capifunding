package capi.funding.api;

import capi.funding.api.dto.ResponseError;
import capi.funding.api.exceptions.AuthException;
import capi.funding.api.exceptions.InvalidParametersException;
import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.exceptions.TokenCreationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                new ResponseError(ex.getMessage())
        );
    }

    @ExceptionHandler(TokenCreationException.class)
    public ResponseEntity<ResponseError> tokenCreationException(TokenCreationException ex) {
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
}
