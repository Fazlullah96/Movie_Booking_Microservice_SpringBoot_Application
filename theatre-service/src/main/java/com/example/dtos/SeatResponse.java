package com.example.dtos;

import com.example.models.Seat;
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
    private String seatRow;
    private Integer seatNumber;
    private Seat.SeatType seatType;
    private ScreenResponse screenResponse;
}
