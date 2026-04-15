package com.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowResponse {
    private Integer id;
    private Integer movieId;
    private Integer screenId;
    private LocalDate showDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
}
