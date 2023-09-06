package com.telegram.bot.commands.proxy;

import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.MessageHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProxyCommandsHandler implements CommandHandler {
    private CommandHandler next;

    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, MessageHandlerContext context) {
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        if ("Прокси".equals(message)) {
            final List<KeyboardButton> mainButtons = List.of(
                    new KeyboardButton("Загрузить список"),
                    new KeyboardButton("Очистить список"),
                    new KeyboardButton("Посмотреть список"));

            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Выбери команду:")
                    .buttons(mainButtons).build());
        } else if ("Загрузить список".equals(message)) {
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Загрузи файл с названием и форматом proxy.txt \n" +
                            "формат прокси - USERNAME:PASSWORD:IP:PORT \n" +
                            "каждое прокси на новой строке без пробелов и другой хуеты. \n" +
                            "УЧТИ ЧТО ЭТИ ПРОКСИ ПОЛНОСТЬЮ ОБНОВЯТ СПИСОК В БАЗЕ ДАННЫХ \n")
                    .build());
        }
        if (next != null) {
            next.handle(update, context);
        }
    }
}
