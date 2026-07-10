package com.artur.jobaggregator.project.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ArbeitnowResult{

    private String slug;

    private List<String> tags;

    private String title;

    @JsonProperty("company_name")
    private String companyName;

    private String description;

    private String url;

    private String location;

    private boolean remote;
}
