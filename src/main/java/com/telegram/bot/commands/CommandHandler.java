package com.telegram.bot.commands;

import com.telegram.bot.model.MessageHandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    void setNext(CommandHandler handler);
    void handle(Update update, MessageHandlerContext context);
}

