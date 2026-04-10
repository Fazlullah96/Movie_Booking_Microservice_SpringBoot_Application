package com.example.service;

import com.example.dtos.CityRequest;
import com.example.dtos.CityResponse;
import com.example.exception.CityAlreadyExistsException;
import com.example.exception.CityNotFoundException;
import com.example.models.City;
import com.example.repo.CityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {
    private final CityRepo cityRepo;

    @Transactional
    @CachePut(key = "#result.name", value = "CITY_CACHE")
    public CityResponse addCity(CityRequest request){
        boolean isCityExist = cityRepo.existsByName(request.getName());
        if(isCityExist){
            log.error("City {} already exist", request.getName());
            throw new CityAlreadyExistsException("City already exist with Name: " + request.getName());
        }
        City city = toCityModel(request);
        City savedCity = cityRepo.save(city);
        log.info("City {} added successfully", savedCity.getName());
        return toCityResponse(savedCity);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#name", value = "CITY_CACHE")
    public CityResponse getCityByName(String name){
        City city = cityRepo.findByName(name).orElseThrow(() -> {
            log.error("City {} not found", name);
            return new CityNotFoundException("City not found by Name : " + name);
        });
        log.info("City {} found", name);
        return toCityResponse(city);
    }

    @Transactional
    @CacheEvict(key = "#name", value = "CITY_CACHE")
    public void deleteCityByName(String name){
        boolean isCityExist = cityRepo.existsByName(name);
        if(!isCityExist){
            throw new CityNotFoundException("City not found for Name : " + name);
        }
        cityRepo.deleteByName(name);
        log.info("City {} deleted", name);
    }

    @Transactional(readOnly = true)
    public List<CityResponse> getAllCities(){
        List<City> cities = cityRepo.findAll();
        return cities
                .stream()
                .map(city -> toCityResponse(city))
                .collect(Collectors.toList());
    }

    public City toCityModel(CityRequest request){
        return City
                .builder()
                .name(request.getName())
                .state(request.getState())
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
