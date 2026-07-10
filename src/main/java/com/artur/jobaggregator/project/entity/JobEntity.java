package com.artur.jobaggregator.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String slug;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;

    private String title;

    @JsonProperty("company_name")
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String url;

    private String location;

    private boolean remote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        JobEntity jobEntity = (JobEntity) o;
        return jobEntity.remote == this.remote
                && Objects.equals(jobEntity.companyName, companyName)
                && Objects.equals(jobEntity.description, description)
                && Objects.equals(jobEntity.location, location)
                && Objects.equals(jobEntity.title, title)
                && Objects.equals(jobEntity.slug, slug)
                && Objects.equals(jobEntity.url, url)
                && Objects.deepEquals(jobEntity.tags, tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug);
    }
}

