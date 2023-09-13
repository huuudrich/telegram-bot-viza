package com.telegram.bot.controller;

import com.telegram.bot.config.BotConfig;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.util.KeyboardCreator;
import com.telegram.bot.util.UpdateReceivedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ApplicationEventPublisher eventPublisher;
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
        eventPublisher.publishEvent(new UpdateReceivedEvent(update));
    }

    public java.io.File downloadDocument(GetFile fileName) {
        try {
            File file = execute(fileName);
            return downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            log.error("Ошибка при загрузке документа: {}", fileName);
        }
        return null;
    }

    public void sendPhoto(SendPhoto photo) {
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке фото");
        }
    }

    public void sendEditedMessage(CmdMessage cmdMessage) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(cmdMessage.getChatId()));
        editMessageText.setText(cmdMessage.getMessage());
        editMessageText.setMessageId(cmdMessage.getMessageId());

        if (cmdMessage.getInlineButtons() != null) {
            editMessageText.setReplyMarkup(keyboardCreator.createInlineKeyboard(cmdMessage.getInlineButtons()));
        }

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке отредактированного сообщения");
        }
    }

    public void sendImageWithButtons(CmdMessage cmdMessage) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(cmdMessage.getChatId());
        sendPhotoRequest.setPhoto(new InputFile(cmdMessage.getImageUrl()));
        sendPhotoRequest.setReplyMarkup(keyboardCreator.createInlineKeyboard(cmdMessage.getInlineButtons()));

        try {
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке фото URL с кнопками");
        }
    }

    public void sendGif(long chatId, String gifUrl) {
        try {
            SendDocument sendDocumentRequest = new SendDocument();
            sendDocumentRequest.setChatId(chatId);
            sendDocumentRequest.setDocument(new InputFile(gifUrl));

            execute(sendDocumentRequest);
        } catch (TelegramApiException e) {
            log.error("Ошибка при загрузке гифки");
        }
    }

    public void sendMessage(CmdMessage cmdMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(cmdMessage.getChatId()));
        sendMessage.setText(cmdMessage.getMessage());

        List<KeyboardButton> buttons = cmdMessage.getButtons();

        if (cmdMessage.getReplyKeyboard() != null) {
            sendMessage.setReplyMarkup(cmdMessage.getReplyKeyboard());
        }

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
