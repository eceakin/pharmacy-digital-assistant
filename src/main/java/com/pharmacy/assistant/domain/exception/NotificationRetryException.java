package com.pharmacy.assistant.domain.exception;

public class NotificationRetryException extends RuntimeException {
    public NotificationRetryException(String message) {
        super(message);
    }

    public NotificationRetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
