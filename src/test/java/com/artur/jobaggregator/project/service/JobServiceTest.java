package com.artur.jobaggregator.project.service;

import com.artur.jobaggregator.project.JobMapper;
import com.artur.jobaggregator.project.dto.JobDto;
import com.artur.jobaggregator.project.entity.JobEntity;
import com.artur.jobaggregator.project.exception.notfound.JobNotFoundException;
import com.artur.jobaggregator.project.repository.JobRepository;
import com.artur.jobaggregator.project.source.JobSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Sort;
import java.util.Optional;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock
    private JobRepository jobRepository;

    @Spy
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    @Test
    void fetchAndSaveJobs_newJob_savesIt() {
        JobSource source = mock(JobSource.class);

        JobEntity job = new JobEntity();

        job.setSlug("developer");
        job.setTags(List.of("java"));
        job.setTitle("software");

        JobService service = new JobService(jobRepository, jobMapper, List.of(source));
        ReflectionTestUtils.setField(service, "keyWords", List.of("developer"));

        when(source.fetchJobs()).thenReturn(List.of(job));

        when(jobRepository.findBySlug(job.getSlug())).thenReturn(Optional.empty());

        service.fetchAndSaveJobs();

        verify(jobRepository).save(job);

    }
    @Test
    void fetchAndSaveJobs_existingJob_updatesIt() {
        JobSource source = mock(JobSource.class);

        JobEntity jobToUpdate = new JobEntity();

        jobToUpdate.setSlug("developer");
        jobToUpdate.setTags(List.of("java"));
        jobToUpdate.setTitle("new title");

        JobEntity existingJob = new JobEntity();

        existingJob.setSlug("developer");
        existingJob.setTags(List.of("java"));
        existingJob.setTitle("old title");

        JobService service = new JobService(jobRepository, jobMapper, List.of(source));
        ReflectionTestUtils.setField(service, "keyWords", List.of("developer"));

        when(source.fetchJobs()).thenReturn(List.of(jobToUpdate));

        when(jobRepository.findBySlug(jobToUpdate.getSlug())).thenReturn(Optional.of(existingJob));

        service.fetchAndSaveJobs();

        verify(jobRepository).save(existingJob);
        assertEquals("new title" , existingJob.getTitle());

    }


    @Test
    void getAllJobs_withJobs_returnsMappedDtos() {

        JobEntity entity1 = new JobEntity();
        entity1.setTitle("1 entity");

        JobEntity entity2 = new JobEntity();
        entity2.setTitle("2 entity");

        JobEntity entity3 = new JobEntity();
        entity3.setTitle("3 entity");

        List<JobEntity> entities = List.of(entity1, entity2, entity3);

        when(jobRepository.findAll()).thenReturn(entities);


        List<JobDto> result = jobService.getAllJobs();

        assertEquals("1 entity", result.get(0).getTitle());
        assertEquals("2 entity", result.get(1).getTitle());
        assertEquals("3 entity", result.get(2).getTitle());

    }

    @Test
    void getAllJobs_noJobs_returnsEmptyList() {
        when(jobRepository.findAll()).thenReturn(List.of());

        List<JobDto> result = jobService.getAllJobs();

        assertTrue(result.isEmpty());
    }

    @Test
    void getJobById_jobExists_returnsDto() {
        JobEntity entity = new JobEntity();
        entity.setTitle("test_title");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(entity));

        JobDto result = jobService.getJobById(1L);

        assertEquals("test_title", result.getTitle());
    }

    @Test
    void getJobById_jobMissing_throwsNotFound() {
        Long jobId = 15L;

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        JobNotFoundException exception = assertThrows(JobNotFoundException.class, () -> jobService.getJobById(jobId));
        assertEquals("Job not found with ID " + jobId, exception.getMessage());
    }

    @Test
    void searchJobs_matchingCriteria_returnsMappedDtos() {
        String keyword = "java";

        String location = "Prague";

        Boolean remote = false;

        String sortBy = "title";

        Sort sort = Sort.by(sortBy).descending();

        JobEntity job1 = new JobEntity();

        job1.setTitle("java");

        job1.setRemote(false);

        job1.setLocation("Prague");

        when(jobRepository.search(keyword,location,remote, sort)).thenReturn(List.of(job1));

        List<JobDto> result = jobService.searchJobs(keyword,location,remote,sort);

        assertEquals("java", result.get(0).getTitle());
        assertEquals("Prague", result.get(0).getLocation());
        assertFalse(result.get(0).isRemote());
    }

}