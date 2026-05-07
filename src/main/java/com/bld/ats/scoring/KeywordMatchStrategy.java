package com.bld.ats.scoring;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;
import com.bld.ats.model.Job.SkillSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeywordMatchStrategy implements ScoringStrategy {

    @Override
    public double calculateScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return 0;
        }

        // 1. Extract every single requirement from the Job
        Set<String> targetKeywords = extractJobKeywords(job);
        if (targetKeywords.isEmpty()) {
            return 100; // No requirements means instant match
        }

        // 2. Build a massive "Corpus" (a giant string) of everything the candidate wrote
        String candidateCorpus = buildCandidateCorpus(candidate).toLowerCase();

        // 3. Count how many keywords appear in the corpus
        int matches = 0;
        for (String keyword : targetKeywords) {
            // We pad with spaces to avoid accidental substring matches 
            // e.g., finding "C" inside "React"
            if (candidateCorpus.contains(" " + keyword.toLowerCase() + " ") || 
                candidateCorpus.contains(keyword.toLowerCase())) { 
                matches++;
            }
        }

        // 4. Calculate percentage
        double score = (double) matches / targetKeywords.size();
        return Math.round(score * 100);
    }

    @Override
    public DetailedScore calculateDetailedScore(Candidate candidate, Job job) {
        return null;
    }
    /**
     * Grabs all mandatory and nice-to-have skills from the job description.
     */
    private Set<String> extractJobKeywords(Job job) {
        Set<String> keywords = new HashSet<>();
        
        // Helper lambda/method to safely add skills
        addSkillsToSet(keywords, job.requirements().mandatorySkills());
        addSkillsToSet(keywords, job.requirements().niceToHaveSkills());
        
        return keywords;
    }

    private void addSkillsToSet(Set<String> set, SkillSet skillSet) {
        if (skillSet == null) return;
        if (skillSet.programmingLanguages() != null) set.addAll(skillSet.programmingLanguages());
        if (skillSet.frameworksAndTools() != null) set.addAll(skillSet.frameworksAndTools());
        if (skillSet.softSkills() != null) set.addAll(skillSet.softSkills());
    }

    /**
     * Smashes all candidate data into one massive, searchable string block.
     */
    private String buildCandidateCorpus(Candidate candidate) {
        StringBuilder corpus = new StringBuilder();

        if (candidate.summary() != null) {
            corpus.append(candidate.summary()).append(" ");
        }

        // Add Skills
        if (candidate.categorizedSkills() != null) {
            for (List<String> skills : candidate.categorizedSkills().values()) {
                if (skills != null) {
                    corpus.append(String.join(" ", skills)).append(" ");
                }
            }
        }

        // Add Experience Descriptions
        if (candidate.experience() != null) {
            for (Candidate.Experience exp : candidate.experience()) {
                if (exp.description() != null) corpus.append(exp.description()).append(" ");
                if (exp.highlights() != null) corpus.append(String.join(" ", exp.highlights())).append(" ");
            }
        }

        // Add Project Descriptions and Tags
        if (candidate.projects() != null) {
            for (Candidate.Project proj : candidate.projects()) {
                if (proj.description() != null) corpus.append(proj.description()).append(" ");
                if (proj.tags() != null) corpus.append(String.join(" ", proj.tags())).append(" ");
            }
        }

        return corpus.toString();
    }


}