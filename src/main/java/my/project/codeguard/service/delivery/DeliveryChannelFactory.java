package my.project.codeguard.service.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DeliveryChannelFactory {
    @Qualifier("smsService")
    private final DeliveryChannel smsService;
    @Qualifier("emailService")
    private final DeliveryChannel emailService;
    @Qualifier("telegramService")
    private final DeliveryChannel telegramService;
    @Qualifier("fileStorageService")
    private final DeliveryChannel fileStorageService;

    @Autowired
    public DeliveryChannelFactory(DeliveryChannel smsService, DeliveryChannel emailService, DeliveryChannel telegramService, DeliveryChannel fileStorageService) {
        this.smsService = smsService;
        this.emailService = emailService;
        this.telegramService = telegramService;
        this.fileStorageService = fileStorageService;
    }


    public DeliveryChannel getChannel(String channelType) {
        return switch (channelType.toLowerCase()) {
            case "sms" -> smsService;
            case "email" -> emailService;
            case "telegram" -> telegramService;
            case "file" -> fileStorageService;
            default -> throw new IllegalArgumentException("Unknown delivery channel: " + channelType);
        };
    }
}
