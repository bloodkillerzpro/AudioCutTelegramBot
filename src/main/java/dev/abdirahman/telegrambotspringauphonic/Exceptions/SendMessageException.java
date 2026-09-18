package dev.abdirahman.telegrambotspringauphonic.Exceptions;

import java.io.IOException;

public class SendMessageException extends IOException {
    public SendMessageException(String message) {
        super(message);
    }
}
