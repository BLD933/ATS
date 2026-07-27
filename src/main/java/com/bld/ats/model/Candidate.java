package com.bld.ats.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A completely generalized Candidate model suitable for any profession.
 * Converted to standard classes with record-style accessors to prevent breaking existing code.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Candidate {

    private PersonalInfo personalInfo;
    private String summary;
    private Map<String, List<String>> categorizedSkills;
    private List<Experience> experience;
    private List<Education> education;
    private List<Project> projects;
    private Map<String, Object> additionalInfo;

    public Candidate() {}

    public Candidate(PersonalInfo personalInfo, String summary, Map<String, List<String>> categorizedSkills,
                     List<Experience> experience, List<Education> education, List<Project> projects,
                     Map<String, Object> additionalInfo) {
        this.personalInfo = personalInfo;
        this.summary = summary;
        this.categorizedSkills = categorizedSkills;
        this.experience = experience;
        this.education = education;
        this.projects = projects;
        this.additionalInfo = additionalInfo;
    }

    // --- Record-Style Getters and Standard Setters ---

    public PersonalInfo personalInfo() { return this.personalInfo; }
    public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }

    public String summary() { return this.summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Map<String, List<String>> categorizedSkills() { return this.categorizedSkills; }
    public void setCategorizedSkills(Map<String, List<String>> categorizedSkills) { this.categorizedSkills = categorizedSkills; }

    public List<Experience> experience() { return this.experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }

    public List<Education> education() { return this.education; }
    public void setEducation(List<Education> education) { this.education = education; }

    public List<Project> projects() { return this.projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    public Map<String, Object> additionalInfo() { return this.additionalInfo; }
    public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }


    // ==========================================
    // NESTED CLASSES 
    // ==========================================

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalInfo {
        private String fullName;
        private String email;
        private String phone;
        private String location;
        private Map<String, String> webLinks;

        public PersonalInfo() {}

        public PersonalInfo(String fullName, String email, String phone, String location, Map<String, String> webLinks) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.location = location;
            this.webLinks = webLinks;
        }

        public String fullName() { return this.fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String email() { return this.email; }
        public void setEmail(String email) { this.email = email; }

        public String phone() { return this.phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String location() { return this.location; }
        public void setLocation(String location) { this.location = location; }

        public Map<String, String> webLinks() { return this.webLinks; }
        public void setWebLinks(Map<String, String> webLinks) { this.webLinks = webLinks; }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Experience {
        private String role;
        private String organization;
        private String startDate;
        private String endDate;
        private String description;
        private List<String> highlights;

        public Experience() {}

        public Experience(String role, String organization, String startDate, String endDate, String description, List<String> highlights) {
            this.role = role;
            this.organization = organization;
            this.startDate = startDate;
            this.endDate = endDate;
            this.description = description;
            this.highlights = highlights;
        }

        public String role() { return this.role; }
        public void setRole(String role) { this.role = role; }

        public String organization() { return this.organization; }
        public void setOrganization(String organization) { this.organization = organization; }

        public String startDate() { return this.startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String endDate() { return this.endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public String description() { return this.description; }
        public void setDescription(String description) { this.description = description; }

        public List<String> highlights() { return this.highlights; }
        public void setHighlights(List<String> highlights) { this.highlights = highlights; }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Education {
        private String degreeOrCertificate;
        private String institution;
        private String dateRange;
        private Map<String, String> details;

        public Education() {}

        public Education(String degreeOrCertificate, String institution, String dateRange, Map<String, String> details) {
            this.degreeOrCertificate = degreeOrCertificate;
            this.institution = institution;
            this.dateRange = dateRange;
            this.details = details;
        }

        public String degreeOrCertificate() { return this.degreeOrCertificate; }
        public void setDegreeOrCertificate(String degreeOrCertificate) { this.degreeOrCertificate = degreeOrCertificate; }

        public String institution() { return this.institution; }
        public void setInstitution(String institution) { this.institution = institution; }

        public String dateRange() { return this.dateRange; }
        public void setDateRange(String dateRange) { this.dateRange = dateRange; }

        public Map<String, String> details() { return this.details; }
        public void setDetails(Map<String, String> details) { this.details = details; }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Project {
        private String name;
        private String description;
        private List<String> tags;

        public Project() {}

        public Project(String name, String description, List<String> tags) {
            this.name = name;
            this.description = description;
            this.tags = tags;
        }

        public String name() { return this.name; }
        public void setName(String name) { this.name = name; }

        public String description() { return this.description; }
        public void setDescription(String description) { this.description = description; }

        public List<String> tags() { return this.tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
}