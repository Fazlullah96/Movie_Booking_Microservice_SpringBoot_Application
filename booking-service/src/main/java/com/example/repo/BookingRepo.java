package com.example.repo;

import com.example.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByBookingReference(String bookingReference);

    @Query("SELECT s.showSeatId FROM Booking b JOIN b.bookedSeats s WHERE b.bookingReference = :bookingReference")
    List<Integer> findAllBookingSeatIdByBookingReference(@Param("bookingReference") String bookingReference);
}

