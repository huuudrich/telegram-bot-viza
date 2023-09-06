package com.telegram.bot.commands;

import com.telegram.bot.model.MessageHandlerContext;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface InlineButtonsHandler {
    void setNext(CommandHandler handler);

    void handle(Update update, MessageHandlerContext context);
}
