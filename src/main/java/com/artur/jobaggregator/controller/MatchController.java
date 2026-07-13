package com.artur.jobaggregator.controller;

import com.artur.jobaggregator.dto.matching.MatchRequestDto;
import com.artur.jobaggregator.dto.matching.MatchResultDto;
import com.artur.jobaggregator.service.MatchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/api/match/{jobId}")
    public MatchResultDto matchResume(@Valid @RequestBody MatchRequestDto matchRequest, @PathVariable Long jobId) {
        return matchService.match(matchRequest, jobId);
    }
}