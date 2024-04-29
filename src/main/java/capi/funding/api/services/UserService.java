package capi.funding.api.services;

import capi.funding.api.dto.UserEditDTO;
import capi.funding.api.models.User;
import capi.funding.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<User> changePassword(String newPassword) {
        final User user = utilsService.getAuthUser();

        user.setPassword(bcrypt.encode(newPassword));

        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<User> editUser(UserEditDTO userDto) {
        final User user = utilsService.getAuthUser();

        user.setName(userDto.name());

        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<User> changeProfileImage(MultipartFile file) {
        final byte[] compressedFile = utilsService.checkImageValidityAndCompress(file);

        final User user = utilsService.getAuthUser();

        user.setProfile_image(compressedFile);

        return ResponseEntity.ok(userRepository.save(user));
    }
}
