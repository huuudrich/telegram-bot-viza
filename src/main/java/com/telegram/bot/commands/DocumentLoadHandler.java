package com.telegram.bot.commands;

import com.telegram.bot.model.ChinaProxy;
import com.telegram.bot.model.CmdMessage;
import com.telegram.bot.model.MessageHandlerContext;
import com.telegram.bot.repository.ChinaProxyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentLoadHandler {

    private final ChinaProxyRepository proxyRepository;

    public void handle(Update update, MessageHandlerContext context, File file) {
        String fileName = update.getMessage().getDocument().getFileName();

        if (fileName.equals("proxy.txt")) {
            List<ChinaProxy> proxies = new ArrayList<>(extractProxiesFromFile(file));
            proxyRepository.saveAll(proxies);
            String info = "Прокси успешно загружены и сохранены в базу";
            log.info(info);
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .message(info)
                    .build());
        } else if (fileName.equals("requests.txt")) {

        } else {
            context.setResponseMessage(CmdMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .message("Нет обработки такого файла")
                    .build());
        }
    }

    private List<ChinaProxy> extractProxiesFromFile(File file) {
        List<ChinaProxy> proxies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] rowData = line.split(":");

                if (rowData.length >= 4) {
                    ChinaProxy proxy = ChinaProxy.builder()
                            .username(rowData[0])
                            .password(rowData[1])
                            .ipAddress(rowData[2])
                            .port(Integer.parseInt(rowData[3]))
                            .valid(true)
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
