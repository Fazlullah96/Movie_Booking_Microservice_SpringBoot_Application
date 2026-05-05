package com.example.component;

import com.example.events.PaymentCompletedEvent;
import com.example.events.PaymentInitiatedEvent;
import com.example.model.Payment;
import com.example.repo.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentListener {
    private final PaymentRepo paymentRepo;
    private static final String PAYMENT_RESULT_TOPIC = "payment-result-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-events", groupId = "payment-group")
    public void ProcessPayment(PaymentInitiatedEvent event){
        Payment payment = Payment
                .builder()
                .bookingReference(event.getBookingReference())
                .userId(event.getUserId())
                .amount(event.getAmount())
                .paymentMethod(Payment.PaymentMethod.valueOf(event.getPaymentMethod()))
                .paymentStatus(Payment.PaymentStatus.valueOf("PENDING"))
                .build();
        Payment savedPayment = paymentRepo.save(payment);

        boolean isSuccess = Math.random() > 0.1;
        if(isSuccess){
            savedPayment.setPaymentStatus(Payment.PaymentStatus.valueOf("SUCCESS"));
            savedPayment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
        }else{
            savedPayment.setPaymentStatus(Payment.PaymentStatus.valueOf("FAILED"));
        }

        paymentRepo.save(savedPayment);

        PaymentCompletedEvent completedEvent = PaymentCompletedEvent
                .builder()
                .bookingReference(savedPayment.getBookingReference())
                .paymentId(savedPayment.getId())
                .transactionId(savedPayment.getTransactionId())
                .status(savedPayment.getPaymentStatus().name())
                .message(isSuccess ? "SUCCESS" : "FAILED")
                .build();

        kafkaTemplate.send(PAYMENT_RESULT_TOPIC, completedEvent.getBookingReference(), completedEvent);
    }

}
