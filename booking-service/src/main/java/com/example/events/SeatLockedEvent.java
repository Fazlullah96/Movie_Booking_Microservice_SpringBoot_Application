package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatLockedEvent {
    private String bookingId;
    private Integer seatId;
    private String status; // "LOCKED" or "FAILED"
    private String message;
}
