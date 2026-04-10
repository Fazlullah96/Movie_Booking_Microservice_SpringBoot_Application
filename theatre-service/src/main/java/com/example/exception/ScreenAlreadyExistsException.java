package com.example.exception;

public class ScreenAlreadyExistsException extends RuntimeException {
    public ScreenAlreadyExistsException(String message) {
        super(message);
    }
}
