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
public class SeatLockedEvent {
    private String bookingReference;
    private List<Integer> showSeatIds;
    private String status; // "LOCKED" or "FAILED"
    private String message;
}
