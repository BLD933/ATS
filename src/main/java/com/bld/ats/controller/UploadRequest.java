package com.bld.ats.controller;

// This is the entire file! 
// It perfectly defines the JSON shape we expect from the frontend.
public record UploadRequest(String cvText, String jobText) {}
