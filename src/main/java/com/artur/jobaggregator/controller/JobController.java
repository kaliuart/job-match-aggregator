package com.artur.jobaggregator.controller;

import com.artur.jobaggregator.dto.JobDto;
import com.artur.jobaggregator.service.JobService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/api/jobs/{id}")
    public JobDto getOne(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @GetMapping("/api/jobs")
    public PagedModel<JobDto> getAll(@ParameterObject @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return new PagedModel<>(jobService.getAllJobs(pageable));
    }

    @GetMapping("/api/jobs/search")
    public PagedModel<JobDto> search(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String location,
                                     @RequestParam(required = false) Boolean remote,
                                     @ParameterObject @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return new PagedModel<>(jobService.searchJobs(keyword, location, remote, pageable));
    }
}