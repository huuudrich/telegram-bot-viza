package com.telegram.bot.model;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Data
public class MessageHandlerContext {
    private CmdMessage responseMessage;
    private EditedCmdMessage editedResponseMessage;
    private SendPhoto sendPhoto;
}

