package com.telegram.bot.commands;

import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.CmdMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private CommandHandler next;
    private final TelegramBot bot;
    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        if ("/start".equals(update.getMessage().getText())) {
            final List<KeyboardButton> mainButtons = List.of(
                    new KeyboardButton("Прокси"),
                    new KeyboardButton("Заявки"),
                    new KeyboardButton("Посмотреть логи"));

            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Welcome in auto record bot for avas.mfa.gov.cn, выбери команду")
                    .buttons(mainButtons).build());

            bot.sendPhoto(new SendPhoto(String.valueOf(chatId),
                    new InputFile(new File("start_cmd.jpg"))));
        } else if (next != null) {
            next.handle(update, chatId);
        }
    }
}

