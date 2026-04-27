package com.vertex.core.config;

import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;

@SuppressWarnings("unused")
@Component
public class MessageProvider {

    private static final String location = "fa";

    public MessageProvider() {
    }

    public String getMessage(String key) {
        try {
            java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle("messages", Locale.forLanguageTag("fa"));
            return resourceBundle.getString(key);
        } catch (MissingResourceException var3) {
            return var3.getMessage();
        }
    }

    public static String getMessageByKey(String key) {
        try {
            java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle("messages", Locale.forLanguageTag("fa"));
            return resourceBundle.getString(key);
        } catch (MissingResourceException var2) {
            return var2.getMessage();
        }
    }

    public static String getMessageByKeyAndParam(String key, String... paramValue) {
        java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle("messages", Locale.forLanguageTag("fa"));
        String msgValue = resourceBundle.getString(key);
        MessageFormat messageFormat = new MessageFormat(msgValue);
        return messageFormat.format(paramValue);
    }
}

