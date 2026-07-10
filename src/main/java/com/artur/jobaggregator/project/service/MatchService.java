package com.artur.jobaggregator.project.service;

import com.artur.jobaggregator.project.dto.matching.MatchRequestDto;
import com.artur.jobaggregator.project.dto.matching.MatchResultDto;
import com.artur.jobaggregator.project.entity.JobEntity;
import com.artur.jobaggregator.project.dto.api.GeminiResponseDto;
import com.artur.jobaggregator.project.exception.externalservice.GeminiResponseEmptyException;
import com.artur.jobaggregator.project.exception.externalservice.GeminiUnavailableException;
import com.artur.jobaggregator.project.exception.notfound.JobNotFoundException;
import com.artur.jobaggregator.project.repository.JobRepository;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class MatchService {
    private final JobRepository jobRepository;
    private final RestClient client;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    public MatchService(JobRepository jobRepository, RestClient client, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.client = client;
        this.objectMapper = objectMapper;
    }
    public MatchResultDto match(MatchRequestDto matchRequest, Long jobId) {
        Map<String, Object> requestBody = getRequestBody(matchRequest,jobId);
        try {
            GeminiResponseDto geminiResponse = client
                    .post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent")
                    .header("X-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponseDto.class);

            if (geminiResponse == null || geminiResponse.getCandidates() == null) {
                throw new GeminiResponseEmptyException("Gemini response contains no data");
            }
            String jsonResult = geminiResponse
                        .getCandidates()
                        .getFirst()
                        .getContent()
                        .getParts()
                        .getFirst()
                        .getText();

                return objectMapper.readValue(jsonResult, MatchResultDto.class);
            }
        catch (HttpServerErrorException e) {
            throw new GeminiUnavailableException("Gemini unavailable");
        }

    }

    public Map<String, Object> getRequestBody(MatchRequestDto matchRequest, Long jobId) {
        JobEntity job = jobRepository
                .findById(jobId)
                .orElseThrow(
                        () -> new JobNotFoundException(
                                "Job not found with ID " + jobId
                        )
                );

        String cleanDescription = Jsoup.parse(job.getDescription()).text();

        String prompt = """
        You are an expert technical recruiter and ATS (Applicant Tracking System).
        Analyze how well the candidate's resume matches the job vacancy.
    
        Evaluate the match based on these criteria:
        1. Technical skills match (required technologies, languages, frameworks)
        2. Experience level match (years, seniority)
        3. Domain/industry relevance
        4. Education and certifications (if relevant)
        5. Soft skills and responsibilities alignment
    
        RESUME:
        %s
    
        JOB VACANCY:
        Title: %s
        Description: %s
        """
                .formatted(matchRequest.getResume(), job.getTitle(), cleanDescription);

        return  Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "matchPercentage", Map.of("type", "integer"),
                                        "matchedSkills", Map.of("type", "array", "items", Map.of("type", "string")),
                                        "missingSkills", Map.of("type", "array", "items", Map.of("type", "string")),
                                        "experienceMatch", Map.of("type", "string"),
                                        "summary", Map.of("type", "string"),
                                        "recommendation", Map.of("type", "string")
                                )
                        )
                )
        );

    }
}
