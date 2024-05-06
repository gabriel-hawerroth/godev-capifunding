package capi.funding.api.utils;

import capi.funding.api.dto.InvalidFieldsDTO;
import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.*;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class Utils {

    private static final List<String> VALID_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "jfif", "webp");

    private static final int MAX_FILE_SIZE = 3 * 1024 * 1024; // 3Mb in bytes

    private static final Map<Integer, Integer> COMPRESSION_SIZE_MAP = Map.of(
            50000, 500,     // 0,05MB - 50kb
            125000, 600,    // 0,125MB - 125kb
            250000, 800,    // 0,25MB - 250kb
            500000, 1000,   // 0,5MB - 500kb
            1000000, 1500,  // 1,0MB
            1500000, 2000,  // 1,5MB
            2000000, 2400,  // 2,0MB
            2500000, 2600,  // 2,5MB
            Integer.MAX_VALUE, 3000
    );

    private final Logger logger;

    private final Validator validator;

    public Utils(Validator validator) {
        this.validator = validator;
        this.logger = Logger.getLogger(getClass().getName());
    }

    private static String checkImageValidity(MultipartFile file) {
        if (file.getSize() == 0) {
            throw new InvalidParametersException("invalid file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("the file is too large");
        }

        final String fileName = Objects.requireNonNull(file.getOriginalFilename());
        final int pointIndex = fileName.lastIndexOf('.');

        if (pointIndex <= 0) {
            throw new InvalidFileException("invalid file name");
        }

        final String fileExtension = fileName.substring(pointIndex + 1).toLowerCase();
        if (!VALID_EXTENSIONS.contains(fileExtension)) {
            throw new InvalidFileException("invalid file extension");
        }
        return fileExtension;
    }

    public int getCompressedSize(byte[] file) {
        for (Map.Entry<Integer, Integer> entry : COMPRESSION_SIZE_MAP.entrySet()) {
            if (file.length < entry.getKey()) {
                return entry.getValue();
            }
        }
        return 3000;
    }

    public User getAuthUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthException("user is not authenticated");
        } else {
            return (User) authentication.getPrincipal();
        }
    }

    public void checkPermission(long userId) {
        final long authUserId = getAuthUser().getId();

        if (authUserId != userId) {
            throw new WithoutPermissionException();
        }
    }

    public byte[] checkImageValidityAndCompress(@NonNull MultipartFile file) {
        final String fileExtension = checkImageValidity(file);

        try {
            if (fileExtension.equals("png") || fileExtension.equals("webp")) {
                return file.getBytes();
            } else {
                logger.info("uncompressed file size: " + file.getSize());

                final ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                final int compressedFileSize = getCompressedSize(file.getBytes());

                Thumbnails.of(inputStream)
                        .size(compressedFileSize, compressedFileSize)
                        .outputFormat(fileExtension)
                        .outputQuality(0.5)
                        .toOutputStream(outputStream);

                logger.info(() -> "compressed file size: " + outputStream.size());

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }
    }

    public void validateObject(@NonNull Object object) {
        final Errors errors = validator.validateObject(object);

        if (errors.hasErrors()) {
            final List<InvalidFieldsDTO> invalidFieldsDTOList =
                    errors.getFieldErrors().stream()
                            .map(InvalidFieldsDTO::new)
                            .toList();

            throw new ValidationException(invalidFieldsDTOList);
        }
    }
}
