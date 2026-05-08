package com.example.repo;

import com.example.models.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxEventRepo extends JpaRepository<OutBoxEvent, Integer> {
    List<OutBoxEvent> findAllByStatusFalse();
}
