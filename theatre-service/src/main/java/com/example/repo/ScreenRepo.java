package com.example.repo;

import com.example.models.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepo extends JpaRepository<Screen, Integer> {
}
