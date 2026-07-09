package com.artur.jobaggregator.project.service;

import com.artur.jobaggregator.project.auth.JwtService;
import com.artur.jobaggregator.project.dto.LoginRequestDto;
import com.artur.jobaggregator.project.dto.RegisterRequestDto;
import com.artur.jobaggregator.project.entity.UserEntity;
import com.artur.jobaggregator.project.exception.badrequest.InvalidCredentialsException;
import com.artur.jobaggregator.project.exception.conflict.EmailAlreadyExistsException;
import com.artur.jobaggregator.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String loginUser(LoginRequestDto loginRequest) {
        Optional<UserEntity> existing = userRepository.findByEmail(loginRequest.getEmail());

        if (existing.isPresent()) {
            UserEntity user = existing.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.info("User successfully signed in");

                return jwtService.generateToken(user.getEmail());
            }
            else {
                throw new InvalidCredentialsException("Invalid email or password");
            }
        }
        else throw  new InvalidCredentialsException("Invalid email or password");

    }

    public void registerUser(RegisterRequestDto registerRequest) {
        UserEntity userEntity = new UserEntity();

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("A user with this email already exists");
        }
        else {
            userEntity.setEmail(registerRequest.getEmail());
            userEntity.setName(registerRequest.getName());
            userEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            userRepository.save(userEntity);
            logger.info("User successfully saved");
        }
    }
}
