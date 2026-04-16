package com.example.service;

import com.example.component.MapperComponent;
import com.example.dtos.MovieResponse;
import com.example.dtos.ScreenResponse;
import com.example.dtos.ShowRequest;
import com.example.dtos.ShowResponse;
import com.example.exception.MovieInActiveException;
import com.example.exception.MovieNotFoundException;
import com.example.exception.ScreenNotFoundException;
import com.example.exception.ShowNotFoundException;
import com.example.models.Show;
import com.example.repo.ShowRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final MapperComponent mapper;

    public ShowResponse addShow(ShowRequest request){
        MovieResponse movie = webClientBuilder.build()
                .get()
                .uri("http://movie-service/api/movie/{id}", request.getMovieId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(() -> new MovieNotFoundException("Movie not found for Id" + request.getMovieId())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(() -> new RuntimeException("Service not Available")))
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
                .onStatus(HttpStatusCode::is4xxClientError, (response) ->
                        Mono.error(() -> new ScreenNotFoundException("Screen Not Found for Id" + request.getScreenId())))
                .onStatus(HttpStatusCode::is5xxServerError, (response) ->
                        Mono.error(() -> new RuntimeException("Service Currently not available")))
                .bodyToMono(ScreenResponse.class)
                .block();

        Objects.requireNonNull(screen, "The Screen Response Cannot be negative");

        Show show = mapper.toShowEntity(request);
        Show savedShow = showRepo.save(show);
        return mapper.toShowResponse(savedShow);
    }


    public ShowResponse getShowById(int id){
        Show show = showRepo.findById(id)
                .orElseThrow(() -> new ShowNotFoundException("Show not found for Id: "+ id));
        return mapper.toShowResponse(show);
    }

    public ShowResponse softDeleteShowById(int id){
        Show show = showRepo.findById(id)
                .orElseThrow(() -> new ShowNotFoundException("Show not found for Id: " + id));
        show.setIsActive(false);
        return mapper.toShowResponse(show);
    }
}
