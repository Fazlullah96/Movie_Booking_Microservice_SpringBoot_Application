package com.example.repo;

import com.example.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepo extends JpaRepository<Seat, Integer> {
    boolean existsBySeatRowAndSeatNumberAndScreenId(String seatRow, int seatNumber, int screenId);
    List<Seat> findAllByScreenId(int screenId);
}
