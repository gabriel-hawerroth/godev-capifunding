package capi.funding.api.services;

import capi.funding.api.dto.EmailDTO;
import capi.funding.api.enums.EmailType;
import capi.funding.api.utils.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private EmailDTO emailDTO;

    @InjectMocks
    private EmailService emailService;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private Utils utils;

    @BeforeEach
    void setUp() {
        emailDTO = new EmailDTO(
                "gabriel@gmail.com",
                "subject test",
                "content test"
        );
    }

    @Test
    @DisplayName("sendMail - should validate the DTO")
    void testShouldValidateTheDTO() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendMail(emailDTO);

        verify(utils).validateObject(emailDTO);
    }

    @Test
    @DisplayName("sendMail - shouldn't accept null parameters")
    void testSendMailShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendMail(null));
    }

    @Test
    @DisplayName("buildEmailTemplate - shouldn't accept null parameters")
    void testBuildEmailTemplateShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                emailService.buildEmailTemplate(null, 1, "token"));

        assertThrows(IllegalArgumentException.class, () ->
                emailService.buildEmailTemplate(EmailType.ACTIVATE_ACCOUNT, 1, null));
    }

    @Test
    @DisplayName("buildEmailTemplate - should build the email content")
    void testShouldBuildTheEmailContent() {
        final String mailContent = emailService.buildEmailTemplate(
                EmailType.ACTIVATE_ACCOUNT, 1, "token"
        );

        assertNotNull(mailContent);
        assertInstanceOf(String.class, mailContent);
    }
}
