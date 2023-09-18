package com.telegram.bot.commands.requests;

import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.CityType;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.ThreadType;
import com.telegram.bot.model.User;
import com.telegram.bot.model.data.BookingData;
import com.telegram.bot.model.data.CheckerData;
import com.telegram.bot.model.data.UserData;
import com.telegram.bot.repository.BookingDataRepository;
import com.telegram.bot.repository.CheckerDataRepository;
import com.telegram.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestsLoadHandler implements CommandHandler {
    private CommandHandler next;
    private final TelegramBot bot;
    private final BookingDataRepository bookingDataRepository;
    private final CheckerDataRepository checkerDataRepository;
    private final UserRepository userRepository;

    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        if (!update.getMessage().isReply()) {
            return;
        }

        String replyMessage = update.getMessage().getReplyToMessage().getText();
        String message = update.getMessage().getText();

        if (replyMessage.contains("Имя Фамилия,телефон,email,номер заявки")) {
            String[] arrayReply = replyMessage.split("\n");

            String typeInfo = arrayReply[0];
            ThreadType threadType = ThreadType.valueOf(typeInfo.substring(typeInfo.indexOf(":") + 2));

            String cityInfo = arrayReply[1];
            CityType cityType = CityType.toCode(cityInfo.substring(cityInfo.indexOf(":") + 2));


            String[] data = message.trim().split(",");

            if (!validatedData(data, chatId)) {
                return;
            }

            User owner = userRepository.getUserByTelegramId(chatId);

            boolean b = cityType.equals(CityType.MSK) || cityType.equals(CityType.SPB);

            if (threadType.equals(ThreadType.BOOKERS)) {
                if (b) {
                    BookingData bookingData = (BookingData) createUserData(BookingData.class, data, owner, cityType);
                    bookingData.setPassport(data[4]);
                    bookingDataRepository.save(bookingData);
                } else {
                    BookingData bookingData = (BookingData) createUserData(BookingData.class, data, owner, cityType);
                    bookingDataRepository.save(bookingData);
                }
            } else if (threadType.equals(ThreadType.CHECKER)) {
                if (b) {
                    CheckerData checkerData = (CheckerData) createUserData(CheckerData.class, data, owner, cityType);
                    checkerData.setPassport(data[4]);
                    checkerDataRepository.save(checkerData);
                } else {
                    CheckerData checkerData = (CheckerData) createUserData(CheckerData.class, data, owner, cityType);
                    checkerDataRepository.save(checkerData);
                }
            }

            String info = (("Тип: %s\n" +
                    "Город: %s\n" +
                    "Данные успешно сохранены").formatted(threadType, cityType.getName()));

            log.info(info);
            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message(info)
                    .build());
        }
    }

    private UserData createUserData(Class<? extends UserData> type, String[] data, User owner, CityType cityType) {
        UserData userData = new UserData();
        try {
            userData = type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.warn("Ошибка создания userdata");
        }

        userData.setOwner(owner);
        userData.setNameSurname(data[0]);
        userData.setPhone(data[1]);
        userData.setEmail(data[2]);
        userData.setAppointmentNumber(data[3]);
        userData.setCityType(cityType);

        if (cityType.equals(CityType.MSK) || cityType.equals(CityType.SPB)) {
            userData.setPassport(data[4]);
        }

        return userData;
    }

    private boolean validatedData(String[] data, long chatId) {
        CmdMessage cmdMessage = CmdMessage.builder()
                .chatId(chatId)
                .build();

        if (!data[0].contains(" ")) {
            String info = "Имя и фамилия написаны не раздельно!";
            cmdMessage.setMessage(info);
            log.error(info);
            bot.sendMessage(cmdMessage);
            return false;
        }

        if (data[1].length() != 11) {
            System.out.println(data[1].length());
            String info = "Номер телефона должен быть равен 11 знакам";
            cmdMessage.setMessage(info);
            log.error(info);
            bot.sendMessage(cmdMessage);
            return false;
        }

        if (!data[2].contains("@")) {
            String info = "Ошибка в емейле";
            cmdMessage.setMessage(info);
            log.error(info);
            bot.sendMessage(cmdMessage);
            return false;
        }
        return true;
    }

}
