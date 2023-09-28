package com.sagatrading.exception;

public class InvalidOrderPriceException extends RuntimeException{
    public InvalidOrderPriceException(String message) {
        super(message);
    }
}
