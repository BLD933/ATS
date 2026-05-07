package com.bld.ats.extraction;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.bld.ats.model.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JobInfoExtractor implements InfoExtractor<Job> {

    // You get this from the AI provider (e.g., Google AI Studio)

    @Override
    public Job extractInfosWithAI(String rawResumeText) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            ObjectNode requestBody = objectMapper.createObjectNode();

            ArrayNode messages = objectMapper.createArrayNode(); // ArrayNode

            ObjectNode systemMsg = objectMapper.createObjectNode();

            systemMsg.put("role", "system");
            systemMsg.put("content", "Extract all the information from the following job description in a structured way, always respond ONLY in a json format in this EXACT format: {\"jobDetails\":{\"title\":\"String\",\"company\":\"String\",\"location\":\"String\",\"employmentType\":\"String (e.g., Full-time, Internship, Contract)\"},\"requirements\":{\"mandatorySkills\":{\"programmingLanguages\":[\"String\"],\"frameworksAndTools\":[\"String\"],\"softSkills\":[\"String\"]},\"niceToHaveSkills\":{\"programmingLanguages\":[\"String\"],\"frameworksAndTools\":[\"String\"],\"softSkills\":[\"String\"]},\"minimumExperienceYears\":\"Integer\",\"minimumEducationLevel\":\"String\"},\"responsibilities\":[\"String\"]}");

            messages.add(systemMsg);
            
            ObjectNode exampleUserMsg = objectMapper.createObjectNode();

            exampleUserMsg.put("role", "user");
            exampleUserMsg.put("content", "Job Posting: Software Engineering Intern (Backend & Security Focus)\n" + //
                                "Company: CipherTech Systems\n" + //
                                "Location: Casablanca, Morocco (Hybrid)\n" + //
                                "About Us:CipherTech Systems is a leading infrastructure and security firm. We build robust backend services and ensure our clients' data is safe from emerging threats. We are looking for an analytical engineering student to join our core backend team for a 4-month internship.\n" + //
                                "The Role:As a Software Engineering Intern, you will work closely with our backend architecture and security teams. You will help build out RESTful APIs, optimize database queries, and assist in security auditing of our internal systems. The ideal candidate has a hacker mindset, loves the terminal, and writes clean, object-oriented code.\n" + //
                                "Mandatory Technical Skills:Strong proficiency in Java and backend architecture, Solid understanding of Object-Oriented Programming (OOP) and Data Structures.\n" + //
                                "Experience with systems-level programming languages, specifically C or C++.\n" + //
                                "Comfortable working in a Linux environment (command line, bash scripting, system administration).\n" + //
                                "Familiarity with foundational cybersecurity concepts, reverse engineering, or participation in CTF (Capture The Flag) competitions.\n" + //
                                "Nice-to-Have Qualifications:\n" + //
                                "Scripting experience using Python.\n" + //
                                "Experience building containerized applications using Docker and orchestrating with Kubernetes.\n" + //
                                "Familiarity with algorithm optimization (e.g., Minimax, pathfinding).\n" + //
                                "Currently enrolled in an engineering cycle (Cycle Ingénieur) related to computer science, geomatics, or secure systems.\n" + //
                                "Responsibilities: Develop and maintain Java-based backend services, Perform binary analysis and assist in vulnerability patching, Write secure, efficient, and well-documented code.");
            messages.add(exampleUserMsg);

            ObjectNode assistantMsg = objectMapper.createObjectNode();

            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", "{\"jobDetails\":{\"title\":\"Software Engineering Intern\",\"company\":\"CipherTech Systems\",\"location\":\"Casablanca, Morocco (Hybrid)\",\"employmentType\":\"Internship\"},\"requirements\":{\"mandatorySkills\":{\"programmingLanguages\":[\"Java\",\"C\",\"C++\"],\"frameworksAndTools\":[\"Linux\",\"bash scripting\",\"system administration\",\"reverse engineering\",\"CTF (Capture The Flag)\",\"Data Structures\"],\"softSkills\":[\"analytical\",\"hacker mindset\"]},\"niceToHaveSkills\":{\"programmingLanguages\":[\"Python\"],\"frameworksAndTools\":[\"Docker\",\"Kubernetes\",\"algorithm optimization\",\"Minimax\",\"pathfinding\"],\"softSkills\":[]},\"minimumExperienceYears\":0,\"minimumEducationLevel\":\"Cycle Ingénieur (computer science, geomatics, or secure systems)\"},\"responsibilities\":[\"Develop and maintain Java-based backend services.\",\"Perform binary analysis and assist in vulnerability patching.\",\"Write secure, efficient, and well-documented code.\"]}"
            );

            messages.add(assistantMsg);

            ObjectNode userMsg = objectMapper.createObjectNode();

            userMsg.put("role", "user");
            userMsg.put("content", rawResumeText);

            messages.add(userMsg);
            requestBody.set("messages", messages);

            requestBody.put("model", "meta-llama/llama-4-scout-17b-16e-instruct");
            requestBody.put("temperature", 0);
            requestBody.put("max_completion_tokens", 1024);
            requestBody.put("top_p", 1);
            requestBody.put("stream", false);
            requestBody.putNull("stop");

            // ObjectNode compound_custom = objectMapper.createObjectNode();
            // ArrayNode enabled_tools = objectMapper.createArrayNode();
            // enabled_tools.add("web_search");
            // enabled_tools.add("code_interpreter");
            // enabled_tools.add("visit_website");
            // compound_custom.set("enabled_tools", enabled_tools);
            // requestBody.set("compound_custom", compound_custom);



            
            String jsonPayload = objectMapper.writeValueAsString(requestBody);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // 4. Send the request and wait for the AI's response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String parsedInfo = objectMapper.readTree(response.body()).path("choices").get(0).path("message").path("content").asText();
            System.out.println("Parsed info: " + parsedInfo);
            Job jobObj = objectMapper.readValue(parsedInfo, Job.class);
            // The response will contain the JSON array of skills the AI found!
            return jobObj;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}