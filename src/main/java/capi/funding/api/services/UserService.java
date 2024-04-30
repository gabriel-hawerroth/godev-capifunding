package capi.funding.api.services;

import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.dto.UserEditDTO;
import capi.funding.api.models.User;
import capi.funding.api.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UtilsService utilsService;
    private final BCryptPasswordEncoder bcrypt;
    private final UserRepository userRepository;

    public User getTokenUser() {
        return utilsService.getAuthUser();
    }

    public User changePassword(@Valid NewPasswordDTO dto) {
        final User user = utilsService.getAuthUser();

        user.setPassword(bcrypt.encode(dto.newPassword()));

        return userRepository.save(user);
    }

    public User editUser(@Valid UserEditDTO userDto) {
        final User user = utilsService.getAuthUser();

        user.setName(userDto.name());

        return userRepository.save(user);
    }

    public User changeProfileImage(MultipartFile file) {
        final byte[] compressedFile = utilsService.checkImageValidityAndCompress(file);

        final User user = utilsService.getAuthUser();

        user.setProfile_image(compressedFile);

        return userRepository.save(user);
    }
}
