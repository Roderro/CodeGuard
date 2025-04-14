package my.project.codeguard.service.telegrambot;

import my.project.codeguard.entity.TelegramUser;
import my.project.codeguard.repository.TelegramUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class TelegramUpdateHandler extends TelegramLongPollingBot {
    private final TelegramUserRepository telegramUserRepository;


    public TelegramUpdateHandler(@Value("${telegram.bot.token}")String botToken, TelegramUserRepository telegramUserRepository) {
        super(botToken);
        this.telegramUserRepository = telegramUserRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();
            TelegramUser telegramUser = new TelegramUser(user.getId(), user.getUserName(), chatId);
            telegramUserRepository.save(telegramUser);
        }
    }

    @Override
    public String getBotUsername() {
        return "CodeGuard";
    }
}
