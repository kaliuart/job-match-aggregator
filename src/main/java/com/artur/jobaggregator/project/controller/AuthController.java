package com.artur.jobaggregator.project.controller;

import com.artur.jobaggregator.project.auth.AuthResponse;
import com.artur.jobaggregator.project.dto.LoginRequestDto;
import com.artur.jobaggregator.project.dto.RegisterRequestDto;
import com.artur.jobaggregator.project.service.UserService;
import jakarta.validation.Valid;
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
    public void registration(@RequestBody  @Valid RegisterRequestDto registerRequest) {
        userService.registerUser(registerRequest);
    }

    @PostMapping("/auth/logout")
    public void logout(){
        userService.logoutUser();
    }




}
