package org.speech4j.contentservice.exception;

public class TenantNotFoundException extends EntityNotFoundException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}
