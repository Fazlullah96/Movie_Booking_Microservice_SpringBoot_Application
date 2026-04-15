package com.example.service;

import com.example.dtos.MovieRequest;
import com.example.dtos.MovieResponse;
import com.example.exception.MovieNotFoundException;
import com.example.model.Movie;
import com.example.repo.MovieRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepo movieRepo;

    @CachePut(key = "#result.id", value = "MOVIE_CACHE")
    @Caching(evict = {
            @CacheEvict(key = "#result.genre", value = "MOVIE_GENRE_CACHE_LIST"),
            @CacheEvict(key = "#result.language", value = "MOVIE_LANGUAGE_CACHE_LIST"),
            @CacheEvict(value = "MOVIE_ACTIVE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_INACTIVE_CACHE_LIST", allEntries = true)
    })
    public MovieResponse addMovie(MovieRequest request){
        Movie movie = toMovieEntity(request);
        movie.setIsActive(true);
        Movie savedMovie = movieRepo.save(movie);
        log.info("Movie {} saved to the DB", savedMovie.getTitle());
        return toMovieResponse(savedMovie);
    }

    @Cacheable(key = "#id", value = "MOVIE_CACHE")
    public MovieResponse getMovieById(int id){
        Movie movie = movieRepo.findById(id).orElseThrow(
                () -> new MovieNotFoundException("Movie Not found for Id: " + id));
        log.info("Movie Id {} is found", movie.getId());
        return toMovieResponse(movie);
    }

    @Cacheable(key = "#genre", value = "MOVIE_GENRE_CACHE_LIST")
    public List<MovieResponse> getMoviesByGenre(String genre){
        List<Movie> movies = movieRepo.findAllByGenre(genre);
        log.info("Movie Genre {} all collected", genre);
        return  movies
                .stream()
                .map(movie -> toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#language", value = "MOVIE_LANGUAGE_CACHE_LIST")
    public List<MovieResponse> getMovieByLanguages(String language){
        List<Movie> movies = movieRepo.findAllByLanguage(language);
        log.info("Movie Language {} all collected", language);
        return movies
                .stream()
                .map(movie -> toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#value", value = "MOVIE_ACTIVE_CACHE_LIST")
    public List<MovieResponse> findAllActiveMovies(String value){
        List<Movie> movies = movieRepo.findByIsActiveTrue();
        return movies.stream()
                .map(movie -> toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#value", value = "MOVIE_INACTIVE_CACHE_LIST")
    public List<MovieResponse> findAllNotActiveMovies(String value){
        List<Movie> movies = movieRepo.findByIsActiveFalse();
        return movies
                .stream()
                .map(movie -> toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(value = "MOVIE_CACHE", key = "#id"),
            @CacheEvict(value = "MOVIE_ACTIVE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_INACTIVE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_LANGUAGE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_GENRE_CACHE_LIST", allEntries = true)
    })
    public void deleteMovieById(int id){
        Movie movie = movieRepo.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie Not Found for Id: " + id));
        movieRepo.deleteById(id);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id", value = "MOVIE_CACHE"),
            @CacheEvict(value = "MOVIE_ACTIVE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_INACTIVE_CACHE_LIST", allEntries = true)
    })
    public void updateIsActive(int id, boolean value){
        Movie movie = movieRepo.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie Not Found for Id: " + id));
        movie.setIsActive(value);
        movieRepo.save(movie);
    }

    public MovieResponse toMovieResponse(Movie movie){
        return MovieResponse
                .builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
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
                .durationInMinutes(request.getDurationInMinutes())
                .releaseDate(request.getReleaseDate())
                .language(request.getLanguage())
                .genre(request.getGenre())
                .posterImageUrl(request.getPosterImageUrl())
                .trailerUrl(request.getTrailerUrl())
                .build();
    }
}
