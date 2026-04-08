package com.example.repo;

import com.example.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepo extends JpaRepository<Movie, Integer> {
    List<Movie> findAllByGenre(String genre);
    List<Movie> findAllByLanguage(String language);
    List<Movie> findByIsActiveTrue();
    List<Movie> findByIsActiveFalse();
}
