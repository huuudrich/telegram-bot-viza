package com.telegram.bot.config;

import com.telegram.bot.commands.*;
import com.telegram.bot.commands.CityCallbackHandler;
import com.telegram.bot.commands.proxies.ProxyCallbackHandler;
import com.telegram.bot.commands.proxies.ProxyCommandsHandler;
import com.telegram.bot.commands.requests.RequestsMainCallbackHandler;
import com.telegram.bot.commands.requests.RequestsCommandsHandler;
import com.telegram.bot.commands.requests.RequestsThreadTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {

    @Bean
    public CommandHandler firstHandler(StartCommandHandler startCommandHandler,
                                       ProxyCommandsHandler proxyCommandsHandler,
                                       RequestsThreadTypeHandler requestsThreadTypeHandler,
                                       RequestsCommandsHandler requestsCommandsHandler) {
        startCommandHandler.setNext(proxyCommandsHandler);
        proxyCommandsHandler.setNext(requestsThreadTypeHandler);
        requestsThreadTypeHandler.setNext(requestsCommandsHandler);
        // Другие настройки цепочки обработчиков могут быть добавлены здесь
        return startCommandHandler;
    }

    @Bean
    public CallbackHandler secondHandler(ProxyCallbackHandler proxyCallbackHandler,
                                         CityCallbackHandler cityCallbackHandler,
                                         RequestsMainCallbackHandler requestsMainCallbackHandler) {
        proxyCallbackHandler.setNext(cityCallbackHandler);
        cityCallbackHandler.setNext(requestsMainCallbackHandler);

        // Другие настройки цепочки обработчиков могут быть добавлены здесь
        return proxyCallbackHandler;
    }
}

