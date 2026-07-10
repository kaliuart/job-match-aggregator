package com.artur.jobaggregator.project.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MuseResult {
    //slug
    @JsonProperty("short_name")
    private String shortName;

    //tags
    private List<MuseCategory> categories;

    private String name;

    private String contents;

    private MuseCompany company;

    private MuseRef refs;

    private List<Location> locations;


    @Getter
     public static class Location {
        private String name;
    }
    @Getter
    public static class MuseRef {
        @JsonProperty("landing_page")
        private String landingPage;
    }
    @Getter
    public static class MuseCompany {
        private String name;
    }
    @Getter
    public static class MuseCategory {
        private String name;
    }

}
