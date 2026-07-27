package com.bld.ats.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bld.ats.model.AnalysisResult;
import com.bld.ats.repository.AnalysisResultRepository;
import com.bld.ats.scoring.DetailedScore;
import com.bld.ats.service.AtsProcessingService;

/** REST controller exposing the CV analysis endpoint. */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/cv")
public class AtsController {

    private final AtsProcessingService atsService;
    private final AnalysisResultRepository analysisResultRepository;

    public AtsController(AtsProcessingService atsService, AnalysisResultRepository analysisResultRepository) {
        this.atsService = atsService;
        this.analysisResultRepository = analysisResultRepository;
    }

    @GetMapping("/results")
    public ResponseEntity<List<AnalysisResult>> getResults() {
        return ResponseEntity.ok(analysisResultRepository.findAllByOrderByCreatedAtDesc());
    }

    /**
     * Accepts a PDF resume and a job description, returns a detailed match score
     * with skill gap analysis.
     */
    @PostMapping("/analyze")
    public ResponseEntity<DetailedScore> analyzeCv(@RequestParam("cvFile") MultipartFile cvFile,
                                                   @RequestParam("jobDescription") String jobDescription) {
        try {
            if (cvFile == null || jobDescription == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(atsService.evaluateCandidate(cvFile, jobDescription));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
