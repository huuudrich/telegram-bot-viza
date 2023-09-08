package com.telegram.bot.commands.proxy;

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
public class ProxyCommandsHandler implements CommandHandler {
    private CommandHandler next;
    private static final String proxy = """
              ####      ####      ####    ##  ##   ##  ##
             ##  ##    ##  ##   ##  ##   ##  ##   ##  ##
             ##  ##    ##  ##   ##  ##    ####    ##  ##
             ####      ####     ##  ##      ##        ####
             ##           ####      ##  ##    ####       ##
             ##           ## ##     ##  ##   ##  ##      ##
             ##          ##  ##      ####    ##  ##      ##\
            """;
    private final TelegramBot bot;
    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        String message = update.getMessage().getText();

        if ("Прокси".equals(message)) {
            final List<InlineButton> buttons = List.of(
                    InlineButton.builder().text("Загрузить список").callbackData("load-proxy").build(),
                    InlineButton.builder().text("Очистить список").callbackData("clear-proxy").build(),
                    InlineButton.builder().text("Посмотреть список").callbackData("check-proxy").build());

            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message(proxy)
                    .inlineButtons(buttons)
                    .build());
        } else if (next != null) {
            next.handle(update, chatId);
        }
    }
}
