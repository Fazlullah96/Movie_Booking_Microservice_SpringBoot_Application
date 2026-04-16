package com.example.service;

import com.example.component.MapperComponent;
import com.example.dtos.*;
import com.example.exception.ScreenNotFoundException;
import com.example.exception.SeatAlreadyExistsException;
import com.example.models.City;
import com.example.models.Screen;
import com.example.models.Seat;
import com.example.models.Theatre;
import com.example.repo.ScreenRepo;
import com.example.repo.SeatRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {
    private final SeatRepo seatRepo;
    private final ScreenRepo screenRepo;
    private final MapperComponent mapper;

    @Transactional
    public SeatResponse addSeat(SeatRequest request){
        boolean isSeatRowSeatNumberScreenIdExists = seatRepo.existsBySeatRowAndSeatNumberAndScreenId(request.getSeatRow(), request.getSeatNumber(), request.getScreenId());
        if(isSeatRowSeatNumberScreenIdExists){
            throw new SeatAlreadyExistsException("SeatRow: " + request.getSeatRow()+ " and SeatNumber: " + request.getSeatNumber()+" already exists in ScreenId: " + request.getScreenId());
        }
        Screen screen = screenRepo.findById(request.getScreenId()).orElseThrow(() -> {
            return new ScreenNotFoundException("ScreenId: " + request.getScreenId()+" not found");
        });
        Seat seat = mapper.toSeatModel(request, screen);
        Seat savedSeat = seatRepo.save(seat);
        Theatre theatre = screen.getTheatre();
        City city = theatre.getCity();
        return mapper.toSeatResponse(savedSeat, mapper.toScreenResponse(screen, mapper.toTheatreResponse(theatre, mapper.toCityResponse(city))));
    }

    @Transactional
    public List<SeatResponse> addMultipleSeats(ScreenSetupRequest request){
        Screen screen = screenRepo.findById(request.getScreenId()).orElseThrow(() -> {
            log.error("ScreenId {} not found", request.getScreenId());
            return new ScreenNotFoundException("ScreenId: " + request.getScreenId()+" not found");
        });
        Set<String> existingSeatInDb = screen
                .getSeats()
                .stream()
                .map(seat -> seat.getSeatRow()+"-"+seat.getSeatNumber())
                .collect(Collectors.toSet());
        Set<String> seatInCurrentRequest = new HashSet<>();
        List<Seat> needToSave = request
                .getSeats()
                .stream()
                .map(seat -> {
                    String seatIdentifier = seat.getSeatRow()+"-"+seat.getSeatNumber();
                    if(existingSeatInDb.contains(seatIdentifier)){
                        throw new SeatAlreadyExistsException("SeatRow " + seat.getSeatRow()+" and seatNumber "+seat.getSeatNumber()+" already exist");
                    }
                    if(!seatInCurrentRequest.add(seatIdentifier)){
                        throw new SeatAlreadyExistsException("Duplicate SeatRow "+seat.getSeatRow()+" and seatNumber " + seat.getSeatNumber()+" in this screen");
                    }
                    return mapper.toSeatModel(seat, screen);
                }).collect(Collectors.toList());
        List<Seat> savedSeats = seatRepo.saveAll(needToSave);
        return savedSeats
                .stream()
                .map(seat -> mapper.toSeatResponse(seat, mapper.toScreenResponse(screen, mapper.toTheatreResponse(screen.getTheatre(), mapper.toCityResponse(screen.getTheatre().getCity())))))
                .collect(Collectors.toList());
    }

//    public SeatResponse toSeatResponse(Seat seat, ScreenResponse screenResponse){
//        return SeatResponse
//                .builder()
//                .id(seat.getId())
//                .seatRow(seat.getSeatRow())
//                .seatNumber(seat.getSeatNumber())
//                .seatType(seat.getSeatType())
//                .screenResponse(screenResponse)
//                .build();
//    }
//
//    public Seat toSeatModel(SeatRequest request, Screen screen){
//        return Seat
//                .builder()
//                .seatRow(request.getSeatRow())
//                .seatNumber(request.getSeatNumber())
//                .seatType(request.getSeatType())
//                .screen(screen)
//                .build();
//    }
//
//    public ScreenResponse toScreenResponse(Screen screen, TheatreResponse theatre){
//        return ScreenResponse
//                .builder()
//                .id(screen.getId())
//                .name(screen.getName())
//                .theatre(theatre)
//                .build();
//    }
//
//    public TheatreResponse toTheatreResponse(Theatre theatre, CityResponse cityResponse){
//        return TheatreResponse
//                .builder()
//                .name(theatre.getName())
//                .address(theatre.getAddress())
//                .city(cityResponse)
//                .build();
//    }
//
//    public CityResponse toCityResponse(City city){
//        return CityResponse
//                .builder()
//                .id(city.getId())
//                .name(city.getName())
//                .state(city.getState())
//                .build();
//    }
}
