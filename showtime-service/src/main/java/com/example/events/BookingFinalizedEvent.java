package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingFinalizedEvent {
    private String bookingReference;
    private List<Integer> showSeatIds;
    private String finalStatus;
}
