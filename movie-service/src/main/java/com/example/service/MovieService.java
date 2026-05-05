package com.example.service;

import com.example.component.MapperComponent;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepo movieRepo;
    private final MapperComponent mapper;

    @CachePut(key = "#result.id", value = "MOVIE_CACHE")
    @Caching(evict = {
            @CacheEvict(key = "#result.genre", value = "MOVIE_GENRE_CACHE_LIST"),
            @CacheEvict(key = "#result.language", value = "MOVIE_LANGUAGE_CACHE_LIST"),
            @CacheEvict(value = "MOVIE_ACTIVE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_INACTIVE_CACHE_LIST", allEntries = true),
            @CacheEvict(value = "MOVIE_CACHE_LIST", allEntries = true)
    })
    public MovieResponse addMovie(MovieRequest request){
        Movie movie = mapper.toMovieEntity(request);
        movie.setIsActive(true);
        Movie savedMovie = movieRepo.save(movie);
        log.info("Movie {} saved to the DB", savedMovie.getTitle());
        return mapper.toMovieResponse(savedMovie);
    }

    @Cacheable(key = "#id", value = "MOVIE_CACHE")
    public MovieResponse getMovieById(int id){
        Movie movie = movieRepo.findById(id).orElseThrow(
                () -> new MovieNotFoundException("Movie Not found for Id: " + id));
        log.info("Movie Id {} is found", movie.getId());
        return mapper.toMovieResponse(movie);
    }

    @Cacheable(key = "#genre", value = "MOVIE_GENRE_CACHE_LIST")
    public List<MovieResponse> getMoviesByGenre(String genre){
        List<Movie> movies = movieRepo.findAllByGenre(genre);
        log.info("Movie Genre {} all collected", genre);
        return  movies
                .stream()
                .map(movie -> mapper.toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#language", value = "MOVIE_LANGUAGE_CACHE_LIST")
    public List<MovieResponse> getMovieByLanguages(String language){
        List<Movie> movies = movieRepo.findAllByLanguage(language);
        log.info("Movie Language {} all collected", language);
        return movies
                .stream()
                .map(movie -> mapper.toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'ACTIVE'", value = "MOVIE_ACTIVE_CACHE_LIST")
    public List<MovieResponse> findAllActiveMovies(){
        List<Movie> movies = movieRepo.findAllByIsActiveTrue();
        return movies.stream()
                .map(movie -> mapper.toMovieResponse(movie))
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'INACTIVE'", value = "MOVIE_INACTIVE_CACHE_LIST")
    public List<MovieResponse> findAllNotActiveMovies(){
        List<Movie> movies = movieRepo.findAllByIsActiveFalse();
        return movies
                .stream()
                .map(movie -> mapper.toMovieResponse(movie))
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

    @Transactional(readOnly = true)
    @Cacheable(value = "MOVIE_CACHE_LIST", key = "'ALL'")
    public List<MovieResponse> getAllMovies(){
        List<Movie> movies = movieRepo.findAll();
        return movies
                .stream()
                .map(mapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    public MovieResponse updateMovie(int id, MovieRequest request){
        Movie movie = movieRepo.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found for MovieId: " + id));
        Movie updatedMovie = mapper.toMovieEntity(request);
        updatedMovie.setIsActive(true);
        Movie savedMovie = movieRepo.save(updatedMovie);
        return mapper.toMovieResponse(savedMovie);
    }


//    public MovieResponse toMovieResponse(Movie movie){
//        return MovieResponse
//                .builder()
//                .id(movie.getId())
//                .title(movie.getTitle())
//                .description(movie.getDescription())
//                .durationInMinutes(movie.getDurationInMinutes())
//                .releaseDate(movie.getReleaseDate())
//                .genre(movie.getGenre())
//                .language(movie.getLanguage())
//                .posterImageUrl(movie.getPosterImageUrl())
//                .trailerUrl(movie.getTrailerUrl())
//                .isActive(movie.getIsActive())
//                .build();
//    }
//
//    public Movie toMovieEntity(MovieRequest request){
//        return Movie
//                .builder()
//                .title(request.getTitle())
//                .description(request.getDescription())
//                .durationInMinutes(request.getDurationInMinutes())
//                .releaseDate(request.getReleaseDate())
//                .language(request.getLanguage())
//                .genre(request.getGenre())
//                .posterImageUrl(request.getPosterImageUrl())
//                .trailerUrl(request.getTrailerUrl())
//                .build();
//    }
}
