package com.artur.jobaggregator.project.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = ("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=)]).{8,20}$"))
    private String password;

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank
    @Pattern(regexp = "^(\\+420)?[0-9]{9}$")
    @JsonProperty("phone_number")
    private String phoneNumber;

}
