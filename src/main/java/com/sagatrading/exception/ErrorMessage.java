package com.sagatrading.exception;

public class ErrorMessage {
    private final String message;
    private final int status;

    public ErrorMessage(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
