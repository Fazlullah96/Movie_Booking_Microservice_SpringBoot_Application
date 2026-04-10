package com.example.repo;

import com.example.models.Theatre;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheatreRepo extends JpaRepository<Theatre, Integer> {
    Optional<Theatre> findByName(String name);
    @EntityGraph(attributePaths = {"city"})
    List<Theatre> findByCityName(String name);
    boolean existsByName(String name);
    void deleteAllByName(String name);
    void deleteByName(String name);
}
