package com.bld.ats.scoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;
import com.bld.ats.model.Job.SkillSet;

/**
 * Keyword matching strategy: builds a single text corpus from all candidate
 * data (summary, skills, experience, projects) and searches for each job
 * requirement keyword within it. A keyword matches if it appears as a
 * substring of the corpus.
 *
 * Unlike WeightedPriorityStrategy, this strategy searches the full text of the
 * candidate's experience descriptions and project details, not just skill lists.
 */
public class KeywordMatchStrategy implements ScoringStrategy {

    @Override
    public double calculateScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return 0;
        }

        // Collect all requirements from both mandatory and nice-to-have categories
        Set<String> targetKeywords = extractJobKeywords(job);
        if (targetKeywords.isEmpty()) {
            return 100;
        }

        // Flatten all candidate text into a single lowercase corpus
        String candidateCorpus = buildCandidateCorpus(candidate).toLowerCase();

        // Count how many keywords appear in the corpus
        int matches = 0;
        for (String keyword : targetKeywords) {
            // Check both space-padded (exact word) and bare (substring) forms
            // to avoid false matches like "C" inside "React" while still catching "C++"
            if (candidateCorpus.contains(" " + keyword.toLowerCase() + " ") ||
                candidateCorpus.contains(keyword.toLowerCase())) {
                matches++;
            }
        }

        double score = (double) matches / targetKeywords.size();
        return Math.round(score * 100);
    }

    @Override
    public DetailedScore calculateDetailedScore(Candidate candidate, Job job) {
        if (candidate == null || job == null || job.requirements() == null) {
            return new DetailedScore(0, 0, 0, new ArrayList<>(), new ArrayList<>(), candidate);
        }

        String candidateCorpus = buildCandidateCorpus(candidate).toLowerCase();

        // Separate mandatory and nice-to-have for individual score reporting
        Set<String> mandatoryKeywords = new HashSet<>();
        addSkillsToSet(mandatoryKeywords, job.requirements().mandatorySkills());

        Set<String> niceToHaveKeywords = new HashSet<>();
        addSkillsToSet(niceToHaveKeywords, job.requirements().niceToHaveSkills());

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        int mandatoryMatches = analyzeCorpusMatch(mandatoryKeywords, candidateCorpus, matchedSkills, missingSkills);
        int niceToHaveMatches = analyzeCorpusMatch(niceToHaveKeywords, candidateCorpus, matchedSkills, missingSkills);

        double mandatoryScore = mandatoryKeywords.isEmpty() ? 1.0 : (double) mandatoryMatches / mandatoryKeywords.size();
        double niceToHaveScore = niceToHaveKeywords.isEmpty() ? 1.0 : (double) niceToHaveMatches / niceToHaveKeywords.size();

        // Final score = total matches / total requirements (unweighted)
        int totalTargetSize = mandatoryKeywords.size() + niceToHaveKeywords.size();
        int totalMatches = mandatoryMatches + niceToHaveMatches;

        double finalScore = totalTargetSize == 0 ? 1.0 : (double) totalMatches / totalTargetSize;

        return new DetailedScore(
            (int) Math.round(finalScore * 100),
            (int) Math.round(mandatoryScore * 100),
            (int) Math.round(niceToHaveScore * 100),
            matchedSkills,
            missingSkills,
            candidate
        );
    }

    /** Searches the corpus for each keyword, populating matched and missing lists. */
    private int analyzeCorpusMatch(Set<String> keywords, String corpus, List<String> matched, List<String> missing) {
        int matchCount = 0;
        for (String keyword : keywords) {
            String lowerKeyword = keyword.toLowerCase();
            // Check space-padded version first (exact word), then bare substring
            if (corpus.contains(" " + lowerKeyword + " ") || corpus.contains(lowerKeyword)) {
                matchCount++;
                matched.add(keyword); // Keep original casing for UI display
            } else {
                missing.add(keyword);
            }
        }
        return matchCount;
    }

    /** Collects all mandatory + nice-to-have skills from the job into a flat set. */
    private Set<String> extractJobKeywords(Job job) {
        Set<String> keywords = new HashSet<>();
        addSkillsToSet(keywords, job.requirements().mandatorySkills());
        addSkillsToSet(keywords, job.requirements().niceToHaveSkills());
        return keywords;
    }

    /** Flattens a SkillSet (programmingLanguages, frameworksAndTools, softSkills) into a set. */
    private void addSkillsToSet(Set<String> set, SkillSet skillSet) {
        if (skillSet == null) return;
        if (skillSet.programmingLanguages() != null) set.addAll(skillSet.programmingLanguages());
        if (skillSet.frameworksAndTools() != null) set.addAll(skillSet.frameworksAndTools());
        if (skillSet.softSkills() != null) set.addAll(skillSet.softSkills());
    }

    /**
     * Concatenates all candidate data (summary, skills, experience descriptions,
     * project descriptions) into a single searchable string for keyword matching.
     */
    private String buildCandidateCorpus(Candidate candidate) {
        StringBuilder corpus = new StringBuilder();

        if (candidate.summary() != null) {
            corpus.append(candidate.summary()).append(" ");
        }

        if (candidate.categorizedSkills() != null) {
            for (List<String> skills : candidate.categorizedSkills().values()) {
                if (skills != null) {
                    corpus.append(String.join(" ", skills)).append(" ");
                }
            }
        }

        if (candidate.experience() != null) {
            for (Candidate.Experience exp : candidate.experience()) {
                if (exp.description() != null) corpus.append(exp.description()).append(" ");
                if (exp.highlights() != null) corpus.append(String.join(" ", exp.highlights())).append(" ");
            }
        }

        if (candidate.projects() != null) {
            for (Candidate.Project proj : candidate.projects()) {
                if (proj.description() != null) corpus.append(proj.description()).append(" ");
                if (proj.tags() != null) corpus.append(String.join(" ", proj.tags())).append(" ");
            }
        }

        return corpus.toString();
    }
}
