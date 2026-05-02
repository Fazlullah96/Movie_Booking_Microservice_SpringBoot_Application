package com.example.controller;

import com.example.dtos.ScreenSetupRequest;
import com.example.dtos.SeatRequest;
import com.example.dtos.SeatResponse;
import com.example.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatResponse> addSeat(@RequestBody SeatRequest request){
        return new ResponseEntity<>(seatService.addSeat(request), HttpStatus.CREATED);
    }

    @PostMapping("/list")
    public ResponseEntity<List<SeatResponse>> addMultipleSeats(@RequestBody ScreenSetupRequest request){
        return new ResponseEntity<>(seatService.addMultipleSeats(request), HttpStatus.CREATED);
    }

    @GetMapping("/screenId/{screenId}")
    public ResponseEntity<List<SeatResponse>> getAllSeatsByScreenId(@PathVariable("screenId") int screenId){
        return new ResponseEntity<>(seatService.getSeatsByScreenId(screenId), HttpStatus.OK);
    }
}
