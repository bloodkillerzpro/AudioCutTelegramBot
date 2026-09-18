package dev.abdirahman.telegrambotspringauphonic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramBotSpringAuphonicApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotSpringAuphonicApplication.class, args);
        System.out.println("TelegramBotSpringAuphonicApplication started");

    }
}
