package com.example.service;

import com.example.clients.BookingClient;
import com.example.dtos.BookingResponse;
import com.example.dtos.PaymentRequest;
import com.example.dtos.PaymentResponse;
import com.example.events.PaymentCompletedEvent;
import com.example.exceptions.BookingStatusException;
import com.example.model.OutBoxEvent;
import com.example.model.Payment;
import com.example.repo.OutBoxEventRepo;
import com.example.repo.PaymentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final BookingClient bookingClient;
    private final ObjectMapper objectMapper;
    private final OutBoxEventRepo outBoxEventRepo;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private static final String PAYMENT_TOPIC = "payment-result-events";

    @Transactional
    public PaymentResponse initiatePayment(String token, PaymentRequest request) throws JsonProcessingException {
        BookingResponse booking = bookingClient.findBookingByBookingReference(token, request.getBookingReference());
        if(!booking.getStatus().name().equals("AWAITING_PAYMENT")){
            throw new BookingStatusException("Your Booking Status is " + booking.getStatus().name() + " not AWAITING_PAYMENT");
        }
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        boolean isSuccessful = Math.random() > 0.1;
        Payment payment = Payment
                .builder()
                .bookingReference(booking.getBookingReference())
                .transactionId(transactionId)
                .userId(booking.getUserId())
                .amount(booking.getTotalAmount())
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(Payment.PaymentStatus.valueOf(isSuccessful? "SUCCESS" : "FAILED"))
                .build();
        Payment savedPayment = paymentRepo.save(payment);

        PaymentCompletedEvent event = PaymentCompletedEvent
                .builder()
                .paymentId(savedPayment.getId())
                .bookingReference(savedPayment.getBookingReference())
                .transactionId(savedPayment.getTransactionId())
                .status(isSuccessful ? "SUCCESS" : "FAILED")
                .message(isSuccessful ? "PAYMENT IS SUCCESS" : "PAYMENT IS FAILURE")
                .build();

        OutBoxEvent outBoxEvent = OutBoxEvent
                .builder()
                .aggregateType("PAYMENT")
                .aggregateId(savedPayment.getBookingReference())
                .topic("payment-result-events")
                .payload(objectMapper.writeValueAsString(event))
                .status(false)
                .build();
        outBoxEventRepo.save(outBoxEvent);
        return PaymentResponse
                .builder()
                .paymentId(savedPayment.getId())
                .bookingId(booking.getBookingId())
                .transactionId(savedPayment.getTransactionId())
                .status(String.valueOf(savedPayment.getPaymentStatus()))
                .message(savedPayment.getPaymentStatus().name().equals("SUCCESS") ? "PAYMENT SUCCESSFUL" : "PAYMENT FAILED")
                .build();
    }
}
