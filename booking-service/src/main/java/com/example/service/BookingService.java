package com.example.service;

import com.example.clients.ShowClient;
import com.example.clients.UserClient;
import com.example.dtos.*;
import com.example.events.BookingCreatedEvent;
import com.example.exceptions.BookingNotFoundException;
import com.example.exceptions.SeatAlreadyLockedException;
import com.example.model.Booking;
import com.example.model.BookingSeat;
import com.example.model.OutBoxEvent;
import com.example.repo.BookingRepo;
import com.example.repo.OutBoxEventRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepo bookingRepo;
    private final UserClient userClient;
    private final ShowClient showClient;
    private final StringRedisTemplate redisTemplate;
    private final OutBoxEventRepo outBoxEventRepo;
    private final ObjectMapper objectMapper;

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private static final String BOOKING_TOPIC = "booking-events";

    @Transactional
    public BookingResponse createBooking(String token, BookingRequest request) throws JsonProcessingException {
        UserResponse user = userClient.getUserByUserId(token, request.getUserId());
        List<Integer> requestShowSeatIds = request.getShowSeatIds();
        List<String> successfullyLockedSeats = new ArrayList<>();
        String LOCKED_VALUE = "LOCKED_BY_USER_" + request.getUserId();

        try{
            for(Integer showSeatId : requestShowSeatIds){
                String LOCKED_KEY = "LOCK:SEAT:" + showSeatId;
                Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                        LOCKED_KEY, LOCKED_VALUE, 10, TimeUnit.MINUTES
                );

                if(Boolean.TRUE.equals(acquired)){
                    successfullyLockedSeats.add(LOCKED_KEY);
                }else{
                    throw new SeatAlreadyLockedException("SeatId: " + showSeatId + " already locked by another user. Please try different seat");
                }
            }

            List<ShowSeatResponse> showSeats = showClient.getAllShowSeatByIds(token, requestShowSeatIds);

            if(showSeats.size() != requestShowSeatIds.size()){
                throw new RuntimeException("One or more requested seats are invalid or not found.");
            }

            boolean allAvailable = showSeats
                    .stream()
                    .allMatch(showSeat -> showSeat.getStatus().equals("AVAILABLE"));
            if(!allAvailable){
                throw new RuntimeException("One or more selected seats have already been permanently booked.");
            }

            double totalAmount = showSeats
                    .stream()
                    .mapToDouble(showSeat -> showSeat.getPrice())
                    .sum();

            Booking booking = Booking
                    .builder()
                    .bookingReference(getBookingReference())
                    .transactionId(null)
                    .userId(request.getUserId())
                    .showId(request.getShowId())
                    .totalAmount(totalAmount)
                    .bookingStatus(Booking.Status.PENDING)
                    .bookingTime(LocalDateTime.now())
                    .build();

            List<BookingSeat> bookingSeats = showSeats
                    .stream()
                    .map(showSeat -> BookingSeat
                            .builder()
                            .showSeatId(showSeat.getId())
                            .priceAtBooking(showSeat.getPrice())
                            .booking(booking)
                            .build())
                    .collect(Collectors.toList());
            booking.setBookedSeats(bookingSeats);
            Booking savedBooking = bookingRepo.save(booking);

            BookingCreatedEvent event = BookingCreatedEvent
                    .builder()
                    .bookingReference(savedBooking.getBookingReference())
                    .showSeatIds(requestShowSeatIds)
                    .build();
            OutBoxEvent outBoxEvent = OutBoxEvent
                    .builder()
                    .aggregateType("BOOKING")
                    .aggregateId(savedBooking.getBookingReference())
                    .topic("booking-events")
                    .status(false)
                    .payload(objectMapper.writeValueAsString(event))
                    .build();
            outBoxEventRepo.save(outBoxEvent);
            return BookingResponse
                    .builder()
                    .bookingId(savedBooking.getId())
                    .bookingReference(savedBooking.getBookingReference())
                    .transactionId(savedBooking.getTransactionId())
                    .userId(savedBooking.getUserId())
                    .showId(savedBooking.getShowId())
                    .totalAmount(savedBooking.getTotalAmount())
                    .status(savedBooking.getBookingStatus())
                    .bookingTime(savedBooking.getBookingTime())
                    .bookedSeats(savedBooking.getBookedSeats()
                            .stream()
                            .map(bookingSeat -> BookedSeatInfo
                                    .builder()
                                    .showSeatId(bookingSeat.getShowSeatId())
                                    .price(bookingSeat.getPriceAtBooking())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }catch (Exception e){
            for(String key : successfullyLockedSeats){
                redisTemplate.delete(key);
            }
            throw e;
        }
    }

    public String getBookingReference(){
        return "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public BookingResponse getBookingByBookingReference(String bookingReference){
        Booking booking = bookingRepo.findByBookingReference(bookingReference)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for BookingReference: " + bookingReference));
        return BookingResponse
                .builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .transactionId(booking.getTransactionId())
                .userId(booking.getUserId())
                .showId(booking.getShowId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getBookingStatus())
                .bookingTime(booking.getBookingTime())
                .bookedSeats(booking.getBookedSeats()
                        .stream()
                        .map(bookingSeat -> BookedSeatInfo
                                .builder()
                                .showSeatId(bookingSeat.getShowSeatId())
                                .price(bookingSeat.getPriceAtBooking())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
