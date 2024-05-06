package capi.funding.api.services;

import capi.funding.api.dto.EditUserDTO;
import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.DataIntegrityException;
import capi.funding.api.infra.exceptions.InvalidParametersException;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.utils.Utils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final BCryptPasswordEncoder bcrypt;
    private final Utils utils;

    private final UserRepository userRepository;

    public UserService(BCryptPasswordEncoder bcrypt, Utils utils, UserRepository userRepository) {
        this.bcrypt = bcrypt;
        this.utils = utils;
        this.userRepository = userRepository;
    }

    public User getAuthUser() {
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

    protected User findById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    protected User save(User user) {
        utils.validateObject(user);

        return userRepository.save(user);
    }

    protected void deleteById(long id) {
        if (id < 1) {
            throw new InvalidParametersException("id must be valid");
        }

        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityException("this user has linked registers, impossible to exclude");
        }
    }
}
