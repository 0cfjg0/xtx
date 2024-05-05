package com.itheima.xiaotuxian.exception;

public class LoginException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int status;

    public LoginException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
