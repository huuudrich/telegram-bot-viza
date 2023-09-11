package com.telegram.bot.commands.requests;

import com.telegram.bot.commands.CallbackHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.City;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.InlineButton;
import com.telegram.bot.model.ThreadType;
import com.telegram.bot.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestsCallbackHandler implements CallbackHandler {
    private CallbackHandler next;
    private final TelegramBot bot;
    private final UserDataRepository userDataRepository;

    @Override
    public void setNext(CallbackHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        String callbackData = update.getCallbackQuery().getData();

        for (ThreadType tt : ThreadType.values()) {
            StringBuilder concatData = new StringBuilder(tt.toString() + "-requests-city-");
            if (callbackData.contains(concatData)) {
                for (City c : City.values()) {
                    concatData = new StringBuilder(concatData + c.toString());
                    if (callbackData.contentEquals(concatData)) {

                        final List<InlineButton> buttons = List.of(
                                InlineButton.builder().text("Загрузить список").callbackData(callbackData + "-load-txt").build(),
                                InlineButton.builder().text("Вписать по одной").callbackData(callbackData + "-load-str").build(),
                                InlineButton.builder().text("Очистить список").callbackData(callbackData + "-clear-requests").build(),
                                InlineButton.builder().text("Посмотреть список").callbackData(callbackData + "-check-requests").build());

                        bot.sendMessage(CmdMessage.builder()
                                .chatId(chatId)
                                .message("Тип: %s \n".formatted(tt.toString()) +
                                        "Текущий город: %s ".formatted(c.getName()))
                                .inlineButtons(buttons)
                                .build());
                    } else if (callbackData.contentEquals(new StringBuilder(concatData + "-load-str"))) {
                        SendMessage message = new SendMessage();
                        message.setChatId(String.valueOf(chatId));
                        message.setText("Пожалуйста, введите данные:");
                        message.setReplyMarkup(new ForceReplyKeyboard(true));
                        try {
                            bot.execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (callbackData.contentEquals(new StringBuilder(concatData + "-clear-requests"))) {

                    } else if (callbackData.contentEquals(new StringBuilder(concatData + "-check-requests"))) {

                    }
                }
            }
        }
    }
}

