package com.example.service;

import com.example.clients.MovieClient;
import com.example.clients.ScreenClient;
import com.example.component.MapperComponent;
import com.example.dtos.*;
import com.example.exception.ShowNotFoundException;
import com.example.exception.ShowSeatNotFoundException;
import com.example.exception.ShowSeatStatusException;
import com.example.models.Show;
import com.example.models.ShowSeat;
import com.example.repo.ShowRepo;
import com.example.repo.ShowSeatRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {
    private final ShowRepo showRepo;
    private final ShowSeatRepo showSeatRepo;
    private final MapperComponent mapper;
    private final MovieClient movieClient;
    private final ScreenClient screenClient;

    @Caching(put = {
            @CachePut(value = "SHOW_CACHE", key = "#result.id")
    }, evict = {
            @CacheEvict(value = "ACTIVE_SHOW_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "INACTIVE_SHOW_CACHE_LIST", allEntries = true)
    })
    public ShowResponse addShow(ShowRequest request, String token){
        MovieResponse movie = movieClient.getMovieById(token, request.getMovieId());
        ScreenResponse screen = screenClient.getScreenById(token, request.getScreenId());
        List<SeatResponse> physicalSeats = screenClient.getSeatsByScreenId(token, request.getScreenId());

        Show show = mapper.toShowEntity(request);

        List<ShowSeat> showSeats = new ArrayList<>();

        for(SeatResponse seats : physicalSeats){
            double finalPrice = calculateSeatPrice(seats.getSeatType().name(), request);

            ShowSeat showSeat = ShowSeat
                    .builder()
                    .show(show)
                    .seatId(seats.getId())
                    .price(finalPrice)
                    .status(ShowSeat.Status.AVAILABLE)
                    .build();

            showSeats.add(showSeat);
        }
        show.setShowSeats(showSeats);
        Show savedShow = showRepo.save(show);
        return mapper.toShowResponse(savedShow);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "SHOW_CACHE", key = "#result.id")
    public ShowResponse getShowById(int id){
        Show show = showRepo.findById(id)
                .orElseThrow(() -> new ShowNotFoundException("Show not found for Id: "+ id));
        return mapper.toShowResponse(show);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "ACTIVE_SHOW_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "INACTIVE_SHOW_CACHE_LIST", allEntries = true)
    })
    public ShowResponse softDeleteShowById(int id){
        Show show = showRepo.findById(id)
                .orElseThrow(() -> new ShowNotFoundException("Show not found for Id: " + id));
        show.setIsActive(false);
        Show savedShow = showRepo.save(show);
        return mapper.toShowResponse(savedShow);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "ACTIVE_SHOW_CACHE_LIST", key = "'ACTIVE'")
    public List<ShowResponse> getAllActiveShows(){
        List<Show> shows = showRepo.findAllByIsActiveTrue();
        return shows
                .stream()
                .map(mapper::toShowResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "INACTIVE_SHOW_CACHE_LIST", key = "'INACTIVE'")
    public List<ShowResponse> getAllInActiveShows(){
        List<Show> shows = showRepo.findAllByIsActiveFalse();
        return shows
                .stream()
                .map(mapper::toShowResponse)
                .collect(Collectors.toList());
    }

    public double calculateSeatPrice(String seatType, ShowRequest request){
        if("VIP".equalsIgnoreCase(seatType)){
            return request.getBasePrice() * request.getVipMultiplier();
        }else if("PREMIUM".equalsIgnoreCase(seatType)){
            return request.getBasePrice() * request.getPremiumMultiplier();
        }
        return request.getBasePrice();
    }

    public List<ShowSeatResponse> getAllShowSeatsByIds(List<Integer> ids){
        List<ShowSeat> showSeats = showSeatRepo.findAllByIdIn(ids);
        return showSeats
                .stream()
                .map(showSeat -> ShowSeatResponse.builder().id(showSeat.getId()).seatId(showSeat.getSeatId()).price(showSeat.getPrice()).status(String.valueOf(showSeat.getStatus())).build())
                .collect(Collectors.toList());
    }

    public List<ShowSeatResponse> updateShowSeatStatusToLocked(List<Integer> showSheatIds){
        List<ShowSeat> showSeats = showSeatRepo.findAllByIdIn(showSheatIds);
        for(ShowSeat seat : showSeats){
            if(seat.getStatus().name().equals("BOOKED")){
                throw new ShowSeatStatusException("Seat: " + seat.getSeatId() + " is Already BOOKED by Another User");
            } else if (seat.getStatus().name().equals("LOCKED")) {
                throw new ShowSeatStatusException("Seat: " + seat.getSeatId() + " is Already LOCKED by Another User");
            }else{
                seat.setStatus(ShowSeat.Status.valueOf("LOCKED"));
            }
        }
        List<ShowSeat> seats = showSeatRepo.saveAll(showSeats);
        return seats
                .stream()
                .map(seat -> ShowSeatResponse
                        .builder()
                        .id(seat.getId())
                        .seatId(seat.getSeatId())
                        .price(seat.getPrice())
                        .status(String.valueOf(seat.getStatus()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<ShowSeatResponse> revertUpdatedStatusAvailable(List<Integer> showSeatIds){
        List<ShowSeat> showSeats = showSeatRepo.findAllByIdIn(showSeatIds);
        for(ShowSeat seat : showSeats){
            seat.setStatus(ShowSeat.Status.valueOf("AVAILABLE"));
        }
        List<ShowSeat> savedShowSeats = showSeatRepo.saveAll(showSeats);
        return savedShowSeats
                .stream()
                .map(seat -> ShowSeatResponse
                        .builder()
                        .id(seat.getId())
                        .seatId(seat.getSeatId())
                        .price(seat.getPrice())
                        .status(String.valueOf(seat.getStatus()))
                        .build())
                .collect(Collectors.toList());
    }
}
