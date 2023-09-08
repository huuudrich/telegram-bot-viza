package com.telegram.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    void setNext(CommandHandler handler);

    void handle(Update update, long chatId);
}


