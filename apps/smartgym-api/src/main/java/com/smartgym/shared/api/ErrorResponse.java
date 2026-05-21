package com.smartgym.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        int status,
        String error,
        String message,
        Map<String, List<String>> fieldErrors,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(false, status, error, message, null, path, Instant.now());
    }

    public static ErrorResponse withFieldErrors(
            int status,
            String error,
            String message,
            Map<String, List<String>> fieldErrors,
            String path) {
        return new ErrorResponse(false, status, error, message, fieldErrors, path, Instant.now());
    }
}
