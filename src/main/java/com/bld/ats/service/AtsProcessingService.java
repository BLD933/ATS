package com.bld.ats.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bld.ats.extraction.CandidateInfoExtractor;
import com.bld.ats.extraction.JobInfoExtractor;
import com.bld.ats.model.AnalysisResult;
import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;
import com.bld.ats.repository.AnalysisResultRepository;
import com.bld.ats.scoring.CVScoringEngine;
import com.bld.ats.scoring.DetailedScore;
import com.bld.ats.scoring.KeywordMatchStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Orchestrates the ATS pipeline: extract text from PDF, run AI extraction
 * on both the CV and job description, then score the match and persist.
 */
@Service
public class AtsProcessingService {
    private final CandidateInfoExtractor candidateExtractor;
    private final JobInfoExtractor jobExtractor;
    private final CVScoringEngine scoringEngine;
    private final AnalysisResultRepository analysisResultRepository;
    private final ObjectMapper objectMapper;

    public AtsProcessingService(AnalysisResultRepository analysisResultRepository) {
        this.candidateExtractor = new CandidateInfoExtractor();
        this.jobExtractor = new JobInfoExtractor();
        this.scoringEngine = new CVScoringEngine(new KeywordMatchStrategy());
        this.analysisResultRepository = analysisResultRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Full pipeline: PDF text extraction -> AI extraction -> scoring -> persist.
     * Returns null (and logs) if PDF parsing fails; throws on AI failure.
     */
    public DetailedScore evaluateCandidate(MultipartFile cvFile, String rawJobDescriptionText) {
        try (PDDocument document = Loader.loadPDF(cvFile.getInputStream().readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String rawCvText = stripper.getText(document);

            // Step 1: extract structured data via LLM
            Candidate candidate = candidateExtractor.extractInfosWithAI(rawCvText);
            Job job = jobExtractor.extractInfosWithAI(rawJobDescriptionText);

            // Step 2: fail fast if AI extraction returned null (API error)
            if (candidate == null || job == null) {
                throw new RuntimeException("AI Extraction Failed");
            }

            // Step 3: compute match score using the configured strategy
            DetailedScore score = scoringEngine.matchDetailedScore(candidate, job);

            // Step 4: persist the analysis result
            persistResult(score, candidate, job, rawCvText, rawJobDescriptionText);

            return score;

        } catch (IOException e) {
            return null;
        }
    }

    private void persistResult(DetailedScore score, Candidate candidate, Job job,
                                String rawCvText, String rawJobDescription) {
        try {
            AnalysisResult result = new AnalysisResult();
            result.setCreatedAt(LocalDateTime.now());
            result.setFinalScore(score.finalScore());
            result.setMandatoryScore(score.mandatoryScore());
            result.setBonusScore(score.bonusScore());
            result.setMatchedSkills(objectMapper.writeValueAsString(score.matchedSkills()));
            result.setMissingSkills(objectMapper.writeValueAsString(score.missingSkills()));
            result.setCandidateData(objectMapper.writeValueAsString(candidate));
            result.setJobData(objectMapper.writeValueAsString(job));
            result.setRawCvText(rawCvText);
            result.setRawJobDescription(rawJobDescription);
            analysisResultRepository.save(result);
        } catch (JsonProcessingException e) {
            // Log but don't fail the request — analysis result is still returned
            e.printStackTrace();
        }
    }
}
