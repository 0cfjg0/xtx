package com.itheima.xiaotuxian.exception;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private final ErrorMessageEnum errorMessage;

    public BusinessException(ErrorMessageEnum errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ErrorMessageEnum getErrorMessage() {
        return errorMessage;
    }
}
