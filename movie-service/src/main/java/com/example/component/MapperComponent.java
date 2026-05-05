package com.example.component;

import com.example.dtos.MovieRequest;
import com.example.dtos.MovieResponse;
import com.example.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MapperComponent {

    public MovieResponse toMovieResponse(Movie movie){
        return MovieResponse
                .builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .director(movie.getDirector())
                .durationInMinutes(movie.getDurationInMinutes())
                .releaseDate(movie.getReleaseDate())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .posterImageUrl(movie.getPosterImageUrl())
                .trailerUrl(movie.getTrailerUrl())
                .isActive(movie.getIsActive())
                .build();
    }

    public Movie toMovieEntity(MovieRequest request){
        return Movie
                .builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .director(request.getDirector())
                .durationInMinutes(request.getDurationInMinutes())
                .releaseDate(request.getReleaseDate())
                .language(request.getLanguage())
                .genre(request.getGenre())
                .posterImageUrl(request.getPosterImageUrl())
                .trailerUrl(request.getTrailerUrl())
                .build();
    }
}
