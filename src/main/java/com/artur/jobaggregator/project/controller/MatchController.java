package com.artur.jobaggregator.project.controller;

import com.artur.jobaggregator.project.dto.matching.MatchRequestDto;
import com.artur.jobaggregator.project.dto.matching.MatchResultDto;
import com.artur.jobaggregator.project.service.MatchService;
import org.springframework.web.bind.annotation.*;

@RestController
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/api/match/{jobId}")
    public MatchResultDto matchResume(@RequestBody MatchRequestDto matchRequest, @PathVariable Long jobId) {
        return matchService.match(matchRequest, jobId);

    }


}
