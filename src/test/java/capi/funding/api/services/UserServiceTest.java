package capi.funding.api.services;

import capi.funding.api.dto.EditUserDTO;
import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.DataIntegrityException;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private NewPasswordDTO newPasswordDTO;
    private EditUserDTO editUserDTO;
    private User user;

    @InjectMocks
    private UserService userService;
    @Mock
    private Utils utils;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        newPasswordDTO = new NewPasswordDTO("Gabriel#01");
        editUserDTO = new EditUserDTO("Murilo");
        user = new User(
                1L,
                "gabriel@gmail.com",
                "$2a$10$WjNOD14Yf.LCe3L6gGT9IemiY.4qtxcpv4AEl8DFjxt3HmyKlPn62",
                "Gabriel",
                true,
                LocalDateTime.now().minusDays(45),
                null
        );
    }

    @Test
    @DisplayName("getAuthUser - should fetch the user from the authentication context")
    void testGetAuthUserShouldFetchTheUserFromTheAuthenticationContext() {
        userService.getAuthUser();

        verify(utils).getAuthUser();
    }

    @Test
    @DisplayName("changePassword - should fetch the user from the authentication context")
    void testChangePasswordShouldFetchTheUserFromTheAuthenticationContext() {
        when(utils.getAuthUser()).thenReturn(user);

        userService.changePassword(newPasswordDTO);

        verify(utils).getAuthUser();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("changePassword - should encrypt the password")
    void testShouldEncryptThePassword() {
        when(utils.getAuthUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        final User user = userService.changePassword(newPasswordDTO);

        assertNotEquals(newPasswordDTO.newPassword(), user.getPassword());
        verify(bCryptPasswordEncoder).encode(newPasswordDTO.newPassword());
    }

    @Test
    @DisplayName("changePassword - should save the user")
    void testChangePasswordShouldSaveTheUser() {
        when(utils.getAuthUser()).thenReturn(user);

        userService.changePassword(newPasswordDTO);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("editUser - should get the editing user from the authentication context")
    void testShouldEditTheUserFromTheAuthenticationContext() {
        when(utils.getAuthUser()).thenReturn(user);

        userService.editUser(editUserDTO);

        verify(utils).getAuthUser();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("editUser - should update the user attributes")
    void testShouldUpdateTheUserAttributes() {
        when(utils.getAuthUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        final User user = userService.editUser(editUserDTO);

        verify(userRepository).save(user);
        assertEquals(user.getName(), editUserDTO.name());
    }

    @Test
    @DisplayName("changeProfileImage - should fetch the user from the authentication context")
    void testChangeProfileImageShouldFetchTheUserFromTheAuthenticationContext() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", (byte[]) null
        );

        when(utils.getAuthUser()).thenReturn(user);

        userService.changeProfileImage(multipartFile);

        verify(utils).getAuthUser();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("changeProfileImage - should save the user")
    void testChangeProfileImageShouldSaveTheUser() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", (byte[]) null
        );

        when(utils.getAuthUser()).thenReturn(user);

        userService.changeProfileImage(multipartFile);

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("removeProfileImage - should fetch the user from the authentication context")
    void testRemoveProfileImageShouldFetchTheUserFromTheAuthenticationContext() {
        when(utils.getAuthUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.removeProfileImage();

        verify(utils).getAuthUser();
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("removeProfileImage - should set user profile image to null")
    void testRemoveProfileImageShouldSetUserProfileImageToNull() {
        when(utils.getAuthUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        final User user = userService.removeProfileImage();

        assertNull(user.getProfile_image());
    }

    @DisplayName("findById - should accept just positive numbers")
    @ParameterizedTest
    @CsvSource({
            "0",
            "-25"
    })
    void testFindByIdShouldAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                userService.findById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("findById - should throw NotFoundException when project is not found")
    void testShouldThrowNotFoundExceptionWhenProjectIsNotFound() {
        final long userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userService.findById(userId));

        assertEquals("user not found", exception.getMessage());
    }

    @Test
    @DisplayName("save - should validate the object")
    void testSaveShouldValidateTheObject() {
        final User user = new User();

        userService.save(user);

        verify(utils).validateObject(user);
    }

    @Test
    @DisplayName("save - should save the user")
    void testSaveShouldSaveTheUser() {
        final User user = new User();

        userService.save(user);

        verify(userRepository).save(user);
    }

    @DisplayName("deleteById - should accept just positive numbers")
    @ParameterizedTest
    @CsvSource({
            "0",
            "-25"
    })
    void testDeleteByIdShouldAcceptJustPositiveNumbers(long id) {
        final InvalidParametersException exception = assertThrows(InvalidParametersException.class, () ->
                userService.deleteById(id));

        assertEquals("id must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("deleteById - should delete the user")
    void testDeleteByIdShouldDeleteTheUser() {
        final long userId = 1;

        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("deleteById - should throw DataIntegrityException when has linked registers")
    void testShouldThrowDataIntegrityExceptionWhenHasLinkedRegisters() {
        final long userId = 1;

        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(userId);

        final DataIntegrityException exception = assertThrows(DataIntegrityException.class, () ->
                userService.deleteById(userId));

        assertEquals("this user has linked registers, impossible to exclude", exception.getMessage());
    }
}
