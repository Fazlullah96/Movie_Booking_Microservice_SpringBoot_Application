package com.example.controller;

import com.example.dtos.LoginRequest;
import com.example.dtos.TokenResponse;
import com.example.dtos.UserRegistration;
import com.example.dtos.UserResponse;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/public/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistration request){
        return new ResponseEntity<>(userService.addUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/public/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request){
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

}
