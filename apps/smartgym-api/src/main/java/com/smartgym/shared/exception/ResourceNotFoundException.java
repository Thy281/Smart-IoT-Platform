package com.smartgym.shared.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
