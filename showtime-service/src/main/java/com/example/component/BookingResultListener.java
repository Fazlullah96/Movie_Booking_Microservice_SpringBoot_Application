package com.example.component;

import com.example.events.BookingFinalizedEvent;
import com.example.exceptions.ShowNotFoundException;
import com.example.models.Show;
import com.example.repo.ShowRepo;
import com.example.repo.ShowSeatRepo;
import com.example.service.ShowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingResultListener {
    private final ShowSeatRepo showSeatRepo;
    private final ShowRepo showRepo;
    private final ShowService showService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "booking-finalized-events", groupId = "showtime-service-group")
    public void finalizeShowSeat(String eventPayload) throws JsonProcessingException {
        BookingFinalizedEvent event = objectMapper.readValue(eventPayload, BookingFinalizedEvent.class);
        Show show = showRepo.findById(event.getShowId())
                .orElseThrow(() -> new ShowNotFoundException("Show not found for ShowId: " + event.getShowId()));

        if(event.getFinalStatus().equals("SUCCESS")){
            showService.updateShowSeatStatusToBooked(event.getShowSeatIds());
        }else{
            showService.revertUpdatedStatusAvailable(event.getShowSeatIds());
        }
    }
}
