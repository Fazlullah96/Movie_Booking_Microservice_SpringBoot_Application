package com.example.clients;

import com.example.component.BookingClientFallbackFactory;
import com.example.dtos.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "booking-service", fallbackFactory = BookingClientFallbackFactory.class)
public interface BookingClient {
    @GetMapping("/api/booking/{bookingReference}")
    BookingResponse findBookingByBookingReference(
            @RequestHeader("Authorization") String token,
            @PathVariable("bookingReference") String bookingReference
    );
}
