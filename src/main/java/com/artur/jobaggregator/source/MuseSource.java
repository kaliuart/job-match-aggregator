package com.artur.jobaggregator.source;

import com.artur.jobaggregator.mapper.JobMapper;
import com.artur.jobaggregator.dto.api.MuseResponse;
import com.artur.jobaggregator.dto.api.MuseResult;
import com.artur.jobaggregator.entity.JobEntity;
import com.artur.jobaggregator.exception.externalservice.MuseResponseEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class MuseSource implements JobSource {

    private final RestClient client;
    private final JobMapper jobMapper;
    private final Logger logger = LoggerFactory.getLogger(MuseSource.class);

    public MuseSource(RestClient client, JobMapper jobMapper) {
        this.client = client;
        this.jobMapper = jobMapper;
    }

    @Override
    public List<JobEntity> fetchJobs() {
        List<JobEntity> jobEntities = new ArrayList<>();

        MuseResponse response = client.get()
                .uri("https://www.themuse.com/api/public/jobs?page=1&category=Software Engineering&category=Computer and IT")
                .retrieve()
                .body(MuseResponse.class);

        if (response == null || response.getResults() == null) {
            throw new MuseResponseEmptyException("Muse response contains no data");
        }

        logger.info("MuseAPI responded successfully");

        for(MuseResult job: response.getResults()) {
            jobEntities.add(jobMapper.mapToJobEntityFromMuse(job));
        }
        return jobEntities;
    }
}
