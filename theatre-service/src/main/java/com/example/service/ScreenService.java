package com.example.service;

import com.example.dtos.CityResponse;
import com.example.dtos.ScreenRequest;
import com.example.dtos.ScreenResponse;
import com.example.dtos.TheatreResponse;
import com.example.exception.ScreenAlreadyExistsException;
import com.example.exception.ScreenNotFoundException;
import com.example.exception.TheatreNotFoundException;
import com.example.models.City;
import com.example.models.Screen;
import com.example.models.Theatre;
import com.example.repo.ScreenRepo;
import com.example.repo.TheatreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenService {
    private final ScreenRepo screenRepo;
    private final TheatreRepo theatreRepo;

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#request.theatreId", value = "SCREEN_THEATREID_CACHE_LIST"),
            @CacheEvict(key = "#result.name", value = "SCREEN_NAME_CACHE_LIST")
    })
    public ScreenResponse addScreen(ScreenRequest request){
        boolean isScreenNameExist = screenRepo.existsByNameAndTheatreId(request.getName(), request.getTheatreId());
        if(isScreenNameExist){
            log.error("Screen {} already Exists in Theatre {}", request.getName(), request.getTheatreId());
            throw new ScreenAlreadyExistsException("Screen " + request.getName()+" already exists in this theatre");
        }
        Theatre theatre = theatreRepo.findById(request.getTheatreId()).orElseThrow(() -> {
            log.error("TheatreId {} not found", request.getTheatreId());
            return new TheatreNotFoundException("Theatre not found for Id: " + request.getTheatreId());
        });
        Screen savedScreen = screenRepo.save(toScreenModel(request, theatre));
        City city = theatre.getCity();
        CityResponse cityResponse = toCityResponse(city);
        TheatreResponse theatreResponse = toTheatreResponse(theatre, cityResponse);
        return toScreenResponse(savedScreen, theatreResponse);
    }

    @Transactional(readOnly = true)
    public ScreenResponse getScreenByNameAndTheatreId(String name, int theatreId){
        Screen screen = screenRepo.findByNameAndTheatreId(name, theatreId).orElseThrow(() -> {
            log.error("Screen {} not found in the TheatreId {}", name, theatreId);
            return new ScreenNotFoundException("Screen " + name + " not found in TheatreId: " + theatreId);
        });
        Theatre theatre = screen.getTheatre();
        City city = theatre.getCity();
        return toScreenResponse(screen, toTheatreResponse(theatre, toCityResponse(city)));
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#name", value = "SCREEN_NAME_CACHE_LIST")
    public List<ScreenResponse> getAllScreenByName(String name){
        List<Screen> screens = screenRepo.findAllByName(name);
        return screens
                .stream()
                .map(screen -> toScreenResponse(screen, toTheatreResponse(screen.getTheatre(), toCityResponse(screen.getTheatre().getCity()))))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#theatreId", value = "SCREEN_THEATREID_CACHE_LIST")
    public List<ScreenResponse> getAllScreenByTheatreId(int theatreId){
        List<Screen> screens = screenRepo.findAllByTheatreId(theatreId);
        return screens
                .stream()
                .map(screen -> toScreenResponse(screen, toTheatreResponse(screen.getTheatre(), toCityResponse(screen.getTheatre().getCity()))))
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#theatreId", value = "SCREEN_THEATREID_CACHE_LIST"),
            @CacheEvict(key = "#name", value = "SCREEN_NAME_CACHE_LIST")
    })
    public void deleteScreenByTheatreId(String name, int theatreId){
        boolean isScreenAndTheatreIdExist = screenRepo.existsByNameAndTheatreId(name, theatreId);
        if(!isScreenAndTheatreIdExist){
            log.error("Screen {} not found on TheatreId {}", name, theatreId);
            throw new ScreenNotFoundException("Screen " + name + " not found on TheatreId: " + theatreId);
        }
        screenRepo.deleteByNameAndTheatreId(name, theatreId);
    }

    public Screen toScreenModel(ScreenRequest request, Theatre theatre){
        return Screen
                .builder()
                .name(request.getName())
                .theatre(theatre)
                .build();
    }

    public ScreenResponse toScreenResponse(Screen screen, TheatreResponse theatre){
        return ScreenResponse
                .builder()
                .id(screen.getId())
                .name(screen.getName())
                .theatre(theatre)
                .build();
    }

    public TheatreResponse toTheatreResponse(Theatre theatre, CityResponse cityResponse){
        return TheatreResponse
                .builder()
                .name(theatre.getName())
                .address(theatre.getAddress())
                .city(cityResponse)
                .build();
    }

    public CityResponse toCityResponse(City city){
        return CityResponse
                .builder()
                .id(city.getId())
                .name(city.getName())
                .state(city.getState())
                .build();
    }
}
