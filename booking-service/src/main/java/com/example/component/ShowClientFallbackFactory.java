package com.example.component;

import com.example.clients.ShowClient;
import com.example.dtos.ShowResponse;
import com.example.dtos.ShowSeatResponse;
import com.example.exceptions.ServiceUnavailableException;
import com.example.exceptions.ShowNotFoundException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowClientFallbackFactory implements FallbackFactory<ShowClient> {
    @Override
    public ShowClient create(Throwable cause) {
        return new ShowClient() {
            @Override
            public ShowResponse getShowByShowId(String token, int showId) {
                if(cause instanceof FeignException.NotFound){
                    throw new ShowNotFoundException("Show not found for SHOWID: " + showId);
                }
                throw new ServiceUnavailableException("SERVICE UNAVAILABLE.....");
            }

            @Override
            public List<ShowSeatResponse> getAllShowSeatByIds(String token, List<Integer> showSeatIds) {
                throw new ServiceUnavailableException("SERVICE UNAVAILABLE......");
            }
        };
    }
}
