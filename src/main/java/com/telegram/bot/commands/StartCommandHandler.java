package com.telegram.bot.commands;

import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.MessageHandlerContext;
import com.telegram.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private CommandHandler next;
    private final UserRepository userRepository;

    @Override
    public void setNext(CommandHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, MessageHandlerContext context) {
        long chatId = update.getMessage().getChatId();

        if (!userRepository.existsUserByTelegramId(chatId)) {
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Твой id: %s не зарегистрирован".formatted(chatId)).build());
            return;
        }

        if ("/start".equals(update.getMessage().getText())) {
            final List<KeyboardButton> mainButtons = List.of(
                    new KeyboardButton("Прокси"),
                    new KeyboardButton("Заявки"),
                    new KeyboardButton("Посмотреть логи"));

            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Welcome in auto record bot for avas.mfa.gov.cn, выбери команду")
                    .buttons(mainButtons).build());
        } else if (next != null) {
            next.handle(update, context);
        }
    }
}

