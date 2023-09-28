package com.sagatrading.exception;

public class FailedOrderException extends RuntimeException {

    public FailedOrderException(String message) {
        super(message);
    }
}
