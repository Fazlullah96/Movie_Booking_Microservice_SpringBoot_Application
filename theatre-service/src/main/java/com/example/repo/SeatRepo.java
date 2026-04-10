package com.example.repo;

import com.example.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepo extends JpaRepository<Seat, Integer> {
    boolean existsBySeatRowAndSeatNumberAndScreenId(String seatRow, int seatNumber, int screenId);
}
