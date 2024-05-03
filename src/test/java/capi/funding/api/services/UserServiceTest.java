package capi.funding.api.services;

import capi.funding.api.dto.EditUserDTO;
import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.entity.User;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.utils.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService service;
    @Mock
    Utils utils;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    UserRepository userRepository;

    NewPasswordDTO newPasswordDTO;
    EditUserDTO editUserDTO;
    User user;

    @BeforeAll
    public void setup() {
        newPasswordDTO = new NewPasswordDTO("Gabriel#01");
        editUserDTO = new EditUserDTO("Gabriel");
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
    @DisplayName("changePassword - should fetch the user from the authentication context")
    public void testShouldFetchTheUserFromTheAuthenticationContext() {
        NewPasswordDTO dto = new NewPasswordDTO("Gabriel#01");

        service.changePassword(dto);

        verify(utils).getAuthUser();
        verifyNoInteractions(userRepository.findById(any(Long.class)));
    }

    @Test
    @DisplayName("changePassword - should encrypt the password")
    public void testShouldEncryptThePassword() {
        final User user = service.changePassword(newPasswordDTO);

        assertNotEquals(newPasswordDTO.newPassword(), user.getPassword());
        verify(bCryptPasswordEncoder).encode(newPasswordDTO.newPassword());
    }

    @Test
    @DisplayName("changePassword - should save the user")
    public void testShouldSaveTheUser() {
        service.changePassword(newPasswordDTO);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("editUser - should get the editing user from the authentication context")
    public void testShouldEditTheUserFromTheAuthenticationContext() {
        service.editUser(editUserDTO);

        verify(utils).getAuthUser();
        verifyNoInteractions(userRepository.findById(any()));
    }

    @Test
    @DisplayName("editUser - should update the user attributes")
    public void testShouldUpdateTheUserAttributes() {
        final User user = service.editUser(editUserDTO);

        assertEquals(user.getName(), editUserDTO.name());
    }
}
