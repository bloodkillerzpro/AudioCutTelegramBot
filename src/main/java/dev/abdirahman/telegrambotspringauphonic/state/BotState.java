package dev.abdirahman.telegrambotspringauphonic.state;

import dev.abdirahman.telegrambotspringauphonic.bot.TestBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public interface BotState {
    public BotState handle(TestBot testBot, Update update);
}
