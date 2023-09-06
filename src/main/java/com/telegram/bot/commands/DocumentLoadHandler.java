package com.telegram.bot.commands;

import com.telegram.bot.model.ChinaProxy;
import com.telegram.bot.model.MessageHandlerContext;
import com.telegram.bot.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentLoadHandler implements CommandHandler {
    private CommandHandler next;

    private final TelegramService telegramService;

    @Override
    public void setNext(CommandHandler handler) {

    }

    @Override
    public void handle(Update update, MessageHandlerContext context) {
        if (!update.getMessage().hasDocument()) {
            return;
        }

        String fileName = update.getMessage().getDocument().getFileName();

        if (fileName.equals("proxy.txt")) {

        } else if (fileName.equals("requests.txt")) {

        } else if (next != null) {
            next.handle(update, context);
        }
    }

    private List<ChinaProxy> extractProxiesFromFile(java.io.File file, String delimiter) {
        List<ChinaProxy> proxies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] rowData = line.split(delimiter);

                if (rowData.length >= 4) {
                    ChinaProxy proxy = ChinaProxy.builder()
                            .username(rowData[0])
                            .password(rowData[1])
                            .ipAddress(rowData[2])
                            .port(Integer.parseInt(rowData[3]))
                            .build();

                    proxies.add(proxy);
                } else {
                    log.warn("Строка не содержит достаточного количества данных: {}", line);
                }
            }
            log.info("Успешное чтение и парсинг прокси из файла: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при чтении данных из файла: {}", file.getAbsolutePath());
        }
        return proxies;
    }
}
