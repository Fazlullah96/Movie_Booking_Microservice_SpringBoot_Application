package com.example.service;

import com.example.dtos.MovieResponse;
import com.example.dtos.ScreenResponse;
import com.example.dtos.ShowRequest;
import com.example.dtos.ShowResponse;
import com.example.exception.MovieInActiveException;
import com.example.exception.MovieNotFoundException;
import com.example.exception.ScreenNotFoundException;
import com.example.repo.ShowRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {
    private final ShowRepo showRepo;
    private final WebClient.Builder webClientBuilder;

    public ShowResponse addShow(ShowRequest request){
        MovieResponse movie = webClientBuilder.build()
                .get()
                .uri("http://movie-service/api/movie/{id}", request.getMovieId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return Mono.error(() -> new MovieNotFoundException("Movie not found for Id" + request.getMovieId()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return Mono.error(() -> new RuntimeException("Service not Available"));
                })
                .bodyToMono(MovieResponse.class)
                .block();
        Objects.requireNonNull(movie, "The Movie Response Cannot be null");
        if(Boolean.FALSE.equals(movie.getIsActive())){
            throw new MovieInActiveException("Movie " + movie.getTitle() +" not available at the Moment");
        }

        ScreenResponse screen = webClientBuilder
                .build()
                .get()
                .uri("http://theatre-service/api/screen/{id}", request.getScreenId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (response) -> {
                    return Mono.error(() -> new ScreenNotFoundException("Screen Not Found for Id" + request.getScreenId()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (response) -> {
                    return Mono.error(() -> new RuntimeException("Service Currently not available"));
                })
                .bodyToMono(ScreenResponse.class)
                .block();

        Objects.requireNonNull(screen, "The Screen Response Cannot be negative");
    }
}
