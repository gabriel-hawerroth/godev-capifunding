package capi.funding.api.utils;

import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UtilsTest {

    @InjectMocks
    private Utils utils;
    @Mock
    private Validator validator;
    @Mock
    private Errors errors;

    @Test
    @DisplayName("getCompressedSize - should return 3000 when isn't a mapped file size")
    void testGetCompressedSizeShouldReturn3000WhenIsntAMappedFileSize() {
        assertEquals(3000, utils.getCompressedSize(new byte[Integer.MAX_VALUE - 1000]));
    }

    @Test
    @DisplayName("getAuthUser - should throw exception when there is no authentication in the security context")
    void testShouldThrowExceptionWhenThereIsNoAuthenticationInTheSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(null);
        AuthException exception = assertThrows(AuthException.class, () -> utils.getAuthUser());

        assertEquals("user is not authenticated", exception.getMessage());
    }

    @Test
    @DisplayName("getAuthUser - should fetch the user from the authentication context")
    void testShouldFetchTheUserFromTheAuthenticationContext() {
        final User user = new User();
        final var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final User authUser = utils.getAuthUser();

        assertEquals(user, authUser);
    }

    @Test
    @DisplayName("checkPermission - should throw exception when user id is different")
    void testShouldThrowExceptionWhenUserIdIsDifferent() {
        final User user = new User();
        user.setId(1L);
        final var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(WithoutPermissionException.class, () -> utils.checkPermission(2));
    }

    @Test
    @DisplayName("checkPermission - should pass when user id is equals")
    void testShouldPassWhenUserIdIsEquals() {
        final User user = new User();
        user.setId(1L);
        final var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertDoesNotThrow(() -> utils.checkPermission(1));
    }

    @Test
    @DisplayName("checkImageValidity - shouldn't accept null parameters")
    void testNullFileShouldThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                utils.checkImageValidityAndCompress(null));
    }

    @Test
    @DisplayName("checkImageValidity - empty file should throws exception")
    void testEmptyFileShouldThrowsException() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", new byte[0]
        );

        assertThrows(InvalidParametersException.class, () ->
                utils.checkImageValidityAndCompress(multipartFile));
    }

    @Test
    @DisplayName("checkImageValidity - too large file should throws exception")
    void testTooLargeFileShouldThrowsException() {
        final MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-file.jpeg",
                "image/jpeg",
                new byte[4000000]
        );

        InvalidFileException exception = assertThrows(InvalidFileException.class, () ->
                utils.checkImageValidityAndCompress(mockFile));

        assertEquals("the file is too large", exception.getMessage());
    }

    @Test
    @DisplayName("checkImageValidity - invalid file name should throws exception")
    void testInvalidFileNameShouldThrowsException() {
        final MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test", "text/plain", new byte[2000000]
        );

        InvalidFileException exception = assertThrows(InvalidFileException.class, () ->
                utils.checkImageValidityAndCompress(mockFile));

        assertEquals("invalid file name", exception.getMessage());
    }

    @Test
    @DisplayName("checkImageValidity - invalid file extension should throws exception")
    void testInvalidFileExtensionShouldThrowsException() {
        final MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", new byte[2000000]
        );

        InvalidFileException exception = assertThrows(InvalidFileException.class, () ->
                utils.checkImageValidityAndCompress(mockFile));

        assertEquals("invalid file extension", exception.getMessage());
    }

    @DisplayName("checkImageValidityAndCompress - .png or .webp files should not compress")
    @ParameterizedTest
    @CsvSource({
            "test.png",
            "test.webp"
    })
    void testPngOrWebpFilesShouldNotCompress(String fileName) {
        final byte[] fileContent = new byte[2000000];

        final MockMultipartFile mockFile = new MockMultipartFile(
                "file", fileName, "text/jpeg", fileContent
        );

        final byte[] compressedFile = utils.checkImageValidityAndCompress(mockFile);

        assertEquals(fileContent, compressedFile);
        assertEquals(fileContent.length, compressedFile.length);
    }

    @Test
    @DisplayName("checkImageValidityAndCompress - invalid file should throws exception")
    void testInvalidFileShouldThrowsException() {
        final byte[] fileContent = "test".getBytes();

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.jpeg", "text/jpeg", fileContent
        );

        assertThrows(InvalidFileException.class, () ->
                utils.checkImageValidityAndCompress(mockFile));
    }

    @Test
    @DisplayName("checkImageValidityAndCompress - valid file should be compressed")
    void testValidFileShouldBeCompressed() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("test-normal-image.jpeg")).getFile());

        final MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-file.jpeg",
                "image/jpeg",
                new FileInputStream(file)
        );

        final byte[] compressedFile = utils.checkImageValidityAndCompress(mockFile);

        assertNotEquals(mockFile.getBytes(), compressedFile);
        assertTrue(compressedFile.length < mockFile.getBytes().length);
    }

    @Test
    @DisplayName("validateObject - invalid object should throws exception")
    void testInvalidObjectShouldThrowsException() {
        final TestDTO testDTO = new TestDTO(null);

        when(errors.hasErrors()).thenReturn(true);
        when(validator.validateObject(testDTO)).thenReturn(errors);

        assertThrows(ValidationException.class, () ->
                utils.validateObject(testDTO));
    }

    @Test
    @DisplayName("validateObject - shouldn't accept null parameters")
    void testShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                utils.validateObject(null));
    }

    @Test
    @DisplayName("validateObject - should pass when object is valid")
    void testShouldPassWhenObjectIsValid() {
        final TestDTO testDTO = new TestDTO(null);

        when(errors.hasErrors()).thenReturn(false);
        when(validator.validateObject(testDTO)).thenReturn(errors);

        assertDoesNotThrow(() -> utils.validateObject(testDTO));
    }

    public record TestDTO(
            @NotNull
            @NotBlank
            String test
    ) {
    }
}
