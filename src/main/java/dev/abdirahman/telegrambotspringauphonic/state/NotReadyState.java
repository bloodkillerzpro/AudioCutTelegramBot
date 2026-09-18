package dev.abdirahman.telegrambotspringauphonic.state;

import dev.abdirahman.telegrambotspringauphonic.Client.AudioService;
import dev.abdirahman.telegrambotspringauphonic.bot.TestBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NotReadyState implements BotState {
    private final AudioService audioService;
    public NotReadyState(AudioService audioService) {
        this.audioService = audioService;
    }


    @Override
    public BotState handle(TestBot bot, Update update) {
        Message message = update.getMessage();
        if (!(message.hasText() && message.getText().equals("/cut"))) {
            return this;
        } else {
            Long chatId = message.getChatId();

            try {
                bot.sendText("Send the audio file to be cut", chatId);
                return bot.getReadyState();
            } catch (Exception e) {
                return bot.getNotReadyState();
            }
        }
    }
}
