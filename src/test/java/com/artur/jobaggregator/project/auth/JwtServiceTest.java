package com.artur.jobaggregator.project.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", "abcdefghijklmnoprstkdkadadwooadlawaldaldald");
        ReflectionTestUtils.setField(jwtService, "lifetime", 3600000);

        jwtService.init();
    }
    @Test
    void generateToken() {

    }

    @Test
    void validateToken() {
        String email = "email";

        assertTrue(jwtService.validateToken(generateToken(email)));
    }

    @Test
    void getEmailFromToken() {
    }
}