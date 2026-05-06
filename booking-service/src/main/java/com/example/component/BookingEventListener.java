package com.example.component;

import com.example.events.SeatLockedEvent;
import com.example.exceptions.BookingNotFoundException;
import com.example.model.Booking;
import com.example.repo.BookingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {
    private final BookingRepo bookingRepo;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @KafkaListener(topics = "seat-events", groupId = "booking-service-group")
    public void handleBookingEvent(SeatLockedEvent event){
        Booking booking = bookingRepo.findByBookingReference(event.getBookingReference())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for BookingReference: " + event.getBookingReference()));

        if(event.getStatus().equals("SUCCESS")){
            booking.setBookingStatus(Booking.Status.valueOf("AWAITING_PAYMENT"));
            Booking savedBooking = bookingRepo.save(booking);
        }else{
            booking.setBookingStatus(Booking.Status.valueOf("CANCELLED"));
            bookingRepo.save(booking);

            for(Integer showSeatId : event.getShowSeatIds()){
                redisTemplate.delete("SEAT:LOCK:"+showSeatId);
            }
        }
    }
}
