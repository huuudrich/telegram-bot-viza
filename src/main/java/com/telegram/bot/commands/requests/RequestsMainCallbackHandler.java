package com.telegram.bot.commands.requests;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.CityType;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.InlineButton;
import com.telegram.bot.model.ThreadType;
import com.telegram.bot.repository.BookingDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestsMainCallbackHandler implements CallbackHandler {
    private CallbackHandler next;
    private final TelegramBot bot;
    private final BookingDataRepository bookingDataRepository;

    @Override
    public void setNext(CallbackHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        String callbackData = update.getCallbackQuery().getData();

        if (callbackData.contains("-requests-city-")) {
            String[] callbackDates = callbackData.split("-");
            CityType cityType = CityType.valueOf(callbackDates[3]);
            ThreadType threadType = ThreadType.valueOf(callbackDates[0]);

            final List<InlineButton> buttons = List.of(
                    InlineButton.builder().text("Загрузить список").callbackData(callbackData + "-load-txt").build(),
                    InlineButton.builder().text("Вписать по одной").callbackData(callbackData + "-load-str").build(),
                    InlineButton.builder().text("Очистить список").callbackData(callbackData + "-clear").build(),
                    InlineButton.builder().text("Посмотреть список").callbackData(callbackData + "-check").build());

            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Тип: %s \n".formatted(threadType.toString()) +
                            "Текущий город: %s ".formatted(cityType.getName()))
                    .inlineButtons(buttons)
                    .build());
        }
    }
}

