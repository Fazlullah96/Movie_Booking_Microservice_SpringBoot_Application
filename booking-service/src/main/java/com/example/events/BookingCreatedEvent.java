package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCreatedEvent {
    private String bookingId;
    private Integer showId;
    private Integer seatId;
    private String userId;
}
