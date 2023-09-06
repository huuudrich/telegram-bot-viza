package com.telegram.bot.commands;

import com.telegram.bot.model.City;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.MessageHandlerContext;
import com.telegram.bot.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RequestsLoadHandler implements InlineButtonsHandler {
    private CommandHandler next;
    private final TelegramService telegramService;

    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, MessageHandlerContext context) {
        long chatId = update.getMessage().getChatId();

        if (update.hasCallbackQuery() && "request".equals(update.getCallbackQuery().getData())) {
            System.out.println(update.getCallbackQuery().getData());
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Выберите формат загрузки для города: %s".formatted(City.fromCode(update.getCallbackQuery().getData())))
                    .build());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasDocument()) {
            String fileId = update.getMessage().getDocument().getFileId();

            System.out.println(fileId);
            return;
        }

        if (next != null) {
            next.handle(update, context);
        }
    }
}
