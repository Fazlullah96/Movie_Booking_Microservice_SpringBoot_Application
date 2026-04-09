package com.example.controller;

import brave.Response;
import com.example.dtos.TheatreRequest;
import com.example.dtos.TheatreResponse;
import com.example.service.TheatreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatre")
@RequiredArgsConstructor
public class TheatreController {
    private final TheatreService theatreService;

    @PostMapping
    public ResponseEntity<TheatreResponse> addTheatre(@Valid @RequestBody TheatreRequest request){
        return new ResponseEntity<>(theatreService.addTheatre(request), HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<TheatreResponse> getTheatreByName(@PathVariable String name){
        return new ResponseEntity<>(theatreService.getTheatreByName(name), HttpStatus.FOUND);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteTheatreByName(@PathVariable String name){
        theatreService.deleteTheatreByName(name);
        return new ResponseEntity<>("Theatre " + name +" deleted Successfully", HttpStatus.OK);
    }

    @GetMapping("/city/{name}")
    public ResponseEntity<List<TheatreResponse>> getAllTheatreByCityName(@PathVariable String name){
        return new ResponseEntity<>(theatreService.getAllTheatreByCity(name), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TheatreResponse>> getAllTheatres(){
        return new ResponseEntity<>(theatreService.getAllTheatres(), HttpStatus.OK);
    }

    @DeleteMapping("/all/{name}")
    public ResponseEntity<String> deleteAllTheatreByName(@PathVariable String name){
        theatreService.deleteAllTheatreByName(name);
        return new ResponseEntity<>("Theatre " + name + " deleted all successfully", HttpStatus.OK);
    }
}
