package com.telegram.bot.config;

import com.telegram.bot.commands.*;
import com.telegram.bot.commands.CityCallbackHandler;
import com.telegram.bot.commands.proxies.ProxyCallbackHandler;
import com.telegram.bot.commands.proxies.ProxyCommandsHandler;
import com.telegram.bot.commands.requests.RequestsCallbackHandler;
import com.telegram.bot.commands.requests.RequestsThreadTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {

    @Bean
    public CommandHandler firstHandler(StartCommandHandler startCommandHandler,
                                       ProxyCommandsHandler proxyCommandsHandler,
                                       RequestsThreadTypeHandler requestsThreadTypeHandler) {
        startCommandHandler.setNext(proxyCommandsHandler);
        proxyCommandsHandler.setNext(requestsThreadTypeHandler);
        // Другие настройки цепочки обработчиков могут быть добавлены здесь
        return startCommandHandler;
    }

    @Bean
    public CallbackHandler secondHandler(ProxyCallbackHandler proxyCallbackHandler,
                                         CityCallbackHandler cityCallbackHandler,
                                         RequestsCallbackHandler requestsCallbackHandler) {
        proxyCallbackHandler.setNext(cityCallbackHandler);
        cityCallbackHandler.setNext(requestsCallbackHandler);

        // Другие настройки цепочки обработчиков могут быть добавлены здесь
        return proxyCallbackHandler;
    }
}

