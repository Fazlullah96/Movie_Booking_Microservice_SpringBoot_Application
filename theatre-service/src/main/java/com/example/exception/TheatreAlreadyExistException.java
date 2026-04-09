package com.example.exception;

public class TheatreAlreadyExistException extends RuntimeException {
    public TheatreAlreadyExistException(String message) {
        super(message);
    }
}
