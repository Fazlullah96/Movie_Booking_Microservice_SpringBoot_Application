package com.example.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityRequest {
    @NotBlank(message = "Name cannot be Empty")
    private String name;
    @NotBlank(message = "State cannot be Empty")
    private String state;
}
