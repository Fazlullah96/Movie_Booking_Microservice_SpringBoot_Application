package com.example.component;

import com.example.clients.BookingClient;
import com.example.dtos.BookingResponse;
import com.example.exceptions.BookingNotFoundException;
import com.example.exceptions.ServiceUnavailableException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class BookingClientFallbackFactory implements FallbackFactory<BookingClient> {
    @Override
    public BookingClient create(Throwable cause) {
        return new BookingClient() {
            @Override
            public BookingResponse findBookingByBookingReference(String token, String bookingReference) {
                if(cause instanceof FeignException.NotFound){
                    throw new BookingNotFoundException("Booking not found for BookingReference: " + bookingReference);
                }
                throw new ServiceUnavailableException("BOOKING SERVICE UNAVAILABLE.....");
            }
        };
    }
}
