package com.example.dtos;

import com.example.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Integer bookingId;
    private String bookingReference;
    private Integer showId;
    private Double totalAmount;
    private Booking.Status status;
    private LocalDateTime bookingTime;
    private List<BookedSeatInfo> bookedSeats;
}
