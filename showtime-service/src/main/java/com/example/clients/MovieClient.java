package com.example.clients;

import com.example.component.MovieClientFallbackFactory;
import com.example.dtos.MovieResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "movie-service", fallbackFactory = MovieClientFallbackFactory.class)
public interface MovieClient {
    @GetMapping("/api/movie/{id}")
    MovieResponse getMovieById(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") int id
    );
}
