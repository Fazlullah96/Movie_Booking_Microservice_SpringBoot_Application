package com.example.exception;

import com.example.error.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MovieInActiveException.class)
    public ResponseEntity<ErrorMessage> movieInActiveException(
            MovieInActiveException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorMessage> movieNotFoundException(
            MovieNotFoundException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ScreenNotFoundException.class)
    public ResponseEntity<ErrorMessage> screenNotFoundException(
            ScreenNotFoundException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ShowNotFoundException.class)
    public ResponseEntity<ErrorMessage> showNotFoundException(
            ShowNotFoundException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorMessage> serviceUnavailableException(
            ServiceUnavailableException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ShowSeatNotFoundException.class)
    public ResponseEntity<ErrorMessage> showSeatNotFoundException(
            ShowSeatNotFoundException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShowSeatStatusException.class)
    public ResponseEntity<ErrorMessage> showSeatStatusException(
            ShowSeatStatusException ex,
            HttpServletRequest request
    ){
        ErrorMessage error = ErrorMessage
                .builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
