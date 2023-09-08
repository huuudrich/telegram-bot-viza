package com.telegram.bot.commands;

import com.telegram.bot.controller.TelegramBot;
import com.telegram.bot.model.ChinaProxy;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.InlineButton;
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
    private final TelegramBot bot;

    @Override
    public void setNext(CallbackHandler handler) {
        this.next = handler;
    }

    @Override
    public void handle(Update update, long chatId) {
        String callbackData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        System.out.println(callbackData);

        switch (callbackData) {
            case "load-proxy" -> bot.sendMessage(CmdMessage.builder()
                    .chatId(chatId).message("""
                            Загрузи файл с названием и форматом proxy.txt\s
                            формат прокси - USERNAME:PASSWORD:IP:PORT\s
                            каждое прокси на новой строке без пробелов и другой хуеты.\s
                            УЧТИ ЧТО ЭТИ ПРОКСИ ДОБАВЯТСЯ К СПИСКУ В БД\s
                            """)
                    .build());
            case "clear-proxy" -> {
                proxyRepository.deleteAll();
                String info = "Таблица с прокси успешна очищена";
                log.info(info);
                bot.sendMessage(CmdMessage.builder().chatId(chatId).message(info)
                        .build());
            }
            case "check-proxy" -> proxyScroll(callbackData, chatId, false, messageId);
            default -> {
                if (callbackData.contains("check-proxy-page:")) {
                    proxyScroll(callbackData, chatId, true, messageId);
                } else {
                    next.handle(update, chatId);
                }
            }
        }
    }

    private void proxyScroll(String callbackData, long chatId, boolean isEditedMsg, int messageId) {
        int pageNumber = 0;

        if (isEditedMsg) {
            pageNumber = Integer.parseInt(callbackData.split(":")[1]);
        }

        Pageable pageable = PageRequest.of(pageNumber, 10);

        Page<ChinaProxy> page = proxyRepository.findAll(pageable);
        List<ChinaProxy> proxies = page.getContent();

        if (proxies.isEmpty()) {
            bot.sendMessage(CmdMessage.builder().chatId(chatId).message("Список прокси пуст").build());
            return;
        }

        List<InlineButton> buttons = new ArrayList<>();

        String messageText = proxies.stream().map(ChinaProxy::toString).collect(Collectors.joining("\n"));

        if (page.hasPrevious()) {
            buttons.add(InlineButton.builder().text("Назад").callbackData("check-proxy-page:" + (page.getNumber() - 1)).build());
        }

        if (page.hasNext()) {
            buttons.add(InlineButton.builder().text("Вперед").callbackData("check-proxy-page:" + (page.getNumber() + 1)).build());
        }

        if (isEditedMsg) {
            bot.sendEditedMessage(CmdMessage.builder().chatId(chatId)
                    .message(messageText)
                    .messageId(messageId)
                    .inlineButtons(buttons).build());
        } else {
            bot.sendMessage(CmdMessage.builder().chatId(chatId)
                    .message(messageText)
                    .inlineButtons(buttons).build());
        }
    }
}
