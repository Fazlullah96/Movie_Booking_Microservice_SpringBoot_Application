package com.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScreenRequest {
    @NotBlank(message = "Name cannot be Empty")
    private String name;
    @NotNull(message = "TheatreId cannot be Empty")
    private Integer theatreId;
}
