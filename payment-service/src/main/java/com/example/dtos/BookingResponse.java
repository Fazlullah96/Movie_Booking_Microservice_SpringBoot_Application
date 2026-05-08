package com.example.dtos;

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
    private String transactionId;
    private String userId;
    private Integer showId;
    private Double totalAmount;
    private Status status;
    private LocalDateTime bookingTime;
    private List<BookedSeatInfo> bookedSeats;

    public enum Status{
        PENDING,
        SUCCESS,
        CANCELLED,
        AWAITING_PAYMENT
    }
}
