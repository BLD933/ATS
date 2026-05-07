package com.bld.ats.extraction;

public interface InfoExtractor<extractionType> {

    String API_KEY = System.getenv("GROQ_API_KEY"); 
    String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    public extractionType extractInfosWithAI(String prompt);
}