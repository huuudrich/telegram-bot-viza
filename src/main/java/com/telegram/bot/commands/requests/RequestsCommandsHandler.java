package com.telegram.bot.commands.requests;

import com.telegram.bot.commands.CommandHandler;
import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.*;
import com.telegram.bot.repository.UserDataRepository;
import com.telegram.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestsCommandsHandler implements CommandHandler {
    private CommandHandler next;
    private final TelegramBot bot;
    private final UserDataRepository userDataRepository;
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
            UserData userData = UserData.builder()
                    .owner(owner)
                    .nameSurname(data[0])
                    .phone(data[1])
                    .email(data[2])
                    .appointmentNumber(data[3])
                    .cityType(cityType)
                    .build();

            if (cityType.equals(CityType.MSK) || cityType.equals(CityType.SPB)) {
                userData.setPassport(data[4]);
                userDataRepository.save(userData);
            } else {
                userDataRepository.save(userData);
            }
            String info = "Данные успешно сохранены: %s".formatted(userData.getLogInfo());
            log.info(info);
            bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message(info)
                    .build());
        }
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
