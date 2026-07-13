package com.artur.jobaggregator.source;

import com.artur.jobaggregator.mapper.JobMapper;
import com.artur.jobaggregator.dto.api.ArbeitnowResponse;
import com.artur.jobaggregator.dto.api.ArbeitnowResult;
import com.artur.jobaggregator.entity.JobEntity;
import com.artur.jobaggregator.exception.externalservice.ArbeitnowResponseEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArbeitnowSource implements JobSource {

    private final RestClient client;
    private final JobMapper jobMapper;
    private final Logger logger = LoggerFactory.getLogger(ArbeitnowSource.class);

    public ArbeitnowSource(RestClient client, JobMapper jobMapper) {
        this.client = client;
        this.jobMapper = jobMapper;
    }

    @Override
    public List<JobEntity> fetchJobs() {
        List<JobEntity> jobEntities = new ArrayList<>();

        ArbeitnowResponse response = client.get()
                .uri("https://www.arbeitnow.com/api/job-board-api")
                .retrieve()
                .body(ArbeitnowResponse.class);

        if (response == null || response.getData() == null) {
            throw new ArbeitnowResponseEmptyException("Arbeitnow response contains no data");
        }
        logger.info("ArbeitnowAPI responded successfully");
        for(ArbeitnowResult job: response.getData()) {
                jobEntities.add(jobMapper.mapToJobEntityFromArbeitnow(job));
        }
        return jobEntities;
    }
}
