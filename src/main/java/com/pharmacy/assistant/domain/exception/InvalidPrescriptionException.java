package com.pharmacy.assistant.domain.exception;

// InvalidPrescriptionException.java
public class InvalidPrescriptionException extends RuntimeException {
    public InvalidPrescriptionException(String message) {
        super(message);
    }
}
