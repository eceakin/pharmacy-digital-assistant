package com.pharmacy.assistant.infrastructure.adapter.web.common;

import com.pharmacy.assistant.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Patient Exceptions
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePatientNotFound(PatientNotFoundException ex) {
        log.error("Patient not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Product Exceptions
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductNotFound(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateBarcodeException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateBarcode(DuplicateBarcodeException ex) {
        log.error("Duplicate barcode: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidProductDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidProductData(InvalidProductDataException ex) {
        log.error("Invalid product data: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Stock Exceptions
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleStockNotFound(StockNotFoundException ex) {
        log.error("Stock not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Insufficient stock: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateBatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateBatch(DuplicateBatchException ex) {
        log.error("Duplicate batch: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidStockOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidStockOperation(InvalidStockOperationException ex) {
        log.error("Invalid stock operation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Medication Exceptions
    @ExceptionHandler(MedicationNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMedicationNotFound(MedicationNotFoundException ex) {
        log.error("Medication not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidMedicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidMedication(InvalidMedicationException ex) {
        log.error("Invalid medication: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Prescription Exceptions
    @ExceptionHandler(PrescriptionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePrescriptionNotFound(PrescriptionNotFoundException ex) {
        log.error("Prescription not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicatePrescriptionNumberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicatePrescriptionNumber(DuplicatePrescriptionNumberException ex) {
        log.error("Duplicate prescription number: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPrescriptionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPrescription(InvalidPrescriptionException ex) {
        log.error("Invalid prescription: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Notification Exceptions
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationNotFound(NotificationNotFoundException ex) {
        log.error("Notification not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidNotificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidNotification(InvalidNotificationException ex) {
        log.error("Invalid notification: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(NotificationSendException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationSend(NotificationSendException ex) {
        log.error("Notification send error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(NotificationRetryException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationRetry(NotificationRetryException ex) {
        log.error("Notification retry error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(NotificationConsentException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationConsent(NotificationConsentException ex) {
        log.error("Notification consent error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation errors: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Doğrulama hatası", errors, null));
    }

    // Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Bir hata oluştu: " + ex.getMessage()));
    }
}