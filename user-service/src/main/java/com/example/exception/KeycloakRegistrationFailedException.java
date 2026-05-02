package com.example.exception;

public class KeycloakRegistrationFailedException extends RuntimeException {
    public KeycloakRegistrationFailedException(String message) {
        super(message);
    }
}
