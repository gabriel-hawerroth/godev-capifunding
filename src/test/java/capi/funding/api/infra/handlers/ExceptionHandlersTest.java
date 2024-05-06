package capi.funding.api.infra.handlers;

import capi.funding.api.dto.InvalidFieldsDTO;
import capi.funding.api.dto.ResponseError;
import capi.funding.api.infra.exceptions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ExceptionHandlersTest {

    @InjectMocks
    ExceptionHandlers exceptionHandlers;

    @Test
    @DisplayName("exception - general exception should return internal server error")
    void testGeneralExceptionShouldReturnInternalServerError() {
        final var response = exceptionHandlers.exception(mock(Exception.class));

        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    @DisplayName("exception - general exception should return ResponseError on body")
    void testGeneralExceptionShoulReturnResponseErrorOnBody() {
        final var response = exceptionHandlers.exception(mock(Exception.class));

        assertInstanceOf(ResponseEntity.class, response);
        assertInstanceOf(ResponseError.class, response.getBody());
    }

    @Test
    @DisplayName("methodArgumentNotValidException - should return bad request response")
    void testMethodArgumentNotValidExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.methodArgumentNotValidException(
                mock(MethodArgumentNotValidException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("validationException - should return bad request response")
    void testValidationExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.validationException(
                mock(ValidationException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("validationException - should return InvalidFieldDTO list on body")
    void testValidationExceptionShouldReturnInvalidFieldslist() {
        List<FieldError> errors = List.of(
                new FieldError("object", "field1", "error"),
                new FieldError("object", "field2", "error")
        );

        final List<InvalidFieldsDTO> invalidFieldsDTOList =
                errors.stream()
                        .map(InvalidFieldsDTO::new)
                        .toList();

        final var response = exceptionHandlers.validationException(
                new ValidationException(invalidFieldsDTOList)
        );

        assertInstanceOf(List.class, response.getBody());
        assertEquals(invalidFieldsDTOList, response.getBody());
    }

    @Test
    @DisplayName("authException - should return bad request response")
    void testAuthExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.authException(
                mock(AuthException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("notFoundException - should return bad request response")
    void testNotFoundExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.notFoundException(
                mock(NotFoundException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("withoutPermissionException - should return bad request response")
    void testWithoutPermissionExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.withoutPermissionException(
                mock(WithoutPermissionException.class)
        );

        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
        assertEquals(
                "without permission to perform this action",
                Objects.requireNonNull(response.getBody()).error_description()
        );
    }

    @Test
    @DisplayName("tokenCreationException - should return internal server error response")
    void testTokenGenerateExceptionShouldReturnInternalServerError() {
        final var response = exceptionHandlers.tokenGenerateException(
                mock(TokenGenerateException.class)
        );

        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    @DisplayName("invalidParametersException - should return bad request response")
    void testInvalidParametersExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.invalidParametersException(
                mock(InvalidParametersException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("emailSendException - should return internal server error response")
    void testEmailSendExceptionShouldReturnInternalServerError() {
        final var response = exceptionHandlers.emailSendException(
                mock(EmailSendException.class)
        );

        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    @DisplayName("milestoneSequenceException - should return bad request response")
    void testMilestoneSequenceExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.milestoneSequenceException(
                mock(MilestoneSequenceException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("projectEditabilityException - should return bad request response")
    void testProjectEditabilityExceptionShouldReturnBadRequestResponse() {
        final var response = exceptionHandlers.projectEditabilityException(
                mock(ProjectEditabilityException.class)
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
}
