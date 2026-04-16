package com.example.service;

import com.example.repo.ShowRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowSeatService {
    private final ShowRepo showRepo;
}
