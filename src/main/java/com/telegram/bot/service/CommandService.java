package com.telegram.bot.service;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.commands.DocumentLoadHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.repository.UserRepository;
import com.telegram.bot.util.UpdateReceivedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Controller
public class CommandService {
    private final CommandHandler chainMessageHandler;
    private final CallbackHandler chainCallbackHandler;
    private final DocumentLoadHandler documentLoadHandler;
    private final UserRepository userRepository;
    private final TelegramBot bot;

    public CommandService(@Qualifier("firstHandler") CommandHandler chainMessageHandler,
                          @Qualifier("secondHandler") CallbackHandler chainCallbackHandler,
                          DocumentLoadHandler documentLoadHandler,
                          UserRepository userRepository, TelegramBot bot) {
        this.chainMessageHandler = chainMessageHandler;
        this.chainCallbackHandler = chainCallbackHandler;
        this.documentLoadHandler = documentLoadHandler;
        this.userRepository = userRepository;
        this.bot = bot;
    }

    @EventListener
    public void handleUpdateReceivedEvent(UpdateReceivedEvent event) {
        Update update = event.update();
        onUpdateReceived(update);
    }

    public void onUpdateReceived(Update update) {
        long chatId = extractChatId(update);

        if (!userRepository.existsUserByTelegramId(chatId)) {
            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Твой id: %s не зарегистрирован".formatted(chatId))
                    .build());
            return;
        }

        if (update.hasMessage()) {
            handleTextMessage(update, chatId);
        } else if (update.hasCallbackQuery()) {
            chainCallbackHandler.handle(update, chatId);
        }
    }

    private long extractChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        throw new IllegalStateException("Update does not contain a valid chat ID");
    }

    private void handleTextMessage(Update update, long chatId) {
        if (update.getMessage().hasDocument()) {
            documentLoadHandler.handle(update, chatId);
        } else if (update.getMessage().hasText()) {
            chainMessageHandler.handle(update, chatId);
        }
    }
}
