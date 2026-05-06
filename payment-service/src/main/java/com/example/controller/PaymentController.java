package com.example.controller;

import com.example.dtos.PaymentRequest;
import com.example.dtos.PaymentResponse;
import com.example.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentRequest request
            ) throws JsonProcessingException {
        return new ResponseEntity<>(paymentService.initiatePayment(token, request), HttpStatus.CREATED);
    }
}
