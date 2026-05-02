package com.vertex.frontendcore.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            UI ui = uiEvent.getUI();

            ui.setLocale(Locale.of("fa", "IR"));
            ui.getElement().setAttribute("dir", "rtl");
            ui.getElement().setAttribute("lang", "fa");

            ui.getPage().addStyleSheet("frontend/styles/core.css");
            ui.getElement().getStyle().set("font-family", "Vazirmatn, sans-serif");
        });
    }
}
