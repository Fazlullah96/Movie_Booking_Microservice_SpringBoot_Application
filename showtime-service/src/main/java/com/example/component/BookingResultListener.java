package com.example.component;

import com.example.events.BookingFinalizedEvent;
import com.example.exceptions.ShowNotFoundException;
import com.example.models.Show;
import com.example.repo.ShowRepo;
import com.example.repo.ShowSeatRepo;
import com.example.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingResultListener {
    private final ShowSeatRepo showSeatRepo;
    private final ShowRepo showRepo;
    private final ShowService showService;

    @KafkaListener(topics = "booking-finalized-events", groupId = "showtime-service-group")
    public void finalizeShowSeat(BookingFinalizedEvent event){
        Show show = showRepo.findById(event.getShowId())
                .orElseThrow(() -> new ShowNotFoundException("Show not found for ShowId: " + event.getShowId()));

        if(event.getFinalStatus().equals("SUCCESS")){
            showService.updateShowSeatStatusToBooked(event.getShowSeatIds());
        }else{
            showService.revertUpdatedStatusAvailable(event.getShowSeatIds());
        }
    }
}
