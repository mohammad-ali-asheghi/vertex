package com.vertex.stellarui.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vertex.core.enums.ResponseCode;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import com.vertex.stellarui.auth.AuthManager;
import com.vertex.stellarui.constant.Routing;
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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Requester() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> T executeAndUnwrap(Supplier<RestResponse<T>> supplier) {
        RestResponse<T> response = execute(supplier);
        if (response != null && response.getData() != null) {
            return response.getData();
        }
        if (response != null && response.getMessage() != null && !response.getMessage().isBlank()
                && response.getCode() != ResponseCode.SUCCESS.getCode()) {
            Optional<UI> optionalUI = Optional.ofNullable(UI.getCurrent());
            optionalUI.ifPresent(ui -> ui.access(() -> Notifier.error(response.getMessage())));
        }
        return null;
    }

    public static <E> PagedResponse<E> executePaged(Supplier<PagedResponse<E>> supplier) {
        PagedResponse<E> response = execute(supplier);
        if (response != null && response.getResultList() != null) {
            return response;
        }
        return null;
    }

    public static boolean executeAndCheck(Supplier<ResponseMessage> supplier) {
        ResponseMessage response = execute(supplier);
        if (response != null && response.getCode() == ResponseCode.SUCCESS.getCode()) {
            return true;
        }
        if (response != null && response.getMessage() != null && !response.getMessage().isBlank()) {
            Optional<UI> optionalUI = Optional.ofNullable(UI.getCurrent());
            optionalUI.ifPresent(ui -> ui.access(() -> Notifier.error(response.getMessage())));
        }
        return false;
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
            String errorMsg = extractErrorMessage(ex);
            optionalUI.ifPresent(ui -> ui.access(() -> Notifier.error(errorMsg != null && !errorMsg.isBlank() ? errorMsg : INTERNAL_SERVER_ERROR)));
        }
    }

    public static String extractErrorMessage(Throwable ex) {
        if (ex == null) {
            return null;
        }
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            if (root instanceof ServiceException se && se.getMessage() != null && !se.getMessage().isBlank()) {
                return se.getMessage();
            }
            root = root.getCause();
        }
        if (root instanceof ServiceException se && se.getMessage() != null && !se.getMessage().isBlank()) {
            return se.getMessage();
        }
        if (root instanceof FeignException feignEx) {
            String content = feignEx.contentUTF8();
            if (content != null && !content.isBlank()) {
                try {
                    JsonNode node = OBJECT_MAPPER.readTree(content);
                    if (node.hasNonNull("message") && !node.get("message").asText().isBlank()) {
                        return node.get("message").asText();
                    }
                    if (node.hasNonNull("error") && !node.get("error").asText().isBlank()) {
                        return node.get("error").asText();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return root.getMessage();
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