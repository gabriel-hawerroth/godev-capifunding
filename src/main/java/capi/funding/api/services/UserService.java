package capi.funding.api.services;

import capi.funding.api.dto.EditUserDTO;
import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.entity.User;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.utils.Utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final Utils utils;
    private final BCryptPasswordEncoder bcrypt;

    private final UserRepository userRepository;

    public UserService(Utils utils, BCryptPasswordEncoder bcrypt, UserRepository userRepository) {
        this.utils = utils;
        this.bcrypt = bcrypt;
        this.userRepository = userRepository;
    }

    public User getTokenUser() {
        return utils.getAuthUser();
    }

    public User changePassword(NewPasswordDTO dto) {
        final User user = utils.getAuthUser();

        user.setPassword(bcrypt.encode(dto.newPassword()));

        return userRepository.save(user);
    }

    public User editUser(EditUserDTO dto) {
        final User user = utils.getAuthUser();

        user.updateValues(dto);

        return userRepository.save(user);
    }

    public User changeProfileImage(MultipartFile file) {
        final User user = utils.getAuthUser();

        user.setProfile_image(
                utils.checkImageValidityAndCompress(file)
        );

        return userRepository.save(user);
    }

    public User removeProfileImage() {
        final User user = utils.getAuthUser();

        user.setProfile_image(null);

        return userRepository.save(user);
    }
}
