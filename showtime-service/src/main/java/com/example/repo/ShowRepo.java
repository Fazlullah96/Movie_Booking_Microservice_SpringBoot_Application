package com.example.repo;

import com.example.models.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepo extends JpaRepository<Show, Integer> {
    List<Show> findAllByIsActiveTrue();
    List<Show> findAllByIsActiveFalse();
}
