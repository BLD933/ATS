package com.bld.ats.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bld.ats.service.AtsProcessingService;
import com.bld.ats.scoring.DetailedScore;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/cv")
public class AtsController {


    private final AtsProcessingService atsService = new AtsProcessingService();

    @PostMapping("/analyze")
    public ResponseEntity<DetailedScore> analyzeCv(@RequestParam("cvFile") MultipartFile cvFile,
                                                   @RequestParam("jobDescription") String jobDescription) {
            try{// 3. You just use the instance! No "new" keyword, and no static methods.
                if (cvFile == null || jobDescription == null) {
                    return ResponseEntity.badRequest().build();
                }

                return ResponseEntity.ok(atsService.evaluateCandidate(cvFile, jobDescription));
            }catch (RuntimeException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }

        }

}

