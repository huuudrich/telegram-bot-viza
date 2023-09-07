package com.telegram.bot.controller;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.commands.DocumentLoadHandler;
import com.telegram.bot.config.BotConfig;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.EditedCmdMessage;
import com.telegram.bot.model.MessageHandlerContext;
import com.telegram.bot.repository.UserRepository;
import com.telegram.bot.util.KeyboardCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Controller
public class TelegramBot extends TelegramLongPollingBot {
    private final CommandHandler chainMessageHandler;
    private final CallbackHandler chainCallbackHandler;
    private final BotConfig botConfig;
    private final KeyboardCreator keyboardCreator;
    private final DocumentLoadHandler documentLoadHandler;
    private final UserRepository userRepository;

    public TelegramBot(@Qualifier("firstHandler") CommandHandler chainMessageHandler,
                       @Qualifier("secondHandler") CallbackHandler chainCallbackHandler,
                       BotConfig botConfig, KeyboardCreator keyboardCreator,
                       DocumentLoadHandler documentLoadHandler, UserRepository userRepository) {
        this.chainMessageHandler = chainMessageHandler;
        this.chainCallbackHandler = chainCallbackHandler;
        this.botConfig = botConfig;
        this.keyboardCreator = keyboardCreator;
        this.documentLoadHandler = documentLoadHandler;
        this.userRepository = userRepository;
    }

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
        long chatId = extractChatId(update);

        if (!userRepository.existsUserByTelegramId(chatId)) {
            sendUnregisteredUserMessage(chatId);
            return;
        }

        if (update.hasMessage()) {
            handleTextMessage(update, context, chatId);
        } else if (update.hasCallbackQuery()) {
            chainCallbackHandler.handle(update, context);
        }

        sendResponse(context);
    }

    private long extractChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        throw new IllegalStateException("Update does not contain a valid chat ID");
    }

    private void sendUnregisteredUserMessage(long chatId) {
        sendMessage(CmdMessage.builder()
                .chatId(chatId)
                .message("Твой id: %s не зарегистрирован".formatted(chatId))
                .build());
    }

    private void handleTextMessage(Update update, MessageHandlerContext context, long chatId) {
        if (update.getMessage().hasDocument()) {
            String fileId = update.getMessage().getDocument().getFileId();
            java.io.File file = downloadFile(GetFile.builder().fileId(fileId).build());
            documentLoadHandler.handle(update, context, file);
        } else if (update.getMessage().hasText()) {
            chainMessageHandler.handle(update, context);
        }
    }

    private void sendResponse(MessageHandlerContext context) {
        if (context.getResponseMessage() != null) {
            sendMessage(context.getResponseMessage());
        }

        if (context.getSendPhoto() != null) {
            try {
                execute(context.getSendPhoto());
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке фото");
            }
        }

        if (context.getEditedResponseMessage() != null) {
            sendEditedMessage(context.getEditedResponseMessage());
        }
    }


    private java.io.File downloadFile(GetFile fileName) {
        try {
            File file = execute(fileName);
            return downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            log.error("Ошибка при загрузке документа: {}", fileName);
        }
        return null;
    }

    private void sendEditedMessage(EditedCmdMessage cmdMessage) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(cmdMessage.getChatId()));
        editMessageText.setText(cmdMessage.getMessage());

        if (cmdMessage.getInlineButtons() != null) {
            editMessageText.setReplyMarkup(keyboardCreator.createInlineKeyboard(cmdMessage.getInlineButtons()));
        }

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке отредактированного сообщения");
        }
    }

    private void sendMessage(CmdMessage cmdMessage) {
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
