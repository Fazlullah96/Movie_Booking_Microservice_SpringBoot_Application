package com.example.clients;

import com.example.component.ScreenClientFallbackFactory;
import com.example.dtos.ScreenResponse;
import com.example.dtos.SeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "theatre-service", fallbackFactory = ScreenClientFallbackFactory.class)
public interface ScreenClient {
    @GetMapping("/api/screen/{id}")
    ScreenResponse getScreenById(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") int id
    );
    @GetMapping("/api/seat/screenId/{screenId}")
    List<SeatResponse> getSeatsByScreenId(
            @RequestHeader("Authorization") String token,
            @PathVariable("screenId") int screenId
    );
}
