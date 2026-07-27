package com.bld.ats.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private int finalScore;
    private int mandatoryScore;
    private int bonusScore;

    @Column(columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String candidateData;

    @Column(columnDefinition = "TEXT")
    private String jobData;

    @Column(columnDefinition = "TEXT")
    private String rawCvText;

    @Column(columnDefinition = "TEXT")
    private String rawJobDescription;

    public AnalysisResult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getFinalScore() { return finalScore; }
    public void setFinalScore(int finalScore) { this.finalScore = finalScore; }

    public int getMandatoryScore() { return mandatoryScore; }
    public void setMandatoryScore(int mandatoryScore) { this.mandatoryScore = mandatoryScore; }

    public int getBonusScore() { return bonusScore; }
    public void setBonusScore(int bonusScore) { this.bonusScore = bonusScore; }

    public String getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(String matchedSkills) { this.matchedSkills = matchedSkills; }

    public String getMissingSkills() { return missingSkills; }
    public void setMissingSkills(String missingSkills) { this.missingSkills = missingSkills; }

    public String getCandidateData() { return candidateData; }
    public void setCandidateData(String candidateData) { this.candidateData = candidateData; }

    public String getJobData() { return jobData; }
    public void setJobData(String jobData) { this.jobData = jobData; }

    public String getRawCvText() { return rawCvText; }
    public void setRawCvText(String rawCvText) { this.rawCvText = rawCvText; }

    public String getRawJobDescription() { return rawJobDescription; }
    public void setRawJobDescription(String rawJobDescription) { this.rawJobDescription = rawJobDescription; }
}
