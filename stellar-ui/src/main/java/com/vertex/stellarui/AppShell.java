package com.vertex.stellarui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Vaadin AppShell configuration utilizing standard Lumo theme.
 */
@Push
@PWA(name = "Vertex Management Platform", shortName = "Vertex")
@Theme(themeClass = Lumo.class)
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "logo.png", "32x32");
        settings.addLink("apple-touch-icon", "logo.png");
    }
}
