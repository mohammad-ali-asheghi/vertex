package com.vertex.frontendcore.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Utility class for displaying beautiful UI notifications (Aura/Lumo styled).
 */
@SuppressWarnings("unused")
public final class Notifier {

    private Notifier() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Notification success(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        return notification;
    }

    public static Notification error(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return notification;
    }

    public static Notification loading(String message) {
        Notification notification = Notification.show(message, 0, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        return notification;
    }
}
