package com.artur.jobaggregator.project.service;

import com.artur.jobaggregator.project.auth.JwtService;
import com.artur.jobaggregator.project.dto.auth.LoginRequestDto;
import com.artur.jobaggregator.project.dto.auth.RegisterRequestDto;
import com.artur.jobaggregator.project.entity.UserEntity;
import com.artur.jobaggregator.project.exception.badrequest.InvalidCredentialsException;
import com.artur.jobaggregator.project.exception.conflict.EmailAlreadyExistsException;
import com.artur.jobaggregator.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void loginUser_validCredentials_returnsToken() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        loginRequestDto.setEmail("test@gmail.com");
        loginRequestDto.setPassword("password");

        UserEntity user = new UserEntity();

        user.setEmail("test@gmail.com");
        user.setPassword("password");


        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(true);

        when(jwtService.generateToken(user.getEmail())).thenReturn("successful_token");

        String resultToken = userService.loginUser(loginRequestDto);
        assertEquals("successful_token", resultToken);


    }

    @Test
    void loginUser_userNotFound_throwsInvalidCredentials() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        loginRequestDto.setEmail("test_wrong@gmail.com");
        loginRequestDto.setPassword("password");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(loginRequestDto)
        );
        assertEquals("Invalid email or password", exception.getMessage());

    }
    @Test
    void loginUser_passwordNotMatch_throwsInvalidCredentials () {
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        loginRequestDto.setEmail("test@gmail.com");
        loginRequestDto.setPassword("password_wrong");

        UserEntity user = new UserEntity();

        user.setEmail("test@gmail.com");
        user.setPassword("password");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(loginRequestDto)
        );
        assertEquals("Invalid email or password", exception.getMessage());

    }

    @Test
    void registerUser_newUser_savesUser() {
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@gmail.com");
        registerRequestDto.setPassword("password");
        registerRequestDto.setName("name");
        registerRequestDto.setPhoneNumber("number");

        String hashPassword = "12345";

        when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(registerRequestDto.getPassword())).thenReturn(hashPassword);

        userService.registerUser(registerRequestDto);

        verify(userRepository).save(captor.capture());

        UserEntity saved = captor.getValue();

        assertEquals("test@gmail.com", saved.getEmail());
        assertEquals("name", saved.getName());
        assertEquals("12345", saved.getPassword());
    }

    @Test
    void registerUser_duplicateEmail_throwsConflict() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@gmail.com");
        registerRequestDto.setPassword("password");
        registerRequestDto.setName("name");
        registerRequestDto.setPhoneNumber("number");


        when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(registerRequestDto)
        );

        assertEquals("A user with this email already exists", exception.getMessage());


    }
}