package com.telegram.bot.util;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;


public record UpdateReceivedEvent(Update update) {

}

