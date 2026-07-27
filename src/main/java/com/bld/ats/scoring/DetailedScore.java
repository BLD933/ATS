package com.bld.ats.scoring;


import java.util.List;
import com.bld.ats.model.Candidate;

/**
 * Immutable DTO returned to the frontend.
 * Contains the overall score, per-category breakdowns, and the parsed candidate data.
 */
public record DetailedScore(
    int finalScore,
    int mandatoryScore,
    int bonusScore,
    List<String> matchedSkills,
    List<String> missingSkills,
    Candidate candidate
) {}
