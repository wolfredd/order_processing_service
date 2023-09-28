package com.sagatrading.exception;

public class OrderQuantityExceedsLimitException extends RuntimeException{

    public OrderQuantityExceedsLimitException(String message) {
        super(message);
    }
}
