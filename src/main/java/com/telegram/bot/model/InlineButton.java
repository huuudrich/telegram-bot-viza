package com.telegram.bot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InlineButton  {
    private String text;
    private String callbackData;
    private String url;
}
