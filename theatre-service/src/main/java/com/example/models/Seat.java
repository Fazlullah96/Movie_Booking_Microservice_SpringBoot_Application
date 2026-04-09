package com.example.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "screen_id")
    @ToString.Exclude
    private Screen screen;
    @Pattern(regexp = "^[A-O]$",message = "Seat row must be a single character between A and O")
    private String seatRow;
    @Min(value = 1)
    @Max(value = 15)
    private Integer seatNumber;
    @Enumerated(EnumType.STRING)
    private SeatType seatType;


    public enum SeatType{
        VIP,
        PREMIUM,
        STANDARD
    }
}
