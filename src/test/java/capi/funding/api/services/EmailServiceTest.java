package capi.funding.api.services;

import capi.funding.api.dto.EmailDTO;
import capi.funding.api.enums.EmailType;
import capi.funding.api.utils.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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
public class EmailServiceTest {

    final EmailDTO emailDTO = new EmailDTO(
            "gabriel@gmail.com",
            "subject test",
            "content test"
    );

    @InjectMocks
    EmailService emailService;
    @Mock
    JavaMailSender javaMailSender;
    @Mock
    Utils utils;

    @Test
    @DisplayName("sendMail - should validate the DTO")
    public void testShouldValidateTheDTO() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendMail(emailDTO);

        verify(utils).validateObject(emailDTO);
    }

    @Test
    @DisplayName("sendMail - shouldn't accept null parameters")
    public void testSendMailShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                emailService.sendMail(null));
    }

    @Test
    @DisplayName("buildEmailTemplate - shouldn't accept null parameters")
    public void testBuildEmailTemplateShouldntAcceptNullParameters() {
        assertThrows(NullPointerException.class, () ->
                emailService.buildEmailTemplate(null, 1, "token"));

        assertThrows(NullPointerException.class, () ->
                emailService.buildEmailTemplate(EmailType.ACTIVATE_ACCOUNT, 1, null));
    }

    @Test
    @DisplayName("buildEmailTemplate - should build the email content")
    public void testShouldBuildTheEmailContent() {
        final String mailContent = emailService.buildEmailTemplate(
                EmailType.ACTIVATE_ACCOUNT, 1, "token"
        );

        assertNotNull(mailContent);
        assertInstanceOf(String.class, mailContent);
    }
}