package com.artur.jobaggregator.project.dto.matching;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MatchResultDto implements Serializable {
    private int matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String experienceMatch;
    private String summary;
    private String recommendation;
}
