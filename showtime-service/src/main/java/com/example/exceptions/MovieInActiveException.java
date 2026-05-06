package com.example.exceptions;

public class MovieInActiveException extends RuntimeException {
    public MovieInActiveException(String message) {
        super(message);
    }
}
