package com.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatResponse {
    private Integer id;
    private Integer seatId;
    private Double price;
    private Status status;

    enum Status{
        AVAILABLE,
        LOCKED,
        BOOKED
    }
}
