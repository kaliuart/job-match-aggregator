package com.artur.jobaggregator.project;

import com.artur.jobaggregator.project.dto.JobDto;
import com.artur.jobaggregator.project.dto.api.ArbeitnowResult;
import com.artur.jobaggregator.project.dto.api.MuseResult;
import com.artur.jobaggregator.project.entity.JobEntity;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    public JobDto mapToJobDto(JobEntity jobEntity) {
        JobDto job = new JobDto();

        job.setTitle(jobEntity.getTitle());
        job.setDescription(jobEntity.getDescription());
        job.setRemote(jobEntity.isRemote());
        job.setUrl(jobEntity.getUrl());
        job.setCompanyName(jobEntity.getCompanyName());
        job.setTags(jobEntity.getTags());
        job.setLocation(jobEntity.getLocation());

        return job;
    }

    public JobEntity mapToJobEntityFromMuse(MuseResult museResult) {
        JobEntity jobEntity = new JobEntity();

        jobEntity.setTitle(museResult.getName());
        jobEntity.setCompanyName(museResult.getCompany().getName());
        jobEntity.setDescription(museResult.getContents());
        jobEntity.setTags(museResult.getCategories());
        jobEntity.setLocation(museResult.getLocations().getFirst().getName());
        jobEntity.setUrl(museResult.getRefs().getLandingPage());

        return jobEntity;
    }

    public JobEntity mapToJobEntityFromArbeitnow(ArbeitnowResult arbeitnowResult) {
        JobEntity jobEntity = new JobEntity();

        jobEntity.setTitle(arbeitnowResult.getTitle());
        jobEntity.setCompanyName(arbeitnowResult.getCompanyName());
        jobEntity.setDescription(arbeitnowResult.getDescription());
        jobEntity.setTags(arbeitnowResult.getTags());
        jobEntity.setLocation(arbeitnowResult.getLocation());
        jobEntity.setUrl(arbeitnowResult.getUrl());

        return jobEntity;
    }
}
