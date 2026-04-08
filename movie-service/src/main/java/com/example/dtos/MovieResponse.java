package com.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieResponse {
    private Integer id;
    private String title;
    private String description;
    private Integer durationInMinutes;
    private LocalDate releaseDate;
    private String language;
    private String genre;
    private String posterImageUrl;
    private String trailerUrl;
}
