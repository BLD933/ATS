package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;

/**
 * Scoring engine that delegates to a pluggable ScoringStrategy.
 * Uses the Strategy pattern: pass a different strategy to change scoring behavior
 * without modifying this class or the service layer.
 */
public class CVScoringEngine {

    private final ScoringStrategy strategy;

    /** Defaults to WeightedPriorityStrategy (mandatory 70% / nice-to-have 30%). */
    public CVScoringEngine() {
        this.strategy = new WeightedPriorityStrategy();
    }

    /** Inject any ScoringStrategy at construction time. */
    public CVScoringEngine(ScoringStrategy strategy) {
        this.strategy = strategy;
    }

    /** Returns a 0-100 match percentage between candidate and job. */
    public double match(Candidate candidate, Job job) {
        return strategy.calculateScore(candidate, job);
    }

    /** Returns a DetailedScore with matched/missing skill breakdowns for the UI. */
    public DetailedScore matchDetailedScore(Candidate candidate, Job job) {
        return strategy.calculateDetailedScore(candidate, job);
    }
}