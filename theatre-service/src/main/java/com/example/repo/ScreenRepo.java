package com.example.repo;

import com.example.models.Screen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScreenRepo extends JpaRepository<Screen, Integer> {
    boolean existsByNameAndTheatreId(String name, int theatreId);
    Optional<Screen> findByName(String name);
    @EntityGraph(attributePaths = {"theatre", "theatre.city"})
    List<Screen> findAllByName(String name);
    Optional<Screen> findByNameAndTheatreId(String name, int theatreId);
    @EntityGraph(attributePaths = {"theatre", "theatre.city"})
    List<Screen> findAllByTheatreId(int theatreId);
    void deleteByNameAndTheatreId(String name, int theatreId);
}
