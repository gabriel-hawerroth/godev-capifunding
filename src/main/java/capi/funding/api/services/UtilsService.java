package capi.funding.api.services;

import capi.funding.api.enums.UserRole;
import capi.funding.api.exceptions.InvalidFileException;
import capi.funding.api.exceptions.InvalidParametersException;
import capi.funding.api.exceptions.WithoutPermissionException;
import capi.funding.api.models.User;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UtilsService {

    private static final List<String> VALID_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "jfif", "webp");

    private static String checkImageValidity(MultipartFile file) {
        if (file == null) {
            throw new InvalidParametersException("file cannot be null");
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

    private static int getCompressedSize(MultipartFile file) {
        return (int) file.getSize() / 100 * 65;
    }

    public User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public void checkPermission(long userId) {
        final User user = getAuthUser();

        if (!Objects.equals(user.getRole(), UserRole.ADM.toString()) && user.getId() != userId) {
            throw new WithoutPermissionException();
        }
    }

    public byte[] checkImageValidityAndCompress(MultipartFile file) {
        final String fileExtension = checkImageValidity(file);

        try {
            if (fileExtension.equals("png") || fileExtension.equals("webp")) {
                return file.getBytes();
            } else {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                final int compressedFileSize = getCompressedSize(file);

                Thumbnails.of(inputStream)
                        .size(compressedFileSize, compressedFileSize)
                        .outputFormat(fileExtension)
                        .outputQuality(0.5)
                        .toOutputStream(outputStream);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }
    }
}
