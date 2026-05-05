package com.example.component;

import com.example.events.BookingFinalizedEvent;
import com.example.repo.ShowSeatRepo;
import com.example.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingResultListener {
    private final ShowSeatRepo showSeatRepo;
    private final ShowService showService;

    @KafkaListener(topics = "booking-finalized-events", groupId = "showtime-service-group")
    public void handleFinalization(BookingFinalizedEvent event){
        if(event.getFinalStatus().equals("CONFIRMED")){
            showService.updateShowSeatStatusToBooked(event.getShowSeatIds());
        }else{
            showService.revertUpdatedStatusAvailable(event.getShowSeatIds());
        }
    }
}
