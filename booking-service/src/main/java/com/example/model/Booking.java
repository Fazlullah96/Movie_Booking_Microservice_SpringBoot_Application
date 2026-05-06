package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String bookingReference;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private Integer showId;
    private String transactionId;
    private Double totalAmount;
    @Enumerated(EnumType.STRING)
    private Status bookingStatus;
    private LocalDateTime bookingTime;

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<BookingSeat> bookedSeats;

    public enum Status{
        PENDING,
        SUCCESS,
        CANCELLED,
        AWAITING_PAYMENT
    }
}
