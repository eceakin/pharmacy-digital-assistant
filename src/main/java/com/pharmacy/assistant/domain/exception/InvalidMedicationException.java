package com.pharmacy.assistant.domain.exception;

public class InvalidMedicationException extends RuntimeException {
  public InvalidMedicationException(String message) {
    super(message);
  }
}
