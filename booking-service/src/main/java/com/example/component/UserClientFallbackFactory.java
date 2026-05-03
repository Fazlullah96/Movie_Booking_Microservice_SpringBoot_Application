package com.example.component;

import com.example.clients.UserClient;
import com.example.dtos.UserResponse;
import com.example.exceptions.ServiceUnavailableException;
import com.example.exceptions.UserNotFoundException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public UserResponse getUserByUserId(String token, String userId) {
                if(cause instanceof FeignException.NotFound){
                    throw new UserNotFoundException("User not found for USERID: " + userId);
                }
                throw new ServiceUnavailableException("SERVICE UNAVAILABLE......");
            }
        };
    }
}
