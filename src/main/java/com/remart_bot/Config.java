package com.remart_bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class Config {

    @Bean
    public TelegramBotsApi botsApi (RemartBot remartBot) {
        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(remartBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return botsApi;
    }

}
