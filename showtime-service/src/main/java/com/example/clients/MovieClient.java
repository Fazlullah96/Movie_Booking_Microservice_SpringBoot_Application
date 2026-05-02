package com.example.clients;

import com.example.component.MovieClientFallbackFactory;
import com.example.dtos.MovieResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service", fallbackFactory = MovieClientFallbackFactory.class)
public interface MovieClient {
    @GetMapping("/api/movie/{id}")
    MovieResponse getMovieById(@PathVariable("id") int id);
}
