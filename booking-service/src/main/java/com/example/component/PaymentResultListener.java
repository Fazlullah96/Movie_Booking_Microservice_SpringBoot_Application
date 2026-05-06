package com.example.component;

import com.example.events.BookingFinalizedEvent;
import com.example.events.PaymentCompletedEvent;
import com.example.exceptions.BookingNotFoundException;
import com.example.model.Booking;
import com.example.model.OutBoxEvent;
import com.example.repo.BookingRepo;
import com.example.repo.BookingSeatRepo;
import com.example.repo.OutBoxEventRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentResultListener {
    private final BookingRepo bookingRepo;
    private final BookingSeatRepo bookingSeatRepo;
    private final ObjectMapper objectMapper;
    private final OutBoxEventRepo outBoxEventRepo;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private static final String BOOKING_FINALIZED_TOPIC = "booking-finalized-events";

    @Transactional
    @KafkaListener(topics = "payment-result-events", groupId = "booking-service-group")
    public void finalizeBooking(PaymentCompletedEvent event) throws JsonProcessingException {
        Booking booking = bookingRepo.findByBookingReference(event.getBookingReference())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for BookingReference: " + event.getBookingReference()));


        if(booking.getBookingStatus() == Booking.Status.SUCCESS || booking.getBookingStatus() == Booking.Status.CANCELLED){
            return;
        }

        BookingFinalizedEvent finalizedEvent = BookingFinalizedEvent
                .builder()
                .bookingReference(event.getBookingReference())
                .showId(booking.getShowId())
                .showSeatIds(bookingRepo.findAllBookingSeatIdByBookingReference(event.getBookingReference()))
                .build();

        if(event.getStatus().equals("SUCCESS")){
            booking.setBookingStatus(Booking.Status.valueOf("SUCCESS"));
            bookingRepo.save(booking);
            finalizedEvent.setFinalStatus("SUCCESS");
        }else{
            booking.setBookingStatus(Booking.Status.valueOf("CANCELLED"));
            bookingRepo.save(booking);
            finalizedEvent.setFinalStatus("FAILURE");
        }

        OutBoxEvent outBoxEvent = OutBoxEvent
                .builder()
                .aggregateType("BOOKING")
                .aggregateId(event.getBookingReference())
                .topic("booking-finalized-events")
                .payload(objectMapper.writeValueAsString(finalizedEvent))
                .status(false)
                .build();
        outBoxEventRepo.save(outBoxEvent);
    }
}
