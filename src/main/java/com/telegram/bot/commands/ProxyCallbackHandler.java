package com.telegram.bot.commands;

import com.telegram.bot.model.*;
import com.telegram.bot.repository.ChinaProxyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProxyCallbackHandler implements CallbackHandler {
    private CallbackHandler next;
    private final ChinaProxyRepository proxyRepository;

    @Override
    public void setNext(CallbackHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, MessageHandlerContext context) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (callbackData.equals("load-proxy")) {
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message("Загрузи файл с названием и форматом proxy.txt \n" +
                            "формат прокси - USERNAME:PASSWORD:IP:PORT \n" +
                            "каждое прокси на новой строке без пробелов и другой хуеты. \n" +
                            "УЧТИ ЧТО ЭТИ ПРОКСИ ПОЛНОСТЬЮ ОБНОВЯТ СПИСОК В БАЗЕ ДАННЫХ \n")
                    .build());
        } else if (callbackData.equals("clear-proxy")) {
            proxyRepository.deleteAll();
            String info = "Таблица с прокси успешна очищена";
            log.info(info);
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(chatId)
                    .message(info)
                    .build());
        } else if (callbackData.contains("check-proxy")) {
            int pageNumber = 0;
            if (callbackData.contains(":")) {
                pageNumber = Integer.parseInt(callbackData.split(":")[1]);
            }

            Pageable pageable = PageRequest.of(pageNumber, 10);

            Page<ChinaProxy> page = proxyRepository.findAll(pageable);
            List<ChinaProxy> proxies = page.getContent();

            if (proxies.isEmpty()) {
                context.setResponseMessage(CmdMessage.builder()
                        .chatId(chatId)
                        .message("Список прокси пуст")
                        .build());
                return;
            }

            List<InlineButton> buttons = new ArrayList<>();

            String messageText = proxies.stream()
                    .map(ChinaProxy::toString)
                    .collect(Collectors.joining("\n"));

            if (page.hasPrevious()) {
                buttons.add(InlineButton.builder()
                        .text("Назад")
                        .callbackData("check-proxy-page:" + (page.getNumber() - 1))
                        .build());
            }

            if (page.hasNext()) {
                buttons.add(InlineButton.builder()
                        .text("Вперед")
                        .callbackData("check-proxy-page:" + (page.getNumber() + 1))
                        .build());
            }


            context.setEditedResponseMessage(EditedCmdMessage.builder()
                    .chatId(chatId)
                    .message(messageText)
                    .inlineButtons(buttons).build());
        } else if (next != null) {
            next.handle(update, context);
        }
    }
}
