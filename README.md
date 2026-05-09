# AI-Powered ATS Parsing Engine

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![AI/Groq](https://img.shields.io/badge/AI_Powered-Groq_LLM-8A2BE2?style=for-the-badge)

A full-stack, AI-integrated Applicant Tracking System (ATS) designed to eliminate the "black box" nature of traditional HR screening. This engine parses PDF resumes, leverages Large Language Models for semantic extraction, and applies a custom-weighted mathematical algorithm to generate detailed candidate gap analyses.

> **Visual Demo** > <img width="1041" height="857" alt="image" src="https://github.com/user-attachments/assets/92e476f5-28a5-42b4-a383-77383bbaa950" />

## Key Features

* ** LLM-Driven Data Extraction:** Bypasses fragile Regex parsing by utilizing the Groq API (Llama 3) to accurately structure unstructured CV data and job descriptions into precise JSON.
* ** Weighted Priority Scoring:** Employs a custom mathematical engine dividing job requirements into "Mandatory" (70% weight) and "Nice-to-Have" (30% weight) categories.
* ** Deep Gap Analysis:** Generates a comprehensive breakdown of explicitly matched skills and missing qualifications for recruiter review.
* ** Premium Glassmorphism UI:** A responsive, dark-theme frontend built with Vanilla HTML/CSS/JS, featuring state management, asynchronous multipart form uploads, and 60fps SVG stroke animations.

##  Architecture & Design Patterns

This backend was engineered with strict adherence to Object-Oriented Design and SOLID principles:

* **Strategy Pattern:** The core scoring logic is decoupled via the `ScoringStrategy` interface, allowing the system to hot-swap between weighted algorithms (`WeightedPriorityStrategy`) and strict keyword matching (`KeywordMatchStrategy`) without modifying the service layer.
* **Modern DTOs:** Utilizes Java 16+ `record` classes (e.g., `DetailedScore`, `Candidate`, `Job`) to ensure complete immutability and thread-safe JSON serialization.
* **Native HTTP Client:** Uses Java's native `HttpClient` for external API communication, keeping the footprint lightweight and avoiding bloated third-party wrappers.

## Tech Stack

**Backend:** Java 17+, Spring Boot, Apache PDFBox, Jackson (JSON processing)  
**Frontend:** HTML5, CSS3, Vanilla JavaScript (Fetch API, FormData)  
**AI Integration:** Groq API  
**Build Tool:** Gradle  

## Getting Started

### Prerequisites
* Java 17 or higher installed.
* A free API key from [Groq](https://console.groq.com/).

### Installation

1. **Clone the repository:**
   ```bash
   git clone git@github.com:BLD933/ATS.git
   cd ai-ats-engine

2. **Configure the AI API Key:**
   Open `src/main/resources/application.properties` and add your Groq API key:

3. **Run the Spring Boot Server:**
   ```bash
   gradle bootrun
   ```
4. **Run the Spring Boot Server:**
Simply double-click the `index.html` file in your browser, or serve it via a live server. Upload a PDF, paste a job description, and watch the engine work.
