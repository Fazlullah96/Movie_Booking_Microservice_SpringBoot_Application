package com.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowSeatResponse {
    private Integer id;
    private Integer seatId;
    private Double price;
    private String status;

//    enum Status{
//        AVAILABLE,
//        LOCKED,
//        BOOKED
//    }
}
