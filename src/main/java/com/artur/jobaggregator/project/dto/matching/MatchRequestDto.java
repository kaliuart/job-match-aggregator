package com.artur.jobaggregator.project.dto.matching;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MatchRequestDto {
    @NotBlank
    private String resume;
}
