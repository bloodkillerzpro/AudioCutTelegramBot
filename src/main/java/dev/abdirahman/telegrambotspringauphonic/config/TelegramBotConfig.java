package dev.abdirahman.telegrambotspringauphonic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@Configuration
@Import(TelegramBotStarterConfiguration.class)
public class TelegramBotConfig {
}