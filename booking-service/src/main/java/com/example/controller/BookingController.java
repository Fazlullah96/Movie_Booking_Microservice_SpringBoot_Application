package com.example.controller;

import com.example.dtos.BookingRequest;
import com.example.dtos.BookingResponse;
import com.example.service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> addBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody BookingRequest request
    ) throws JsonProcessingException {
        return new ResponseEntity<>(bookingService.createBooking(token, request), HttpStatus.CREATED);
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<BookingResponse> getBookingByBookingReference(@PathVariable String bookingReference){
        return new ResponseEntity<>(bookingService.getBookingByBookingReference(bookingReference), HttpStatus.OK);
    }
}
