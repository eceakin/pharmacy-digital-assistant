package com.pharmacy.assistant.domain.exception;

public class DuplicateBatchException extends RuntimeException {
    public DuplicateBatchException(String message) {
        super(message);
    }
}
