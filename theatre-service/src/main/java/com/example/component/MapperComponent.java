package com.example.component;

import com.example.dtos.*;
import com.example.models.City;
import com.example.models.Screen;
import com.example.models.Seat;
import com.example.models.Theatre;
import org.springframework.stereotype.Component;

@Component
public class MapperComponent {

    public City toCityModel(CityRequest request){
        return City
                .builder()
                .name(request.getName())
                .state(request.getState())
                .build();
    }

    public CityResponse toCityResponse(City city){
        return CityResponse
                .builder()
                .id(city.getId())
                .name(city.getName())
                .state(city.getState())
                .build();
    }

    public Theatre toTheatreModel(TheatreRequest request){
        return Theatre
                .builder()
                .name(request.getName())
                .address(request.getAddress())
                .build();
    }

    public TheatreResponse toTheatreResponse(Theatre theatre, CityResponse cityResponse){
        return TheatreResponse
                .builder()
                .name(theatre.getName())
                .address(theatre.getAddress())
                .city(cityResponse)
                .build();
    }

    public Screen toScreenModel(ScreenRequest request, Theatre theatre){
        return Screen
                .builder()
                .name(request.getName())
                .theatre(theatre)
                .build();
    }

    public ScreenResponse toScreenResponse(Screen screen, TheatreResponse theatre){
        return ScreenResponse
                .builder()
                .id(screen.getId())
                .name(screen.getName())
                .theatre(theatre)
                .build();
    }

    public SeatResponse toSeatResponse(Seat seat, ScreenResponse screenResponse){
        return SeatResponse
                .builder()
                .id(seat.getId())
                .seatRow(seat.getSeatRow())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .screenResponse(screenResponse)
                .build();
    }

    public Seat toSeatModel(SeatRequest request, Screen screen){
        return Seat
                .builder()
                .seatRow(request.getSeatRow())
                .seatNumber(request.getSeatNumber())
                .seatType(request.getSeatType())
                .screen(screen)
                .build();
    }
}
