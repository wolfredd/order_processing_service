package com.sagatrading.controller;

import com.sagatrading.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler({InvalidOrderException.class})
    public final ResponseEntity<ErrorMessage> handleUnknownProductException(Exception ex, WebRequest request) {
        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public final ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(Exception ex, WebRequest request) {
        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({OrderQuantityExceedsLimitException.class})
    public final ResponseEntity<ErrorMessage> handleOrderQuantityExceedsLimitException(Exception ex, WebRequest request) {
        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({InvalidOrderPriceException.class})
    public final ResponseEntity<ErrorMessage> handleInvalidOrderPriceException(Exception ex, WebRequest request) {
        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({InsufficientStockException.class})
    public final ResponseEntity<ErrorMessage> handleInsufficientStockException(Exception ex, WebRequest request) {
        int status = HttpStatus.CONFLICT.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({InsufficientBalanceException.class})
    public final ResponseEntity<ErrorMessage> handleInsufficientBalanceException(Exception ex, WebRequest request) {
        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({ProductNotFoundException.class})
    public final ResponseEntity<ErrorMessage> handleProductNotFoundException(Exception ex, WebRequest request) {
        int status = HttpStatus.NOT_FOUND.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({OrderNotFoundException.class})
    public final ResponseEntity<ErrorMessage> handleOrderNotFoundException(Exception ex, WebRequest request) {
        int status = HttpStatus.NOT_FOUND.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }

    @ExceptionHandler({FailedOrderException.class})
    public final ResponseEntity<ErrorMessage> handleFailedOrderException(Exception ex, WebRequest request) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ResponseEntity.status(status).body(new ErrorMessage(ex.getMessage(), status));
    }
}
