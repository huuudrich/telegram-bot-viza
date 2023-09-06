package com.telegram.bot.commands;

import com.telegram.bot.model.City;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.InlineButton;
import com.telegram.bot.model.MessageHandlerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CityKeyboardHandler implements CommandHandler {
    private CommandHandler next;

    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, MessageHandlerContext context) {
        long chatId = update.getMessage().getChatId();

        if ("Заявки для букера".equals(update.getMessage().getText())) {
            List<InlineButton> cityButtons = new ArrayList<>();
            for (City code : City.values()) {
                String cityName = code.toString();

                cityButtons.add(InlineButton.builder()
                        .text(cityName)
                        .callbackData("%s-%s".formatted("request", cityName))
                        .build());
            }

            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Выбери код города: ")
                    .inlineButtons(cityButtons)
                    .build());

        } else if (next != null) {
            next.handle(update, context);
        }
    }
}

