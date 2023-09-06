package com.telegram.bot.util;

import com.telegram.bot.model.InlineButton;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardCreator {
    public InlineKeyboardMarkup createInlineKeyboard(List<InlineButton> inlineButtons) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 0; i < inlineButtons.size(); i += 2) {
            List<InlineButton> currentRowData = new ArrayList<>();

            currentRowData.add(inlineButtons.get(i));
            if (i + 1 < inlineButtons.size()) {
                currentRowData.add(inlineButtons.get(i + 1));
            }

            List<InlineKeyboardButton> rowInline = getInlineKeyboardButtons(currentRowData);
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }


    private List<InlineKeyboardButton> getInlineKeyboardButtons(List<InlineButton> rowData) {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (InlineButton buttonData : rowData) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonData.getText());

            if (buttonData.getCallbackData() != null) {
                button.setCallbackData(buttonData.getCallbackData());
            }

            if (buttonData.getUrl() != null) {
                button.setUrl(buttonData.getUrl());
            }

            rowInline.add(button);
        }
        return rowInline;
    }

    public ReplyKeyboardMarkup replyKeyboardMarkup(List<KeyboardButton> buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows(buttons));

        return replyKeyboardMarkup;
    }

    private List<KeyboardRow> keyboardRows(List<KeyboardButton> buttons) {
        List<KeyboardRow> rows = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();

            row.add(buttons.get(i));
            if (i + 1 < buttons.size()) {
                row.add(buttons.get(i + 1));
            }

            rows.add(row);
        }

        return rows;
    }
}
