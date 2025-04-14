package my.project.codeguard.service.delivery;

import lombok.extern.slf4j.Slf4j;
import my.project.codeguard.entity.TelegramUser;
import my.project.codeguard.exception.DeliveryFailedException;
import my.project.codeguard.repository.TelegramUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
@Slf4j
public class TelegramService extends DefaultAbsSender implements DeliveryChannel {
    private final TelegramUserRepository telegramUserRepository;

    protected TelegramService(DefaultBotOptions options, @Value("${telegram.bot.token}") String botToken, TelegramUserRepository telegramUserRepository) {
        super(options, botToken);
        this.telegramUserRepository = telegramUserRepository;
    }

    @Override
    public void send(String code, String destination, String template) {
        String messageText = template.replace("{code}", code);
        TelegramUser tgUser = telegramUserRepository.findByUsername(destination)
                .orElseThrow(() -> new DeliveryFailedException("User must start chat with bot first"));
        long chatId = tgUser.getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        message.enableHtml(true);

        try {
            execute(message);
            log.info("Telegram message sent to chat {}", chatId);
        } catch (TelegramApiException e) {
            handleTelegramError(e, destination);
        }
    }

    private void handleTelegramError(TelegramApiException e, String destination) {
        if (e.getMessage().contains("chat not found")) {
            log.error("Chat not found: {}", destination);
            throw new DeliveryFailedException("User must start chat with bot first");
        } else if (e.getMessage().contains("user is deactivated")) {
            log.error("User deactivated: {}", destination);
            throw new DeliveryFailedException("User account deactivated");
        } else {
            log.error("Telegram API error: {}", e.getMessage());
            throw new DeliveryFailedException("Telegram delivery failed");
        }
    }
}
