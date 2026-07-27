package com.bld.ats.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for AI-powered information extractors.
 * Provides shared Groq API configuration and Jackson ObjectMapper to subclasses.
 * The API key is read from the GROQ_API_KEY environment variable at runtime.
 */
public abstract class InfoExtractor<T> {

    protected final String API_KEY = System.getenv("GROQ_API_KEY");
    protected final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /** Submits raw text to the LLM and returns a structured object of type T. */
    public abstract T extractInfosWithAI(String prompt);
}