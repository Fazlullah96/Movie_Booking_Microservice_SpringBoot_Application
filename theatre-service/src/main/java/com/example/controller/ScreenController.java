package com.example.controller;

import com.example.dtos.ScreenRequest;
import com.example.dtos.ScreenResponse;
import com.example.service.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screen")
@RequiredArgsConstructor
public class ScreenController {
    private final ScreenService screenService;

    @PostMapping
    public ResponseEntity<ScreenResponse> addScreen(@Valid @RequestBody ScreenRequest request){
        return new ResponseEntity<>(screenService.addScreen(request), HttpStatus.CREATED);
    }

    @GetMapping("/{name}/{theatreId}")
    public ResponseEntity<ScreenResponse> getScreenByNameAndTheatreId(@PathVariable String name, @PathVariable int theatreId){
        return new ResponseEntity<>(screenService.getScreenByNameAndTheatreId(name, theatreId), HttpStatus.OK);
    }

    @GetMapping("/screens/{name}")
    public ResponseEntity<List<ScreenResponse>> getAllScreenByName(@PathVariable String name){
        return new ResponseEntity<>(screenService.getAllScreenByName(name), HttpStatus.OK);
    }

    @GetMapping("/theatres/{theatreId}")
    public ResponseEntity<List<ScreenResponse>> getAllScreensByTheatreId(@PathVariable int theatreId){
        return new ResponseEntity<>(screenService.getAllScreenByTheatreId(theatreId), HttpStatus.OK);
    }

    @DeleteMapping("/{name}/{theatreId}")
    public ResponseEntity<String> deleteScreenByNameAndTheatreId(@PathVariable String name, @PathVariable int theatreId){
        screenService.deleteScreenByTheatreId(name, theatreId);
        return new ResponseEntity<>("Screen " + name + " deleted in TheatreId: " + theatreId, HttpStatus.OK);
    }
}
