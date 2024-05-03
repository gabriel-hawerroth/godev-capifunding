package capi.funding.api.services;

import capi.funding.api.dto.EmailDTO;
import capi.funding.api.enums.EmailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(EmailDTO email) throws MessagingException {
        final MimeMessage message = javaMailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email.addressee());
        helper.setSubject(email.subject());
        helper.setText(email.content(), true);

        javaMailSender.send(message);
    }

    public String buildEmailTemplate(EmailType emailType, Long userId, String token) {
        final String url = "http://localhost:8080/auth/" + emailType.getValue() + "/" + userId + "/" + token;

        final String action = switch (emailType) {
            case ACTIVATE_ACCOUNT -> " ativar sua conta.";
        };

        return """
                        <!DOCTYPE html>
                        <html lang="pt-br">
                          <head>
                            <meta charset="UTF-8" />
                            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                            <title>Document</title>
                          </head>
                          <body>
                            <p>Clique <a href='
                """
                + url +
                """
                            ' target="_blank">aqui</a> para
                        """
                + action +
                """
                            </p>
                          </body>
                        </html>
                        """;
    }
}
