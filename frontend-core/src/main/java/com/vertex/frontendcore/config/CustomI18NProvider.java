package com.vertex.frontendcore.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class CustomI18NProvider implements I18NProvider {

    private static final Logger log = LoggerFactory.getLogger(CustomI18NProvider.class);

    private final MessageSource messageSource;

    private static final Locale LOCALE_FA = Locale.of("fa", "IR");
    private static final List<Locale> PROVIDED_LOCALES = List.of(LOCALE_FA, Locale.ENGLISH);

    public CustomI18NProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return PROVIDED_LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null || key.isBlank()) return "";

        try {
            return messageSource.getMessage(key, params, locale);
        } catch (NoSuchMessageException e) {
            log.warn("translate '{}' not found for key :{}", locale.getLanguage(), key);
            return "!" + key;
        }
    }
}
