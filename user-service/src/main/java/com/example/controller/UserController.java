package com.example.controller;

import com.example.dtos.LoginRequest;
import com.example.dtos.TokenResponse;
import com.example.dtos.UserRegistration;
import com.example.dtos.UserResponse;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserByUserId(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
