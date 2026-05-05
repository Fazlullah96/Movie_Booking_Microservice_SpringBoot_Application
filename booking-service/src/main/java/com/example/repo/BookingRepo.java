package com.example.repo;

import com.example.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByBookingReference(String bookingReference);
}
