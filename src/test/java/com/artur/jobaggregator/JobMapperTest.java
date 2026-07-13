package com.artur.jobaggregator;

import com.artur.jobaggregator.dto.JobDto;
import com.artur.jobaggregator.dto.api.ArbeitnowResult;
import com.artur.jobaggregator.dto.api.MuseResult;
import com.artur.jobaggregator.entity.JobEntity;
import com.artur.jobaggregator.mapper.JobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobMapperTest {
    private JobMapper jobMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        jobMapper = new JobMapper();
        objectMapper = new ObjectMapper();
    }

    @Test
    void mapToJobDto_mapsAllFields() {
        JobEntity jobEntity = new JobEntity();

        jobEntity.setTitle("TEST_title");
        jobEntity.setCompanyName("TEST_company_name");
        jobEntity.setDescription("TEST_description");
        jobEntity.setTags(List.of("TEST_tag"));
        jobEntity.setLocation("TEST1_location");
        jobEntity.setUrl("TEST_url");
        jobEntity.setRemote(false);

        JobDto jobDto = jobMapper.mapToJobDto(jobEntity);

        assertEquals("TEST_title", jobDto.getTitle());
        assertEquals("TEST_company_name", jobDto.getCompanyName());
        assertEquals("TEST_description", jobDto.getDescription());
        assertEquals(List.of("TEST_tag"), jobDto.getTags());
        assertEquals("TEST1_location", jobDto.getLocation());
        assertEquals("TEST_url", jobDto.getUrl());
        assertFalse(jobDto.isRemote());
    }


    @Test
    void mapToJobEntityFromMuse_mapsAllFields() {

        String json = """
    {
      "name": "TEST_title",
      "short_name": "TEST_slug",
      "contents": "TEST_description",
      "company": { "name": "TEST_company_name" },
      "locations": [ { "name": "TEST_location" } ],
      "refs": { "landing_page": "TEST_url" },
      "categories": [ { "name": "TEST_tag" } ]
    }
    """;

        MuseResult result = objectMapper.readValue(json, MuseResult.class);

        JobEntity entity = jobMapper.mapToJobEntityFromMuse(result);


        assertEquals("TEST_title", entity.getTitle());
        assertEquals("TEST_company_name", entity.getCompanyName());
        assertEquals("TEST_description", entity.getDescription());
        assertEquals(List.of("TEST_tag"), entity.getTags());
        assertEquals("TEST_location", entity.getLocation());
        assertEquals("TEST_url", entity.getUrl());
        assertEquals("TEST_slug", entity.getSlug());
        assertFalse(entity.isRemote());

    }

    @Test
    void mapToJobEntityFromArbeitnow_mapsAllFields() {

        String json = """
    {
      "title": "TEST_title",
      "slug": "TEST_slug",
      "description": "TEST_description",
      "company_name": "TEST_company_name",
      "location": "TEST_location",
      "url": "TEST_url",
      "tags": [ "TEST_tag" ],
      "remote" : false
    }
    """;

        ArbeitnowResult result = objectMapper.readValue(json, ArbeitnowResult.class);

        JobEntity entity = jobMapper.mapToJobEntityFromArbeitnow(result);


        assertEquals("TEST_title", entity.getTitle());
        assertEquals("TEST_company_name", entity.getCompanyName());
        assertEquals("TEST_description", entity.getDescription());
        assertEquals(List.of("TEST_tag"), entity.getTags());
        assertEquals("TEST_location", entity.getLocation());
        assertEquals("TEST_url", entity.getUrl());
        assertEquals("TEST_slug", entity.getSlug());
        assertFalse(entity.isRemote());

    }

    @Test
    void mapToJobEntityFromMuse_emptyLocations_setsNotSpecified() {
        String json = """
    {
      "name": "TEST_title",
      "short_name": "TEST_slug",
      "contents": "TEST_description",
      "company": { "name": "TEST_company_name" },
      "locations": [],
      "refs": { "landing_page": "TEST_url" },
      "categories": [ { "name": "TEST_tag" } ]
    }
    """;
        MuseResult result = objectMapper.readValue(json, MuseResult.class);

        JobEntity entity = jobMapper.mapToJobEntityFromMuse(result);

        assertEquals("Not specified", entity.getLocation());
    }

    @Test
    void mapToJobEntityFromMuse_nullCategories_setsEmptyTags() {
        String json = """
    {
      "name": "TEST_title",
      "short_name": "TEST_slug",
      "contents": "TEST_description",
      "company": { "name": "TEST_company_name" },
      "locations": [ { "name": "TEST_location" } ],
      "refs": { "landing_page": "TEST_url" }
    }
    """;
        MuseResult result = objectMapper.readValue(json, MuseResult.class);

        JobEntity entity = jobMapper.mapToJobEntityFromMuse(result);

        assertEquals(List.of(), entity.getTags());
    }

    @Test
    void mapToJobEntityFromMuse_remoteLocation_setsRemoteTrue() {
        String json = """
    {
      "name": "TEST_title",
      "short_name": "TEST_slug",
      "contents": "TEST_description",
      "company": { "name": "TEST_company_name" },
      "locations": [ { "name": "Flexible / Remote" } ],
      "refs": { "landing_page": "TEST_url" },
      "categories": [ { "name": "TEST_tag" } ]
    }
    """;
        MuseResult result = objectMapper.readValue(json, MuseResult.class);

        JobEntity entity = jobMapper.mapToJobEntityFromMuse(result);

        assertTrue(entity.isRemote());
    }
}