package com.telegram.bot.controller;

import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.config.BotConfig;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.MessageHandlerContext;
import com.telegram.bot.util.KeyboardCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final CommandHandler firstHandler;
    private final BotConfig botConfig;
    private final KeyboardCreator keyboardCreator;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        MessageHandlerContext context = new MessageHandlerContext();

        if (update.hasCallbackQuery()) {

        }
        firstHandler.handle(update, context);

        if (context.getResponseMessage() != null) {
            sendMessage(context.getResponseMessage());
        }

    }


    public void sendMessage(CmdMessage cmdMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(cmdMessage.getChatId()));
        sendMessage.setText(cmdMessage.getMessage());

        List<KeyboardButton> buttons = cmdMessage.getButtons();

        if (buttons != null) {
            sendMessage.setReplyMarkup(keyboardCreator.replyKeyboardMarkup(buttons));
        }

        if (cmdMessage.getInlineButtons() != null) {
            sendMessage.setReplyMarkup(keyboardCreator.createInlineKeyboard(cmdMessage.getInlineButtons()));
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения");
        }
    }
}
