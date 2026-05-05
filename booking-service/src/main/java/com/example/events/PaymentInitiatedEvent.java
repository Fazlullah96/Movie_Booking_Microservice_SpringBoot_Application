package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiatedEvent {
    private String bookingReference;
    private String userId;
    private Double amount;
    private String paymentMethod;
}
