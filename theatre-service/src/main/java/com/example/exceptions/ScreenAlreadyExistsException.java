package com.example.exceptions;

public class ScreenAlreadyExistsException extends RuntimeException {
    public ScreenAlreadyExistsException(String message) {
        super(message);
    }
}
