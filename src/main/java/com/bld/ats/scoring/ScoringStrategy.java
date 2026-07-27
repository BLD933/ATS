package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;

/** Pluggable strategy for scoring a candidate against a job description. */
public interface ScoringStrategy {
    /** Returns a 0-100 match percentage. */
    double calculateScore(Candidate candidate, Job job);
    /** Returns a detailed breakdown with matched/missing skills for the frontend. */
    DetailedScore calculateDetailedScore(Candidate candidate, Job job);
}