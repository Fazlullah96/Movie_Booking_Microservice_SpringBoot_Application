package com.example.exceptions;

public class BookingStatusException extends RuntimeException {
    public BookingStatusException(String message) {
        super(message);
    }
}
