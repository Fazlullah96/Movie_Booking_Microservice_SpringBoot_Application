package com.example.controller;

import com.example.dtos.MovieRequest;
import com.example.dtos.MovieResponse;
import com.example.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> addMovie(@Valid @RequestBody MovieRequest request){
        return new ResponseEntity<>(movieService.addMovie(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable int id){
        return new ResponseEntity<>(movieService.getMovieById(id), HttpStatus.FOUND);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieResponse>> getMovieByGenre(@PathVariable String genre){
        return new ResponseEntity<>(movieService.getMoviesByGenre(genre), HttpStatus.OK);
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<MovieResponse>> getMovieByLanguage(@PathVariable String language){
        return new ResponseEntity<>(movieService.getMovieByLanguages(language), HttpStatus.OK);
    }

    @GetMapping("/active/{value}")
    public ResponseEntity<List<MovieResponse>> getAllActiveMovies(@PathVariable String value){
        return new ResponseEntity<>(movieService.findAllActiveMovies(value), HttpStatus.OK);
    }

    @GetMapping("/inactive/{value}")
    public ResponseEntity<List<MovieResponse>> getAllInActiveMovies(@PathVariable String value){
        return new ResponseEntity<>(movieService.findAllNotActiveMovies(value), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovieById(@PathVariable int id){
        movieService.deleteMovieById(id);
        return new ResponseEntity<>("Movie deleted successfully....", HttpStatus.OK);
    }

    @PutMapping("/update/{id}/{value}")
    public ResponseEntity<String> updateMovie(@PathVariable int id, @PathVariable boolean value){
        movieService.updateIsActive(id, value);
        return new ResponseEntity<>("Movie updated successfully for movieId: " + id, HttpStatus.OK);
    }
}
