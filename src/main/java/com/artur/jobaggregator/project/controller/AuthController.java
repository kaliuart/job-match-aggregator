package com.artur.jobaggregator.project.controller;

import com.artur.jobaggregator.project.auth.AuthResponse;
import com.artur.jobaggregator.project.dto.auth.LoginRequestDto;
import com.artur.jobaggregator.project.dto.auth.RegisterRequestDto;
import com.artur.jobaggregator.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody @Valid LoginRequestDto loginRequest) {
        String token = userService.loginUser(loginRequest);

        return new AuthResponse(token);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Void> registration(@RequestBody  @Valid RegisterRequestDto registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
