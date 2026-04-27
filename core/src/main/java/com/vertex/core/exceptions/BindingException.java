package com.vertex.core.exceptions;


import com.vertex.core.config.MessageProvider;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.StringUtil;
import lombok.Getter;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Getter
public class BindingException extends RuntimeException {

    private final ArrayList<ResponseMessage> errors = new ArrayList<>();

    public BindingException() {
        super();
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, int code) {
        super(message);
        addError(code, message);
    }

    public BindingException(int code, String message) {
        super(message);
        addError(code, message);
    }

    public void addError(int code, String errorMessage) {
        String oldErrorMessage = errorMessage;
        try {
            errorMessage = MessageProvider.getMessageByKey(errorMessage);
        } catch (Exception ignored) {
        }
        errorMessage = StringUtil.isEmpty(errorMessage) ? oldErrorMessage : errorMessage;
        this.errors.add(new ResponseMessage(errorMessage, code));
    }
}
