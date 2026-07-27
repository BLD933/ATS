package com.bld.ats.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The main root object that holds the entire Job JSON payload.
 * Converted to standard classes with record-style accessors to prevent breaking existing code.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {

    private JobDetails jobDetails;
    private JobRequirements requirements;
    private List<String> responsibilities;

    public Job() {}

    public Job(JobDetails jobDetails, JobRequirements requirements, List<String> responsibilities) {
        this.jobDetails = jobDetails;
        this.requirements = requirements;
        this.responsibilities = responsibilities;
    }

    // --- Record-Style Getters and Standard Setters ---

    public JobDetails jobDetails() { return this.jobDetails; }
    public void setJobDetails(JobDetails jobDetails) { this.jobDetails = jobDetails; }

    public JobRequirements requirements() { return this.requirements; }
    public void setRequirements(JobRequirements requirements) { this.requirements = requirements; }

    public List<String> responsibilities() { return this.responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }


    // ==========================================
    // NESTED CLASSES 
    // ==========================================

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JobDetails {
        private String title;
        private String company;
        private String location;
        private String employmentType;

        public JobDetails() {}

        public JobDetails(String title, String company, String location, String employmentType) {
            this.title = title;
            this.company = company;
            this.location = location;
            this.employmentType = employmentType;
        }

        public String title() { return this.title; }
        public void setTitle(String title) { this.title = title; }

        public String company() { return this.company; }
        public void setCompany(String company) { this.company = company; }

        public String location() { return this.location; }
        public void setLocation(String location) { this.location = location; }

        public String employmentType() { return this.employmentType; }
        public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JobRequirements {
        private SkillSet mandatorySkills;
        private SkillSet niceToHaveSkills;
        private int minimumExperienceYears;
        private String minimumEducationLevel;

        public JobRequirements() {}

        public JobRequirements(SkillSet mandatorySkills, SkillSet niceToHaveSkills, int minimumExperienceYears, String minimumEducationLevel) {
            this.mandatorySkills = mandatorySkills;
            this.niceToHaveSkills = niceToHaveSkills;
            this.minimumExperienceYears = minimumExperienceYears;
            this.minimumEducationLevel = minimumEducationLevel;
        }

        public SkillSet mandatorySkills() { return this.mandatorySkills; }
        public void setMandatorySkills(SkillSet mandatorySkills) { this.mandatorySkills = mandatorySkills; }

        public SkillSet niceToHaveSkills() { return this.niceToHaveSkills; }
        public void setNiceToHaveSkills(SkillSet niceToHaveSkills) { this.niceToHaveSkills = niceToHaveSkills; }

        public int minimumExperienceYears() { return this.minimumExperienceYears; }
        public void setMinimumExperienceYears(int minimumExperienceYears) { this.minimumExperienceYears = minimumExperienceYears; }

        public String minimumEducationLevel() { return this.minimumEducationLevel; }
        public void setMinimumEducationLevel(String minimumEducationLevel) { this.minimumEducationLevel = minimumEducationLevel; }
    }

    /**
     * Notice how we only have to write this once, but we use it 
     * for both mandatorySkills AND niceToHaveSkills in the requirements!
     */
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkillSet {
        private List<String> programmingLanguages;
        private List<String> frameworksAndTools;
        private List<String> softSkills;

        public SkillSet() {}

        public SkillSet(List<String> programmingLanguages, List<String> frameworksAndTools, List<String> softSkills) {
            this.programmingLanguages = programmingLanguages;
            this.frameworksAndTools = frameworksAndTools;
            this.softSkills = softSkills;
        }

        public List<String> programmingLanguages() { return this.programmingLanguages; }
        public void setProgrammingLanguages(List<String> programmingLanguages) { this.programmingLanguages = programmingLanguages; }

        public List<String> frameworksAndTools() { return this.frameworksAndTools; }
        public void setFrameworksAndTools(List<String> frameworksAndTools) { this.frameworksAndTools = frameworksAndTools; }

        public List<String> softSkills() { return this.softSkills; }
        public void setSoftSkills(List<String> softSkills) { this.softSkills = softSkills; }
    }
}