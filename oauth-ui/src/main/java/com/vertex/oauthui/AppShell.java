package com.vertex.oauthui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.aura.Aura;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Vertex Base for Vaadin", shortName = "vertex")
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class AppShell implements AppShellConfigurator {
}
