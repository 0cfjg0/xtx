package com.itheima.xiaotuxian.exception;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;

public class AuthException extends RuntimeException {
    private final ErrorMessageEnum errorMessage;

    public AuthException(ErrorMessageEnum errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ErrorMessageEnum getErrorMessage() {
        return errorMessage;
    }
}
