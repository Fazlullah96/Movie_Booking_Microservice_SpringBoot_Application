package com.example.controller;

import com.example.dtos.CityRequest;
import com.example.dtos.CityResponse;
import com.example.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @PostMapping
    public ResponseEntity<CityResponse> addCity(@Valid @RequestBody CityRequest request){
        return new ResponseEntity<>(cityService.addCity(request), HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<CityResponse> getCityByName(@PathVariable String name){
        return new ResponseEntity<>(cityService.getCityByName(name), HttpStatus.FOUND);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteCityByName(@PathVariable String name){
        cityService.deleteCityByName(name);
        return new ResponseEntity<>("City " + name + " deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CityResponse>> getAllCities(){
        return new ResponseEntity<>(cityService.getAllCities(), HttpStatus.OK);
    }
}
