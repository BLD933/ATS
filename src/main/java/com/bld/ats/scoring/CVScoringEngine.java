package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;

public class CVScoringEngine {
    
    private final ScoringStrategy strategy;

    // Default constructor uses the Weighted Priority
    public CVScoringEngine() {
        this.strategy = new WeightedPriorityStrategy();
    }

    // Allows you to inject a different strategy later (e.g., StrictKeywordStrategy)
    public CVScoringEngine(ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    public double match(Candidate candidate, Job job) {
        return strategy.calculateScore(candidate, job);
    }

    public DetailedScore matchDetailedScore(Candidate candidate, Job job) {
        return strategy.calculateDetailedScore(candidate, job);
    }
}