package com.example.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "show")
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull(message = "Movie Id cannot be Empty")
    private Integer movieId;
    @NotNull(message = "Screen Id cannot be Empty")
    private Integer screenId;
    private LocalDate showDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "show",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ShowSeat> showSeats;
}
