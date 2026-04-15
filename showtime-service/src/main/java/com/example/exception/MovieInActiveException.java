package com.example.exception;

public class MovieInActiveException extends RuntimeException {
    public MovieInActiveException(String message) {
        super(message);
    }
}
