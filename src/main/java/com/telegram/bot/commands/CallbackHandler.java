package com.telegram.bot.commands;

import com.telegram.bot.model.MessageHandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    void setNext(CallbackHandler handler);

    void handle(Update update, MessageHandlerContext context);
}
