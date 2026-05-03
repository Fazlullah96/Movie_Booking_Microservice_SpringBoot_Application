package com.example.exception;

public class ShowSeatStatusException extends RuntimeException {
    public ShowSeatStatusException(String message) {
        super(message);
    }
}
