package com.telegram.bot.commands;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.City;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.InlineButton;
import com.telegram.bot.model.ThreadType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CityCallbackHandler implements CallbackHandler {
    private CallbackHandler next;
    private final TelegramBot bot;

    @Override
    public void setNext(CallbackHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        String callbackData = update.getCallbackQuery().getData();

        for (ThreadType tt : ThreadType.values()) {
            if (callbackData.equals(tt.toString() + "-requests")) {

                List<InlineButton> cityButtons = new ArrayList<>();
                for (City code : City.values()) {

                    cityButtons.add(InlineButton.builder()
                            .text(code.getName())
                            .callbackData(callbackData + "-city-" + code).build());
                }

                bot.sendImageWithButtons(CmdMessage.builder()
                        .chatId(chatId)
                        .imageUrl("https://i.imgur.com/fn6uQ2t.png")
                        .inlineButtons(cityButtons)
                        .build());
                break;
            }
        }
        if (next != null) {
            next.handle(update, chatId);
        }
    }
}
