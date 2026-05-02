package com.example.service;

import com.example.clients.MovieClient;
import com.example.clients.ScreenClient;
import com.example.component.MapperComponent;
import com.example.dtos.*;
import com.example.exception.ShowNotFoundException;
import com.example.models.Show;
import com.example.models.ShowSeat;
import com.example.repo.ShowRepo;
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
    private final MapperComponent mapper;
    private final MovieClient movieClient;
    private final ScreenClient screenClient;

    @Caching(put = {
            @CachePut(value = "SHOW_CACHE", key = "#result.id")
    }, evict = {
            @CacheEvict(value = "ACTIVE_SHOW_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "INACTIVE_SHOW_CACHE_LIST", allEntries = true)
    })
    public ShowResponse addShow(ShowRequest request){
        MovieResponse movie = movieClient.getMovieById(request.getMovieId());
        ScreenResponse screen = screenClient.getScreenById(request.getScreenId());
        List<SeatResponse> physicalSeats = screenClient.getSeatsByScreenId(request.getScreenId());

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
}
