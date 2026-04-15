package com.example.repo;

import com.example.models.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepo extends JpaRepository<Show, Integer> {
}
