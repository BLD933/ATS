package com.bld.ats.scoring;


import java.util.List;
import com.bld.ats.model.Candidate;

public record DetailedScore(
    int finalScore,
    int mandatoryScore,
    int bonusScore,
    List<String> matchedSkills,
    List<String> missingSkills,
    Candidate candidate // Sending the parsed data back to the UI!
) {}
