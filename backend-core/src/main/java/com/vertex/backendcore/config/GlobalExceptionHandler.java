package com.vertex.backendcore.config;

import com.vertex.core.config.MessageProvider;
import com.vertex.core.enums.ResponseCode;
import com.vertex.core.exceptions.BindingException;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@AutoConfiguration
@RestControllerAdvice
@ConditionalOnMissingBean(name = "oauthGlobalExceptionHandler")
public class GlobalExceptionHandler {

    private static final String FORBIDDEN_MESSAGE = "شما اجازه دسترسی به این بخش را ندارید.";
    private static final String INTERNAL_SERVER_ERROR = "خطایی در سرور رخ داده است";

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RestResponse<Void>> handleServiceException(ServiceException ex) {
        log.warn("Service exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(ex.getMessage(), ResponseCode.FAIL.getCode()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        String message = MessageProvider.getMessageByKey("Unauthorized");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(message, ResponseCode.FAIL.getCode()));
    }

    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<RestResponse<Void>> handleAccountStatusException(AuthenticationException ex) {
        log.warn("Account locked or disabled: {}", ex.getMessage());
        String message = MessageProvider.getMessageByKey("AccountDisabledOrLocked");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(message, ResponseCode.FAIL.getCode()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        String message = MessageProvider.getMessageByKey("Unauthorized");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(message, ResponseCode.FAIL.getCode()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RestResponse.error(FORBIDDEN_MESSAGE, ResponseCode.UNAUTHORIZE.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation error: {}", errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(errorMsg, ResponseCode.FAIL.getCode()));
    }

    @ExceptionHandler(BindingException.class)
    public ResponseEntity<RestResponse<Void>> handleBindingException(BindingException ex) {
        log.warn("Binding exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(ex.getMessage(), ResponseCode.FAIL.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled server exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RestResponse.error(INTERNAL_SERVER_ERROR, ResponseCode.FAIL.getCode()));
    }
}
