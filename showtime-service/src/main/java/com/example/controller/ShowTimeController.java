package com.example.controller;

import com.example.dtos.ShowRequest;
import com.example.dtos.ShowResponse;
import com.example.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtime")
@RequiredArgsConstructor
public class ShowTimeController {
    private final ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> addShow(
            @RequestBody ShowRequest request,
            @RequestHeader("Authorization") String token
    ){
        return new ResponseEntity<>(showService.addShow(request, token), HttpStatus.CREATED);
    }

    @GetMapping("/{showId}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable int showId){
        return new ResponseEntity<>(showService.getShowById(showId), HttpStatus.OK);
    }

    @PutMapping("/deactivate/{screenId}")
    public ResponseEntity<ShowResponse> deactivateShowByShowId(@PathVariable int screenId){
        return new ResponseEntity<>(showService.softDeleteShowById(screenId), HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ShowResponse>> getAllActiveShows(){
        return new ResponseEntity<>(showService.getAllActiveShows(), HttpStatus.OK);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ShowResponse>> getAllInActive(){
        return new ResponseEntity<>(showService.getAllInActiveShows(), HttpStatus.OK);
    }
}
