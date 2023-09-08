package com.telegram.bot.config;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.commands.ProxyCallbackHandler;
import com.telegram.bot.commands.StartCommandHandler;
import com.telegram.bot.commands.proxy.ProxyCommandsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {

    @Bean
    public CommandHandler firstHandler(StartCommandHandler startCommandHandler, ProxyCommandsHandler proxyCommandsHandler) {
        startCommandHandler.setNext(proxyCommandsHandler);
        // Другие настройки цепочки обработчиков могут быть добавлены здесь
        return startCommandHandler;
    }

    @Bean
    public CallbackHandler secondHandler(ProxyCommandsHandler proxyCommandsHandler, ProxyCallbackHandler proxyCallbackHandler) {
        proxyCommandsHandler.setNext(proxyCommandsHandler);
        // Другие настройки цепочки обработчиков могут быть добавлены здесь
        return proxyCallbackHandler;
    }
}

