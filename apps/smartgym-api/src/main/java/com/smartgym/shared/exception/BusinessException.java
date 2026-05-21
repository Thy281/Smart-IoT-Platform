package com.smartgym.shared.exception;

/**
 * Base class for domain-level business rule violations.
 * Subclasses map to specific HTTP status codes in the GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
