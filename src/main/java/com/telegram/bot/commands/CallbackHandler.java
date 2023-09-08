package com.telegram.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    void setNext(CallbackHandler handler);

    void handle(Update update, long chatId);
}
