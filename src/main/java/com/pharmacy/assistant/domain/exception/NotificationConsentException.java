package com.pharmacy.assistant.domain.exception;

public class NotificationConsentException extends RuntimeException {
    public NotificationConsentException(String message) {
        super(message);
    }

    public NotificationConsentException(String message, Throwable cause) {
        super(message, cause);
    }
}
