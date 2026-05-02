package com.example.component;

import com.example.clients.MovieClient;
import com.example.dtos.MovieResponse;
import com.example.exception.MovieNotFoundException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class MovieClientFallbackFactory implements FallbackFactory<MovieClient> {
    @Override
    public MovieClient create(Throwable cause) {
        return new MovieClient() {
            @Override
            public MovieResponse getMovieById(int id) {
                if(cause instanceof FeignException.NotFound){
                    throw new MovieNotFoundException("Movie not found for MovieId: " + id);
                }
                throw new RuntimeException("Service not available.....");
            }
        };
    }
}
