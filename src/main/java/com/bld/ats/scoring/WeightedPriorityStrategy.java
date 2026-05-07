package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;
import com.bld.ats.model.Job.SkillSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeightedPriorityStrategy implements ScoringStrategy {

    private static final double MANDATORY_WEIGHT = 0.70;
    private static final double NICE_TO_HAVE_WEIGHT = 0.30;

    @Override
    public double calculateScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return 0;
        }

        // 1. Flatten all candidate skills into a single searchable pool
        Set<String> candidateSkillPool = extractCandidateSkills(candidate);

        // 2. Calculate percentages for both requirement categories
        double mandatoryScore = calculateCategoryMatch(
                job.requirements().mandatorySkills(), candidateSkillPool);
                
        double niceToHaveScore = calculateCategoryMatch(
                job.requirements().niceToHaveSkills(), candidateSkillPool);

        // 3. Apply weights and calculate final score (out of 100)
        double finalScore = (mandatoryScore * MANDATORY_WEIGHT) + (niceToHaveScore * NICE_TO_HAVE_WEIGHT);
        
        return  Math.round(finalScore * 100);
    }

    /**
     * Digs through the candidate's dynamic map and project tags to build a master list of skills.
     */
    private Set<String> extractCandidateSkills(Candidate candidate) {
        Set<String> pool = new HashSet<>();

        // Add all categorized skills
        if (candidate.categorizedSkills() != null) {
            for (List<String> skillList : candidate.categorizedSkills().values()) {
                if (skillList != null) {
                    for (String skill : skillList) {
                        pool.add(skill.toLowerCase().trim());
                    }
                }
            }
        }

        // Add project tags as a backup (e.g., if they used "Docker" in a project but forgot to list it in skills)
        if (candidate.projects() != null) {
            for (Candidate.Project project : candidate.projects()) {
                if (project.tags() != null) {
                    for (String tag : project.tags()) {
                        pool.add(tag.toLowerCase().trim());
                    }
                }
            }
        }

        return pool;
    }

    /**
     * Compares the Job's rigid SkillSet against the Candidate's flattened skill pool.
     */
    private double calculateCategoryMatch(SkillSet requiredSkills, Set<String> candidatePool) {
        if (requiredSkills == null) return 0.0;

        // Combine all required skills from this specific Job SkillSet
        List<String> allRequired = new ArrayList<>();
        if (requiredSkills.programmingLanguages() != null) allRequired.addAll(requiredSkills.programmingLanguages());
        if (requiredSkills.frameworksAndTools() != null) allRequired.addAll(requiredSkills.frameworksAndTools());
        if (requiredSkills.softSkills() != null) allRequired.addAll(requiredSkills.softSkills());

        if (allRequired.isEmpty()) return 1.0; // If they don't require anything, it's an automatic 100% match

        int matchCount = 0;

        for (String requirement : allRequired) {
            if (isSkillPresent(requirement, candidatePool)) {
                matchCount++;
            }
        }

        return (double) matchCount / allRequired.size();
    }

    /**
     * Helper method to do "fuzzy" string matching.
     */
    private boolean isSkillPresent(String requirement, Set<String> candidatePool) {
        String reqLower = requirement.toLowerCase().trim();

        for (String candidateSkill : candidatePool) {
            // Check for exact match OR if one string is inside the other
            // Example: requirement="React", candidateSkill="React.js" -> Match!
            if (candidateSkill.equals(reqLower) || 
                candidateSkill.contains(reqLower) || 
                reqLower.contains(candidateSkill)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public 
    DetailedScore calculateDetailedScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return new DetailedScore(0, 0, 0, new ArrayList<>(), new ArrayList<>(), null);
        }

        Set<String> candidateSkillPool = extractCandidateSkills(candidate);

        // Track exactly what they have and what they are missing
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        // Analyze Mandatory Skills
        double mandatoryScore = analyzeCategory(
                job.requirements().mandatorySkills(), candidateSkillPool, matchedSkills, missingSkills);
                
        // Analyze Nice-To-Have Skills
        double niceToHaveScore = analyzeCategory(
                job.requirements().niceToHaveSkills(), candidateSkillPool, matchedSkills, missingSkills);

        // Math
        double finalScore = (mandatoryScore * MANDATORY_WEIGHT) + (niceToHaveScore * NICE_TO_HAVE_WEIGHT);
        int finalScoreInt = (int) Math.round(finalScore * 100);

        return new DetailedScore(
                finalScoreInt, 
                (int) Math.round(mandatoryScore * 100), 
                (int) Math.round(niceToHaveScore * 100), 
                matchedSkills, 
                missingSkills,
                candidate
        );
    }
    /**
     * Helper method to analyze a specific category and populate our lists.
     */
    private double analyzeCategory(SkillSet requiredSkills, 
                                   Set<String> candidatePool, 
                                   List<String> matchedSkills, 
                                   List<String> missingSkills) {
        if (requiredSkills == null) return 0.0;

        List<String> allRequired = new ArrayList<>();
        if (requiredSkills.programmingLanguages() != null) allRequired.addAll(requiredSkills.programmingLanguages());
        if (requiredSkills.frameworksAndTools() != null) allRequired.addAll(requiredSkills.frameworksAndTools());
        if (requiredSkills.softSkills() != null) allRequired.addAll(requiredSkills.softSkills());

        if (allRequired.isEmpty()) return 1.0;

        int matchCount = 0;

        for (String requirement : allRequired) {
            if (isSkillPresent(requirement, candidatePool)) {
                matchCount++;
                matchedSkills.add(requirement);
            } else {
                missingSkills.add(requirement);
            }
        }

        return (double) matchCount / allRequired.size();
    }
}
