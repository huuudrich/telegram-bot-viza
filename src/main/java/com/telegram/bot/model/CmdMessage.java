package com.telegram.bot.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.List;

@Data
@Builder
public class CmdMessage {
    private Long chatId;
    private String message;
    private Integer messageId;
    private List<KeyboardButton> buttons;
    private List<InlineButton> inlineButtons;
}
