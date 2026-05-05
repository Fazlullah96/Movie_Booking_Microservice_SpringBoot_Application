package com.example.events;

import com.example.exceptions.BookingNotFoundException;
import com.example.model.Booking;
import com.example.repo.BookingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {
    private final BookingRepo bookingRepo;
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String PAYMENT_TOPIC = "payment-events";

    @KafkaListener(topics = "seat-events", groupId = "booking-service-group")
    public void handleBookingEvent(SeatLockedEvent event){
        Booking booking = bookingRepo.findByBookingReference(event.getBookingReference())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for BookingReference: " + event.getBookingReference()));

        if(event.getStatus().equals("SUCCESS")){
            booking.setBookingStatus(Booking.Status.valueOf("AWAITING_PAYMENT"));
            Booking savedBooking = bookingRepo.save(booking);
            PaymentInitiatedEvent paymentEvent = PaymentInitiatedEvent
                    .builder()
                    .bookingReference(savedBooking.getBookingReference())
                    .userId(savedBooking.getUserId())
                    .amount(savedBooking.getTotalAmount())
                    .paymentMethod("UPI")
                    .build();
            kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent.getBookingReference(), paymentEvent);
        }else{
            booking.setBookingStatus(Booking.Status.valueOf("CANCELLED"));
            bookingRepo.save(booking);

            for(Integer showSeatId : event.getShowSeatIds()){
                redisTemplate.delete("SEAT:LOCK:"+showSeatId);
            }
        }
    }
}
