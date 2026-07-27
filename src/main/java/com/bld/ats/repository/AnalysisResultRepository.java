package com.bld.ats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bld.ats.model.AnalysisResult;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findAllByOrderByCreatedAtDesc();
}
