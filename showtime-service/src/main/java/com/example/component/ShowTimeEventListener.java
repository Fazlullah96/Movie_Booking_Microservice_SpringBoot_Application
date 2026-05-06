package com.example.component;

import com.example.events.BookingCreatedEvent;
import com.example.events.SeatLockedEvent;
import com.example.models.OutBoxEvent;
import com.example.repo.OutBoxEventRepo;
import com.example.service.ShowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowTimeEventListener {
    private final OutBoxEventRepo outBoxEventRepo;
    private final ShowService showService;
    private final ObjectMapper objectMapper;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private static final  String SHOWTIME_TOPIC = "seat-events";

    @KafkaListener(topics = "booking-events", groupId = "showtime-service-group")
    public void handleBookingCreatedEvent(BookingCreatedEvent event) throws JsonProcessingException {

        SeatLockedEvent replyEvent = SeatLockedEvent
                .builder()
                .bookingReference(event.getBookingReference())
                .showSeatIds(event.getShowSeatIds())
                .build();

        try {
            showService.updateShowSeatStatusToLocked(event.getShowSeatIds());
            replyEvent.setStatus("SUCCESS");
            replyEvent.setMessage("SEAT LOCKED SUCCESSFULLY IN DB");
        }catch (Exception e){
            replyEvent.setStatus("FAILED");
            replyEvent.setMessage(e.getMessage());
        }

        OutBoxEvent outBoxEvent = OutBoxEvent
                .builder()
                .aggregateType("SEAT")
                .aggregateId(event.getBookingReference())
                .topic("seat-events")
                .payload(objectMapper.writeValueAsString(replyEvent))
                .status(false)
                .build();
        outBoxEventRepo.save(outBoxEvent);
    }
}
