package com.bld.ats.service;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import com.bld.ats.extraction.CandidateInfoExtractor;
import com.bld.ats.extraction.JobInfoExtractor;
import com.bld.ats.model.Candidate;
import com.bld.ats.model.Job;
import com.bld.ats.scoring.CVScoringEngine;
import com.bld.ats.scoring.DetailedScore;
import com.bld.ats.scoring.WeightedPriorityStrategy;


public class AtsProcessingService {
  private final CandidateInfoExtractor candidateExtractor;
  private final JobInfoExtractor jobExtractor;
  private final CVScoringEngine scoringEngine;

  public AtsProcessingService() {
      this.candidateExtractor = new CandidateInfoExtractor();
      this.jobExtractor = new JobInfoExtractor();
      
      // We inject our smart strategy here! 
      // If you ever want to change how the ATS scores, you ONLY change this one line.
      this.scoringEngine = new CVScoringEngine(new WeightedPriorityStrategy());
  }

  public DetailedScore evaluateCandidate(MultipartFile cvFile, String rawJobDescriptionText) {
      try (PDDocument document = Loader.loadPDF(cvFile.getInputStream().readAllBytes())) {
      PDFTextStripper stripper = new PDFTextStripper();
      String rawCvText = stripper.getText(document);
      

      // STEP 1: Extract the data using AI (The API Calls)
      System.out.println("Extracting Candidate Data...");
      Candidate candidate = candidateExtractor.extractInfosWithAI(rawCvText);
      
      System.out.println("Extracting Job Data...");
      Job job = jobExtractor.extractInfosWithAI(rawJobDescriptionText);

      // STEP 2: Safety Check (If the AI failed or API timed out)
      if (candidate == null || job == null) {
          throw new RuntimeException("AI Extraction Failed"); 
      }
      // STEP 3: CALCULATE THE SCORE HERE
      System.out.println("Calculating Match Score...");
      return scoringEngine.matchDetailedScore(candidate, job);


    }   catch (IOException e) {
      return null;
    }
  }
}
