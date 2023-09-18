package com.telegram.bot.commands.requests;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.CityType;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.ThreadType;
import com.telegram.bot.repository.BookingDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestsOtherCallbackHandler implements CallbackHandler {
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

        if (callbackData.contains("-requests-city-") && callbackData.contains("load-str")) {
            String[] callbackDates = callbackData.split("-");
            CityType cityType = CityType.valueOf(callbackDates[3]);
            ThreadType threadType = ThreadType.valueOf(callbackDates[0]);

            StringBuilder message = new StringBuilder("Тип: %s\n".formatted(threadType.toString()) +
                    "Текущий город: %s\n".formatted(cityType.getName()) +
                    "Пожалуйста, введите данные в формате\n" +
                    "Имя Фамилия,телефон,email,номер заявки");

            if (cityType.equals(CityType.MSK) || cityType.equals(CityType.SPB)) {
                message = new StringBuilder(message + ",номер паспорта");
            }

            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message(String.valueOf(message))
                    .replyKeyboard(new ForceReplyKeyboard(true)).build());
        } else - if (callbackData.contains("-requests-city-") && callbackData.contains("load-txt")) {




        } else if (next != null) {
            next.handle(update, chatId);
        }
    }
}
