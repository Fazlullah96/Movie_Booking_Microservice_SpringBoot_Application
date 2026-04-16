package com.example.service;

import com.example.component.MapperComponent;
import com.example.dtos.CityResponse;
import com.example.dtos.TheatreRequest;
import com.example.dtos.TheatreResponse;
import com.example.exception.CityNotFoundException;
import com.example.exception.TheatreAlreadyExistException;
import com.example.exception.TheatreNotFoundException;
import com.example.models.City;
import com.example.models.Theatre;
import com.example.repo.CityRepo;
import com.example.repo.TheatreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheatreService {
    private final TheatreRepo theatreRepo;
    private final CityRepo cityRepo;
    private final MapperComponent mapper;

    @Transactional
    @CachePut(key = "#result.name", value = "THEATRE_CACHE")
    @Caching(evict = {
            @CacheEvict(key = "#result.city.name", value = "THEATRE_CITY_CACHE_LIST"),
            @CacheEvict(value = "ALL_THEATRE_CACHE_LIST", allEntries = true)
    })
    public TheatreResponse addTheatre(TheatreRequest request){
        City city = cityRepo.findById(request.getCityId()).orElseThrow(() -> {
            log.error("CityId {} not found", request.getCityId());
            return new CityNotFoundException("City not found for Id: " + request.getCityId());
        });
        boolean isTheatreExist = theatreRepo.existsByName(request.getName());
        if(isTheatreExist){
            throw new TheatreAlreadyExistException("Theatre "+ request.getName() + " already Exist");
        }
        Theatre theatre = mapper.toTheatreModel(request);
        theatre.setCity(city);
        Theatre savedTheatre = theatreRepo.save(theatre);
        CityResponse cityResponse = mapper.toCityResponse(city);
        log.info("Theatre {} added successfully", savedTheatre.getName());
        return mapper.toTheatreResponse(savedTheatre, cityResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#name", value = "THEATRE_CACHE")
    public TheatreResponse getTheatreByName(String name){
        Theatre theatre = theatreRepo.findByName(name).orElseThrow(() -> {
            log.error("Theatre {} not found", name);
            return new TheatreNotFoundException("Theatre Not found for Name: " + name);
        });
        City city = theatre.getCity();
        CityResponse cityResponse = mapper.toCityResponse(city);
        log.info("Theatre {} fetched successfully", name);
        return mapper.toTheatreResponse(theatre, cityResponse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#name", value = "THEATRE_CACHE"),
            @CacheEvict(key = "#result", value = "THEATRE_CITY_CACHE_LIST"),
            @CacheEvict(value = "ALL_THEATRE_CACHE_LIST", allEntries = true)
    })
    public String deleteTheatreByName(String name){
        Theatre theatre = theatreRepo.findByName(name).orElseThrow(() -> {
            log.error("Theatre {} not found", name);
            return new TheatreNotFoundException("Theatre not found for Name: " + name);
        });
        theatreRepo.deleteByName(name);
        return theatre.getCity().getName();
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#name", value = "THEATRE_CITY_CACHE_LIST")
    public List<TheatreResponse> getAllTheatreByCity(String name){
        List<Theatre> theatres = theatreRepo.findByCityName(name);
        log.info("Found all theatre in city {}", name);
        return theatres
                .stream()
                .map(theatre -> mapper.toTheatreResponse(theatre, mapper.toCityResponse(theatre.getCity())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "THEATRE_CACHE", key = "#name"),
            @CacheEvict(value = "ALL_THEATRE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "THEATRE_CITY_CACHE_LIST", allEntries = true)
    })
    public void deleteAllTheatreByName(String name){
        if(!theatreRepo.existsByName(name)){
            log.error("Theatre {} not found", name);
            throw new TheatreNotFoundException("Theatre " + name + " not found");
        }
        theatreRepo.deleteAllByName(name);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'allTheatre'", value = "ALL_THEATRE_CACHE_LIST")
    public List<TheatreResponse> getAllTheatres(){
        List<Theatre> theatres = theatreRepo.findAll();
        return theatres
                .stream()
                .map(theatre -> mapper.toTheatreResponse(theatre, mapper.toCityResponse(theatre.getCity())))
                .collect(Collectors.toList());
    }

//    public Theatre toTheatreModel(TheatreRequest request){
//        return Theatre
//                .builder()
//                .name(request.getName())
//                .address(request.getAddress())
//                .build();
//    }
//
//    public TheatreResponse toTheatreResponse(Theatre theatre, CityResponse cityResponse){
//        return TheatreResponse
//                .builder()
//                .name(theatre.getName())
//                .address(theatre.getAddress())
//                .city(cityResponse)
//                .build();
//    }
//
//    public CityResponse toCityResponse(City city){
//        return CityResponse
//                .builder()
//                .id(city.getId())
//                .name(city.getName())
//                .state(city.getState())
//                .build();
//    }
}
