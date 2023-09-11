package com.telegram.bot.commands.requests;

import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.InlineButton;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestsThreadTypeHandler implements CommandHandler {
    private CommandHandler next;
    private final TelegramBot bot;

    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        String message = update.getMessage().getText();

        if ("Заявки".equals(message)) {
            final List<InlineButton> buttons = List.of(
                    InlineButton.builder().text("CHECKER").callbackData("CHECKER-requests").build(),
                    InlineButton.builder().text("BOOKERS").callbackData("BOOKERS-requests").build());

            bot.sendImageWithButtons(CmdMessage.builder()
                    .chatId(chatId)
                    .imageUrl("https://i.imgur.com/xkLZQ2Z.png")
                    .inlineButtons(buttons)
                    .build());
        } else if (next != null) {
            next.handle(update, chatId);
        }
    }
}
