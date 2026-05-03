package com.example.component;

import com.example.clients.ScreenClient;
import com.example.dtos.ScreenResponse;
import com.example.dtos.SeatResponse;
import com.example.exception.ScreenNotFoundException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScreenClientFallbackFactory implements FallbackFactory<ScreenClient> {
    @Override
    public ScreenClient create(Throwable cause) {
        return new ScreenClient() {
            @Override
            public ScreenResponse getScreenById(String token, int id) {
                if(cause instanceof FeignException.NotFound){
                    throw new ScreenNotFoundException("Screen Not Found for ScreenId: " + id);
                }
                throw new RuntimeException("Service Not Available......");
            }

            @Override
            public List<SeatResponse> getSeatsByScreenId(String token, int screenId) {
                throw new RuntimeException("Service Not Available.....");
            }
        };
    }
}
