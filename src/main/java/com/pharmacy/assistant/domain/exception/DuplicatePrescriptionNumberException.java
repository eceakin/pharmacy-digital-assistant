package com.pharmacy.assistant.domain.exception;

public class DuplicatePrescriptionNumberException extends RuntimeException {
    public DuplicatePrescriptionNumberException(String message) {
        super(message);
    }
}
