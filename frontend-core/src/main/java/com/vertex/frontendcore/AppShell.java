package com.vertex.frontendcore;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.aura.Aura;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Vertex Base for Vaadin", shortName = "Vertex Base")
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "logo.png", "32x32");
        settings.addLink("apple-touch-icon", "logo.png");
    }
}
