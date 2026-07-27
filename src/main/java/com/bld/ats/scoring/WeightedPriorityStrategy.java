package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;
import com.bld.ats.model.Job.SkillSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Weighted scoring strategy: mandatory skills account for 70% of the final score,
 * nice-to-have skills for 30%. Uses fuzzy substring matching against the candidate's
 * categorized skills and project tags.
 */
public class WeightedPriorityStrategy implements ScoringStrategy {

    // Mandatory skills carry more weight than nice-to-have skills
    private static final double MANDATORY_WEIGHT = 0.70;
    private static final double NICE_TO_HAVE_WEIGHT = 0.30;

    @Override
    public double calculateScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return 0;
        }

        // Flatten all candidate skills into a single searchable pool
        Set<String> candidateSkillPool = extractCandidateSkills(candidate);

        // Score each category independently, then weight the results
        double mandatoryScore = calculateCategoryMatch(
                job.requirements().mandatorySkills(), candidateSkillPool);
        double niceToHaveScore = calculateCategoryMatch(
                job.requirements().niceToHaveSkills(), candidateSkillPool);

        double finalScore = (mandatoryScore * MANDATORY_WEIGHT) + (niceToHaveScore * NICE_TO_HAVE_WEIGHT);
        return Math.round(finalScore * 100);
    }

    /** Collects all skills from the candidate's categorized skills + project tags. */
    private Set<String> extractCandidateSkills(Candidate candidate) {
        Set<String> pool = new HashSet<>();

        if (candidate.categorizedSkills() != null) {
            for (List<String> skillList : candidate.categorizedSkills().values()) {
                if (skillList != null) {
                    for (String skill : skillList) {
                        pool.add(skill.toLowerCase().trim());
                    }
                }
            }
        }

        // Project tags serve as a fallback (candidates often list tools here)
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

    /** Returns the fraction of required skills found in the candidate pool (0.0 to 1.0). */
    private double calculateCategoryMatch(SkillSet requiredSkills, Set<String> candidatePool) {
        if (requiredSkills == null) return 0.0;

        // Flatten all three sub-lists into one list of requirements
        List<String> allRequired = new ArrayList<>();
        if (requiredSkills.programmingLanguages() != null) allRequired.addAll(requiredSkills.programmingLanguages());
        if (requiredSkills.frameworksAndTools() != null) allRequired.addAll(requiredSkills.frameworksAndTools());
        if (requiredSkills.softSkills() != null) allRequired.addAll(requiredSkills.softSkills());

        if (allRequired.isEmpty()) return 1.0;

        int matchCount = 0;
        for (String requirement : allRequired) {
            if (isSkillPresent(requirement, candidatePool)) {
                matchCount++;
            }
        }

        return (double) matchCount / allRequired.size();
    }

    /**
     * Fuzzy match: checks exact equality, substring containment, or reverse containment.
     * e.g. requirement="React" matches candidateSkill="React.js", and vice versa.
     */
    private boolean isSkillPresent(String requirement, Set<String> candidatePool) {
        String reqLower = requirement.toLowerCase().trim();
        for (String candidateSkill : candidatePool) {
            if (candidateSkill.equals(reqLower) ||
                candidateSkill.contains(reqLower) ||
                reqLower.contains(candidateSkill)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DetailedScore calculateDetailedScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return new DetailedScore(0, 0, 0, new ArrayList<>(), new ArrayList<>(), null);
        }

        Set<String> candidateSkillPool = extractCandidateSkills(candidate);

        // Track which skills matched and which are missing, for the frontend to display
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        double mandatoryScore = analyzeCategory(
                job.requirements().mandatorySkills(), candidateSkillPool, matchedSkills, missingSkills);
        double niceToHaveScore = analyzeCategory(
                job.requirements().niceToHaveSkills(), candidateSkillPool, matchedSkills, missingSkills);

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

    /** Like calculateCategoryMatch but also populates the matched/missing lists. */
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
