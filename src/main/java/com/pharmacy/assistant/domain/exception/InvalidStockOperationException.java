package com.pharmacy.assistant.domain.exception;

public class InvalidStockOperationException extends RuntimeException {
    public InvalidStockOperationException(String message) {
        super(message);
    }

    public InvalidStockOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
