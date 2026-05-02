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
        VaadinSession.getCurrent().setAttribute(Constant.AUTH_TOKEN, null);
    }

    public static void saveToken(VaadinSession session, String token) {
        session.setAttribute(Constant.AUTH_TOKEN, token);
    }

    public static String getToken() {
        Object token = VaadinSession.getCurrent().getAttribute(Constant.AUTH_TOKEN);
        return token != null ? token.toString() : null;
    }

    public static void redirectToLogin(UI ui) {
        clearToken();
        ui.navigate(Routing.LOGIN);
    }

    public static void saveMenus(VaadinSession session, List<ViewMenuDto> menus) {
        session.setAttribute(Constant.AUTH_MENU, menus);
    }

    @SuppressWarnings("unchecked")
    public static List<ViewMenuDto> getMenus() {
        Object menus = VaadinSession.getCurrent().getAttribute(Constant.AUTH_MENU);

        if (menus instanceof List<?>) {
            return (List<ViewMenuDto>) menus;
        }

        log.warn("menus are not present in session or in valid format!");
        return new ArrayList<>();
    }
}