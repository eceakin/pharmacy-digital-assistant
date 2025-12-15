package com.pharmacy.assistant.domain.exception;

public class MedicationNotFoundException extends RuntimeException {
  public MedicationNotFoundException(String message) {
    super(message);
  }

  public MedicationNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
