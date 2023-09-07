package com.telegram.bot.config;

import com.telegram.bot.commands.*;
import com.telegram.bot.commands.proxy.ProxyCommandsHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    @Autowired
    private StartCommandHandler startCommandHandler;

    @Autowired
    private CityKeyboardHandler cityKeyboardHandler;

    @Autowired
    private ProxyCommandsHandler proxyCommandsHandler;

    @Autowired
    private DocumentLoadHandler documentLoadHandler;
    @Autowired
    private ProxyCallbackHandler proxyCallbackHandler;


    @Bean
    public CommandHandler firstHandler() {
        startCommandHandler.setNext(proxyCommandsHandler);

        return startCommandHandler;
    }

    @Bean
    public CallbackHandler secondHandler() {
        proxyCommandsHandler.setNext(proxyCommandsHandler);

        return proxyCallbackHandler;
    }
}
