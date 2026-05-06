package com.example.exceptions;

public class TheatreAlreadyExistException extends RuntimeException {
    public TheatreAlreadyExistException(String message) {
        super(message);
    }
}
