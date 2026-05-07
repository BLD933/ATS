package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;

public interface ScoringStrategy {
    // Takes the candidate's profile and the target job description, returns a score 0-100
    double calculateScore(Candidate candidate, Job job);
    DetailedScore calculateDetailedScore(Candidate candidate, Job job);
}