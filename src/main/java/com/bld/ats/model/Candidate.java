package com.bld.ats.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A completely generalized Candidate model suitable for any profession.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Candidate(
    PersonalInfo personalInfo,
    String summary,
    
    // The ultimate flexible bucket for skills: 
    // e.g., "Technical" -> ["Java"], "Languages" -> ["French"], "Soft" -> ["Leadership"]
    Map<String, List<String>> categorizedSkills, 
    
    List<Experience> experience,
    List<Education> education,
    List<Project> projects,
    
    // A catch-all for anything weird or specific the AI finds (certifications, awards, hobbies)
    Map<String, Object> additionalInfo 
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PersonalInfo(
        String fullName,
        String email,
        String phone,
        String location,
        
        // Instead of hardcoding githubUrl or linkedinUrl, we use a map for ALL links
        // e.g., "LinkedIn" -> "url", "Portfolio" -> "url", "Dribbble" -> "url"
        Map<String, String> webLinks 
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Experience(
        String role,          // Generalized from "jobTitle"
        String organization,  // Generalized from "company" (could be a school, NGO, etc.)
        String startDate,
        String endDate,
        String description,
        List<String> highlights // Extracted bullet points of achievements
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Education(
        String degreeOrCertificate,
        String institution,
        String dateRange,
        
        // Catch-all for things like GPA, Honors, Minors, etc.
        Map<String, String> details 
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Project(
        String name,
        String description,
        
        // Generalized from "technologiesUsed". A chef might have "ingredients", a designer "tools".
        List<String> tags 
    ) {}
}