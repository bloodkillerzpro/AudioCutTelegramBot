package dev.abdirahman.telegrambotspringauphonic.state;

import dev.abdirahman.telegrambotspringauphonic.Client.AudioService;
import dev.abdirahman.telegrambotspringauphonic.bot.TestBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;


@Component
public class ReadyState implements BotState {

    private final AudioService audioService;
    public ReadyState(AudioService audioService) {
        this.audioService = audioService;
    }


    @Override
    public BotState handle(TestBot bot, Update update) {
        Message message = update.getMessage();
        if (!update.getMessage().hasAudio()) {
            return bot.getNotReadyState();
        }
        else {
            Long  chatId = message.getChatId();
            Long fileSize = message.getAudio().getFileSize();
            String fileId = message.getAudio().getFileId();
            String fileName = message.getAudio().getFileName();
            byte[] file = bot.getFile(fileId);


            try {
                bot.sendText("Audio will be cut ", chatId);
                URI location = audioService.sendToAuphonic(fileName, file,chatId);


                return bot.getReadyState();
            }
            catch (Exception e) {
                return bot.getNotReadyState();
            }

        }

    }


}

