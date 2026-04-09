package com.example.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatRequest {
    @Pattern(regexp = "^[A-O]$", message = "Seat row must be a single character between A and O")
    private String seatRow;
    @Max(value = 5)
    @Min(value = 1)
    private Integer seatNumber;
    @Pattern(regexp = "^(VIP|PREMIUM|STANDARD)$", message = "Invalid Seat Type")
    private SeatType seatType;
    @NotNull(message = "ScreenId cannot be Empty")
    private Integer screenId;

    public enum SeatType{
        VIP,
        PREMIUM,
        STANDARD
    }
}
