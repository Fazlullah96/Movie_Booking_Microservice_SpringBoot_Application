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
public class TheatreRequest {
    @NotBlank(message = "Name cannot be Empty")
    private String name;
    @NotBlank(message = "Address cannot be Empty")
    private String address;
    @NotNull(message = "CityId cannot be Empty")
    private Integer cityId;
}
