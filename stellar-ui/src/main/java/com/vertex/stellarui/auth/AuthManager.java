package com.vertex.stellarui.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vertex.core.dto.view.ViewMenuDto;
import com.vertex.stellarui.constant.Constant;
import com.vertex.stellarui.constant.Routing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class AuthManager {

    private static final Logger log = LoggerFactory.getLogger(AuthManager.class);

    private AuthManager() {
    }

    public static void clearToken() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(Constant.AUTH_TOKEN, null);
            session.setAttribute(Constant.AUTH_REFRESH_TOKEN, null);
            session.setAttribute(Constant.AUTH_USERNAME, null);
            session.setAttribute(Constant.AUTH_MENU, null);
        }
    }

    public static void saveToken(VaadinSession session, String token) {
        if (session != null) {
            session.setAttribute(Constant.AUTH_TOKEN, token);
        }
    }

    public static void saveTokens(VaadinSession session, String accessToken, String refreshToken, String username) {
        if (session != null) {
            session.setAttribute(Constant.AUTH_TOKEN, accessToken);
            session.setAttribute(Constant.AUTH_REFRESH_TOKEN, refreshToken);
            session.setAttribute(Constant.AUTH_USERNAME, username);
        }
    }

    public static String getToken() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return null;
        Object token = session.getAttribute(Constant.AUTH_TOKEN);
        return token != null ? token.toString() : null;
    }

    public static String getRefreshToken() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return null;
        Object refreshToken = session.getAttribute(Constant.AUTH_REFRESH_TOKEN);
        return refreshToken != null ? refreshToken.toString() : null;
    }

    public static String getUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return "admin";
        Object username = session.getAttribute(Constant.AUTH_USERNAME);
        return username != null ? username.toString() : "admin";
    }

    public static boolean isAuthenticated() {
        String token = getToken();
        return token != null && !token.isBlank();
    }

    public static void redirectToLogin(UI ui) {
        clearToken();
        if (ui != null) {
            ui.navigate(Routing.LOGIN);
        }
    }

    public static void saveMenus(VaadinSession session, List<ViewMenuDto> menus) {
        if (session != null) {
            session.setAttribute(Constant.AUTH_MENU, menus);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<ViewMenuDto> getMenus() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return new ArrayList<>();
        Object menus = session.getAttribute(Constant.AUTH_MENU);

        if (menus instanceof List<?>) {
            return (List<ViewMenuDto>) menus;
        }

        return new ArrayList<>();
    }
}