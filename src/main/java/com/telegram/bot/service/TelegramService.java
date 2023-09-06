package com.telegram.bot.service;

import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.util.KeyboardCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramService {
    private final TelegramLongPollingBot bot;
    private final KeyboardCreator keyboardCreator;


    public java.io.File downloadFile(GetFile fileName) {
        try {
            File file = bot.execute(fileName);
            return bot.downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            log.error("Ошибка при загрузке документа: {}", fileName);
        }
        return null;
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
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", cmdMessage.getMessage());
        }
    }
}
