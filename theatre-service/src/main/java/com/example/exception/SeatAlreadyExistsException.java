package com.example.exception;

public class SeatAlreadyExistsException extends RuntimeException {
    public SeatAlreadyExistsException(String message) {
        super(message);
    }
}
