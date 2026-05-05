package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCompletedEvent {
    private String bookingReference;
    private Integer paymentId;
    private String transactionId;
    private String status; // "SUCCESS" or "FAILED"
    private String message;
}
