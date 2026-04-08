package com.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieRequest {
    @NotBlank(message = "Title Cannot be Empty")
    private String title;
    @NotBlank(message = "Description Cannot be Empty")
    private String description;
    @NotNull(message = "Duration in Minutes Cannot be Empty")
    private Integer durationInMinutes;
    @NotNull(message = "Release Date Cannot be Empty")
    private LocalDate releaseDate;
    @NotBlank(message = "Language Cannot be Empty")
    private String language;
    @NotBlank(message = "Genre Cannot be Empty")
    private String genre;
    @NotBlank(message = "Poster Image Url Cannot be Empty")
    private String posterImageUrl;
    @NotBlank(message = "Trailer Url Cannot be Empty")
    private String trailerUrl;
}
