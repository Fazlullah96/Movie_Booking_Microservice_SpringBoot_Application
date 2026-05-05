package com.example.clients;

import com.example.component.ShowClientFallbackFactory;
import com.example.dtos.ShowResponse;
import com.example.dtos.ShowSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "showtime-service", fallbackFactory = ShowClientFallbackFactory.class)
public interface ShowClient {
    @GetMapping("/api/showtime/{showId}")
    ShowResponse getShowByShowId(
            @RequestHeader("Authorization") String token,
            @PathVariable("showId") int showId
    );

    @GetMapping("/api/showtime/showseat")
    List<ShowSeatResponse> getAllShowSeatByIds(
            @RequestHeader("Authorization") String token,
            @RequestParam("ids") List<Integer> showSeatIds
    );

    @PutMapping("/api/showtime/showseat/status/update/locked")
    List<ShowSeatResponse> updateShowSeatStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam("showSeatIds") List<Integer> showSeatIds
    );

    @PutMapping("/api/showtime/showseat/status/update/revert")
    List<ShowSeatResponse> revertUpdatedShowSeatStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam("showSeatIds") List<Integer> showSeatIds
    );
}
