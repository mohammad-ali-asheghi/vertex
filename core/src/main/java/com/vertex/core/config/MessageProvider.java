package com.vertex.core.config;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@SuppressWarnings("unused")
@Component
public class MessageProvider {

    private static MessageSource messageSource;

    public MessageProvider(MessageSource messageSource) {
        MessageProvider.messageSource = messageSource;
    }

    public static String getMessage(String key) {
        return messageSource.getMessage(key, null, key, getCurrentLocale());
    }

    public static String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, key, getCurrentLocale());
    }

    private static Locale getCurrentLocale() {
        return Locale.of("fa", "IR");
    }
}