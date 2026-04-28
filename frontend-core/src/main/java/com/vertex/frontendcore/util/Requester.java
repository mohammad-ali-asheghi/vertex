package com.vertex.frontendcore.util;

import com.vaadin.flow.component.UI;
import com.vertex.frontendcore.auth.AuthManager;
import com.vertex.frontendcore.constant.Routing;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@SuppressWarnings("unused")
public final class Requester {

    private static final String UNAUTHORIZED_MESSAGE = "نشست کاربری شما پایان یافته است. لطفاً دوباره وارد شوید.";
    private static final String FORBIDDEN_MESSAGE = "شما اجازه دسترسی به این بخش را ندارید.";
    private static final String INTERNAL_SERVER_ERROR = "خطایی در برقراری ارتباط با سرور رخ داد.";

    private Requester() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> T execute(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            handleException(ex);
            return null;
        }
    }

    private static void handleException(Exception ex) {
        log.error("API Execution failed: {}", ex.getMessage(), ex);
        Optional<UI> optionalUI = Optional.ofNullable(UI.getCurrent());
        if (isUnauthorized(ex)) {
            AuthManager.clearToken();
            optionalUI.ifPresent(ui -> ui.access(() -> {
                Notifier.error(UNAUTHORIZED_MESSAGE);
                ui.navigate(Routing.LOGIN);
            }));
        } else if (isForbidden(ex)) {
            optionalUI.ifPresent(ui -> ui.access(() -> Notifier.error(FORBIDDEN_MESSAGE)));
        } else {
            optionalUI.ifPresent(ui -> ui.access(() -> Notifier.error(INTERNAL_SERVER_ERROR)));
        }
    }

    public static boolean isUnauthorized(Throwable ex) {
        switch (ex) {
            case null -> {
                return false;
            }
            case FeignException.Unauthorized feign -> {
                return true;
            }
            case HttpClientErrorException.Unauthorized unauthorized -> {
                return true;
            }
            case HttpStatusCodeException hsc -> {
                return hsc.getStatusCode() == HttpStatus.UNAUTHORIZED;
            }
            default -> {
            }
        }
        return ex.getCause() != null && ex.getCause() != ex && isUnauthorized(ex.getCause());
    }

    public static boolean isForbidden(Throwable ex) {
        switch (ex) {
            case null -> {
                return false;
            }
            case FeignException.Forbidden feign -> {
                return true;
            }
            case HttpClientErrorException.Forbidden forbidden -> {
                return true;
            }
            case HttpStatusCodeException hsc -> {
                return hsc.getStatusCode() == HttpStatus.FORBIDDEN;
            }
            default -> {
            }
        }
        return ex.getCause() != null && ex.getCause() != ex && isForbidden(ex.getCause());
    }
}