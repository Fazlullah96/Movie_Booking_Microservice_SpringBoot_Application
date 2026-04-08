package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Title should not be Empty")
    private String title;
    @NotBlank(message = "Description should not be Empty")
    private String description;
    @NotNull(message = "Duration of the movie cannot be Empty")
    private Integer durationInMinutes;
    @NotNull(message = "Release Date cannot be Empty")
    private LocalDate releaseDate;
    @NotBlank(message = "Language cannot be Empty")
    private String language;
    @NotBlank(message = "Genre cannot be Empty")
    private String genre;
    @NotBlank(message = "Poster Image Url cannot be Empty")
    private String posterImageUrl;
    @NotBlank(message = "Trailer Url cannot be Empty")
    private String trailerUrl;
    @NotNull(message = "IsActive cannot be Empty")
    private Boolean isActive;
}
