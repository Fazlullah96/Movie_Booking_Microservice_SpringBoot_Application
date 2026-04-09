package com.example.repo;

import com.example.models.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Integer> {
    boolean existsByName(String name);
    void deleteByName(String name);
    Optional<City> findByName(String name);
}
