package com.example.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "show_seat")
public class ShowSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "show_id")
    @ToString.Exclude
    private Show show;
    private Integer seatId;
    private Double price;
    private Status status;


    private enum Status{
        AVAILABLE,
        LOCKED,
        BOOKED
    }
}
