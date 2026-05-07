package com.bld.ats.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The main root object that holds the entire Job JSON payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Job(
    JobDetails jobDetails,
    JobRequirements requirements,
    List<String> responsibilities
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JobDetails(
        String title,
        String company,
        String location,
        String employmentType
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JobRequirements(
        SkillSet mandatorySkills,
        SkillSet niceToHaveSkills,
        int minimumExperienceYears,
        String minimumEducationLevel
    ) {}

    /**
     * Notice how we only have to write this once, but we use it 
     * for both mandatorySkills AND niceToHaveSkills in the requirements!
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SkillSet(
        List<String> programmingLanguages,
        List<String> frameworksAndTools,
        List<String> softSkills
    ) {}
}