package com.example.exceptions;

public class ShowSeatStatusException extends RuntimeException {
    public ShowSeatStatusException(String message) {
        super(message);
    }
}
