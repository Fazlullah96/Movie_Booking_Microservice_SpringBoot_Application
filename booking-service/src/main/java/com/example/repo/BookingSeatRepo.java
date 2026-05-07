package com.example.repo;

import com.example.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingSeatRepo extends JpaRepository<BookingSeat, Integer> {
    @Query("SELECT b.id FROM BookingSeat b")
    List<Integer> findAllByBookingSeatId();
}
