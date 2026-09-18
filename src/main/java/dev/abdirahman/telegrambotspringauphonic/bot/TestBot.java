package dev.abdirahman.telegrambotspringauphonic.bot;

import dev.abdirahman.telegrambotspringauphonic.Exceptions.FileRetrievalException;
import dev.abdirahman.telegrambotspringauphonic.Exceptions.SendMessageException;
import dev.abdirahman.telegrambotspringauphonic.state.NotReadyState;
import dev.abdirahman.telegrambotspringauphonic.state.ReadyState;
import dev.abdirahman.telegrambotspringauphonic.state.BotState;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;

import okhttp3.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class TestBot extends TelegramLongPollingBot {
    private NotReadyState notReadyState;
    private ReadyState readyState;
    private BotState botState;

    @Autowired
    public TestBot(NotReadyState notReadyState, ReadyState readyState) {
        this.notReadyState = notReadyState;
        this.readyState = readyState;
        this.botState = notReadyState;
    }


    @Value("${telegram.bot.token}")
    private String token;

    public void sendText(String message, long chatId) throws SendMessageException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.telegram.org/bot" + token + "/sendMessage").newBuilder();

        urlBuilder.addQueryParameter("chat_id", Long.toString(chatId));
        urlBuilder.addQueryParameter("text", message);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new SendMessageException("Message not sent. Unexpected response code: " + response);
            }
        } catch (IOException e) {
            throw new SendMessageException("Message not sent. Unexpected response code:" +e.getMessage());
        }

    }


    @Override
    public void onUpdateReceived(Update update) {
        botState = botState.handle(this, update);
    }

    public byte[] getFile(String fileId) throws FileRetrievalException {
        GetFile getFile = GetFile.builder()
                .fileId(fileId)
                .build();

        try (InputStream inputStream =
                     downloadFileAsStream(execute(getFile).getFilePath());
             ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            inputStream.transferTo(outputStream);
            return outputStream.toByteArray();
        }

         catch (TelegramApiException | IOException e) {
            throw new FileRetrievalException("Failed to retrieve Telegram file");
        }
    }


    @Override
    public String getBotUsername() {
        return "CutSilenceBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @PostConstruct
    public void test() {
        System.out.println("BOT BEAN CREATED");
        System.out.println("TOKEN EXISTS: " + (token != null && !token.isBlank()));
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public BotState getNotReadyState() { return notReadyState; }
    public BotState getReadyState()       { return readyState; }
}




