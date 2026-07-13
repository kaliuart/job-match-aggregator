package com.artur.jobaggregator.service;

import com.artur.jobaggregator.source.JobSource;
import com.artur.jobaggregator.entity.JobEntity;
import com.artur.jobaggregator.mapper.JobMapper;
import com.artur.jobaggregator.exception.notfound.JobNotFoundException;
import com.artur.jobaggregator.repository.JobRepository;
import com.artur.jobaggregator.dto.JobDto;

import java.util.ArrayList;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final List<JobSource> jobSources;

    @Value("${job-filter.it-keywords}")
    private List<String> keyWords;

    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    public JobService(JobRepository jobRepository, JobMapper jobMapper, List<JobSource> jobSources) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.jobSources = jobSources;
    }

    @Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void fetchAndSaveJobs() {

        List<JobEntity> all = new ArrayList<>();
        for (JobSource jobSource : jobSources) {
            all.addAll(jobSource.fetchJobs());
        }
        for (JobEntity job: all) {
            if (!isItJob(job)) {
                continue;
            }
            Optional<JobEntity> existing = jobRepository.findBySlug(job.getSlug());

            if (existing.isPresent()) {
                JobEntity jobEntity = existing.get();
                if (!jobEntity.equals(job)) {

                    jobEntity.setTitle(job.getTitle());
                    jobEntity.setDescription(job.getDescription());
                    jobEntity.setRemote(job.isRemote());
                    jobEntity.setCompanyName(job.getCompanyName());
                    jobEntity.setLocation(job.getLocation());
                    jobEntity.setUrl(job.getUrl());
                    jobEntity.setTags(job.getTags());

                    jobRepository.save(jobEntity);
                    logger.info("Job information updated successfully");
                }
            }
            else {
                jobRepository.save(job);
                logger.info("Job successfully saved");
            }
        }
    }

    public Page<JobDto> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable).map(jobMapper::mapToJobDto);
    }

    public JobDto getJobById(Long id) {
        return jobMapper.mapToJobDto(jobRepository
                .findById(id)
                .orElseThrow(
                        () -> new JobNotFoundException(
                                "Job not found with ID " + id
                        )
                )
        );
    }

    public Page<JobDto> searchJobs(String keyword, String location, Boolean remote, Pageable pageable) {
        return jobRepository.search(keyword, location, remote, pageable).map(jobMapper::mapToJobDto);
    }

     private boolean isItJob(JobEntity job) {
        String title = job.getTitle().toLowerCase();
        List<String> tags = job.getTags().stream().map(tag -> tag.toLowerCase()).toList();
        String slug = job.getSlug().toLowerCase();

        for (String keyword:keyWords) {
            if (title.contains(keyword) || tags.contains(keyword) || slug.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

}
