package my.project.codeguard.service.delivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService implements DeliveryChannel {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String code, String destination, String template) {
        String messageContent = template.replace("{code}", code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destination);
        message.setSubject("Your Verification Code");
        message.setText(messageContent);

        mailSender.send(message);

        log.info("Email sent successfully to: {}", destination);
    }
}
