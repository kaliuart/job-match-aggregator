package com.artur.jobaggregator.project.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbeitnowResponse {
    private List<ArbeitnowResult> data;
}
