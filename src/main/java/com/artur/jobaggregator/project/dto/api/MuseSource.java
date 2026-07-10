package com.artur.jobaggregator.project.dto.api;

import com.artur.jobaggregator.project.JobMapper;
import com.artur.jobaggregator.project.entity.JobEntity;
import com.artur.jobaggregator.project.exception.externalservice.ArbeitnowResponseEmpyException;
import com.artur.jobaggregator.project.exception.externalservice.MuseResponseEmptyException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class MuseSource implements JobSource{
    private final RestClient client;
    private final JobMapper jobMapper;

    public MuseSource(RestClient client, JobMapper jobMapper) {
        this.client = client;
        this.jobMapper = jobMapper;
    }

    @Override
    public List<JobEntity> fetchJobs() {
        List<JobEntity> jobEntities = new ArrayList<>();

        MuseResponse response = client.get()
                .uri("https://www.arbeitnow.com/api/job-board-api")
                .retrieve()
                .body(MuseResponse.class);

        if (response == null || response.getResults() == null) {
            throw new MuseResponseEmptyException("Muse response contains no data");
        }

        for(MuseResult job: response.getResults()) {
            jobEntities.add(jobMapper.mapToJobEntityFromMuse(job));
        }
        return jobEntities;
    }
}
