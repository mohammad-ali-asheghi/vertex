package com.vertex.frontendcore.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class CustomI18NProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "messages";

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(Locale.of("fa", "IR"), Locale.ENGLISH);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) return "";

        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);

        try {
            String value = bundle.getString(key);
            if (params.length > 0) {
                return String.format(value, params);
            }
            return value;
        } catch (MissingResourceException e) {
            return "!" + key;
        }
    }
}
