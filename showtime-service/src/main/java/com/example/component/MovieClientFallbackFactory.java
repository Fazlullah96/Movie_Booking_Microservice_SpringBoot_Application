package com.example.component;

import com.example.clients.MovieClient;
import com.example.dtos.MovieResponse;
import com.example.exception.MovieNotFoundException;
import com.example.exception.ServiceUnavailableException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MovieClientFallbackFactory implements FallbackFactory<MovieClient> {
    @Override
    public MovieClient create(Throwable cause) {
        return new MovieClient() {
            @Override
            public MovieResponse getMovieById(String token, int id) {
                log.error("Feign call to movie-service failed. Cause: ", cause);
                if(cause instanceof FeignException.NotFound){
                    throw new MovieNotFoundException("Movie not found for MovieId: " + id);
                }
                throw new ServiceUnavailableException("SERVICE UNAVAILABLE.....");
            }
        };
    }
}
