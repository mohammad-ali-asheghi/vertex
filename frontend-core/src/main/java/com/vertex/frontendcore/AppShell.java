package com.vertex.frontendcore;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@Push
@PWA(name = "Vertex Base for Vaadin", shortName = "Vertex Base")
@StyleSheet("styles.css")
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "logo.png", "32x32");
        settings.addLink("apple-touch-icon", "logo.png");
    }
}
