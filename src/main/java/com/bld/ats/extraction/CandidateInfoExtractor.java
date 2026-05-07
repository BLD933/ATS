package com.bld.ats.extraction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.bld.ats.model.Candidate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CandidateInfoExtractor implements InfoExtractor<Candidate> {

    // You get this from the AI provider (e.g., Google AI Studio)

    

    @Override
    public Candidate extractInfosWithAI(String rawResumeText) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            ObjectNode requestBody = objectMapper.createObjectNode();

            ArrayNode messages = objectMapper.createArrayNode(); // ArrayNode

            ObjectNode systemMsg = objectMapper.createObjectNode();

            systemMsg.put("role", "system");
            systemMsg.put("content", "You are an ATS system. Extract all the information from the following resume text, in a structured way, the response should be ONLY in json format, specificaly in this EXACT format: {\"personalInfo\":{\"fullName\":\"String\",\"email\":\"String or null\",\"phone\":\"String or null\",\"location\":\"String or null\",\"webLinks\":\"Object (Key: String, Value: String) or null\"},\"summary\":\"String\",\"categorizedSkills\":\"Object (Key: String category name, Value: Array of Strings) or null\",\"experience\":[{\"role\":\"String or null\",\"organization\":\"String or null\",\"startDate\":\"String or null\",\"endDate\":\"String or null\",\"description\":\"String\",\"highlights\":[\"String\"]}],\"education\":[{\"degreeOrCertificate\":\"String or null\",\"institution\":\"String\",\"dateRange\":\"String or null\",\"details\":\"Object (Key: String detail name, Value: String) or null\"}],\"projects\":[{\"name\":\"String\",\"description\":\"String\",\"tags\":[\"String\"]}],\"additionalInfo\":\"Object (Key: String category name, Value: Array of Strings) or null\"}. For any field marked as 'Object', you are free to generate the key names based on the candidate's specific profession, but the values MUST strictly follow the specified data type (String or Array of Strings).");

            messages.add(systemMsg);

            ObjectNode exampleUserMsg = objectMapper.createObjectNode();

            exampleUserMsg.put("role", "user");
            exampleUserMsg.put("content","Contact\nTéléphone\n+212607039162\nEmail\nbouloud94@gmail.com\nAddresse\ncasablanca, oulfa\nExpertise\nCybersécurité\nRéseaux\nC/C++\nPython (pwntools/angr)\nLinux (Fedora/Hyprland)\nGeomatics Engineering\nLaTeX\nBash Scripting\nCréation de contenu technique\nAnalyse stratégique\npython\nLanguage\nAnglais\nArabe\nFrançais\nHobbies\nÉchecs\nCTFs (des challenges en\ncybersecurite)\nProgramming\nBOULOUD MOHAMMED\nStagiaire en Développement\nÉtudiant de 1ère année du cycle d'\''ingénieur en Géomatique et Systèmes d'\''Information (SIG) à\nI'\''EHTP, très motivé, s'\''appuyant sur de solides bases en Mathématiques, Physique et\nInformatique (MIP). Rigoureux, autonome et naturellement curieux, avec une expérience\navérée dans la direction de projets d'\''automatisation et de gestion de bases de données.\nPassionné par la programmation bas niveau, la rétro-ingénierie et l'\''architecture système.\nDésireux d'\''apporter un esprit analytique aiguisé et un solide bagage technique full-stack à\nune équipe de développement dynamique.\nEducation\nÉcole Hassania des Travaux Publics (ЕНТР)\nÉlève ingénieur en 1ère année - SIG\nCours spécialisés en algorithmique avancée, programmation système et géomatique.\nFaculté des Sciences Ain Chock\nDEUG (Bac+2) - Informatique (MIP) | 09/2023 - 09/2025\nÉtudes intensives en Mathématiques, Informatique et Physique.\nFormation axée sur la compréhension approfondie des systèmes informatiques, des algorithmes\net de la programmation logicielle.\nCompétences Techniques\nLangages de Programmation: C/C++, Python, JavaScript, HTML/CSS.\nBas Niveau & Cybersécurité: Rétro-ingénierie, Exploitation de binaires, Shellcode, Gestion de la\nmémoire, GDB, IDA Pro.\nBases de Données & Développement Web: SQL, NoSQL, React, Vue.js, APIs REST.\nSystèmes & Outils: Système d'\''exploitation Linux (Avancé: Fedora, Gestionnaire de fenêtres\nHyprland), Git, Office 365. Outils de Géomatique & Ingénierie: QGIS, Photogrammétrie,\nGéodésie mathématique et physique.\nProjets Techniques & Réalisations\nDéveloppement de jeu en C++:\n이\nDéveloppement d'\''un jeu de plateau Kõnane entièrement fonctionnel à partir de\nzéro en utilisant le C++ avec les bibliothèques ncurses et SDL, améliorant ainsi les\ncompétences en programmation et en gestion de projet.\nCollaboration avec des pairs pour optimiser les mécanismes de jeu, améliorant\nl'\''engagement des utilisateurs et l'\''expérience de jeu globale.\nCybersécurité (CTF):\n• Analyse et exploitation de binaires dans le cadre de défis Capture The Flag (CTF),\nen appliquant des techniques avancées de rétro-ingénierie pour identifier les\nvulnérabilités.\n이 Rédaction de rapports détaillés sur les découvertes, démontrant de solides\ncompétences analytiques nécessaires à l'\''analyse de données et à la sensibilisation\nà la cybersécurité.\n• Géomatique & Analyse SIG:\n이 Réalisation d'\''un mini-projet appliquant les concepts de géodésie physique et\nmathématique et de photogrammétrie à l'\''aide de QGIS, démontrant des\ncompétences en analyse spatiale.\nAutomatisation de bases de données:\n• Direction de projets axés sur l'\''automatisation des flux de travail et l'\''intégration\nde bases de données sécurisées, faisant preuve de leadership dans les pratiques\n이\nde développement logiciel.\nUtilisation de Python pour le développement backend, en adéquation avec\nexigences des stages en développement Python.");
            
            messages.add(exampleUserMsg);

            ObjectNode assistantMsg = objectMapper.createObjectNode();

            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", "{\"personalInfo\":{\"fullName\":\"BOULOUD MOHAMMED\",\"email\":\"bouloud94@gmail.com\",\"phone\":\"+212607039162\",\"location\":\"Casablanca, Oulfa\",\"webLinks\":null},\"summary\":\"Étudiant de 1ère année du cycle d'ingénieur en Géomatique et Systèmes d'Information (SIG) à l'EHTP, très motivé, s'appuyant sur de solides bases en Mathématiques, Physique et Informatique (MIP). Rigoureux, autonome et naturellement curieux, avec une expérience avérée dans la direction de projets d'automatisation et de gestion de bases de données. Passionné par la programmation bas niveau, la rétro-ingénierie et l'architecture système. Désireux d'apporter un esprit analytique aiguisé et un solide bagage technique full-stack à une équipe de développement dynamique.\",\"categorizedSkills\":{\"Langages de Programmation\":[\"C/C++\",\"Python\",\"JavaScript\",\"HTML/CSS\",\"Bash Scripting\",\"LaTeX\"],\"Bas Niveau & Cybersécurité\":[\"Cybersécurité\",\"Rétro-ingénierie\",\"Exploitation de binaires\",\"Shellcode\",\"Gestion de la mémoire\",\"GDB\",\"IDA Pro\",\"pwntools/angr\",\"Réseaux\"],\"Bases de Données & Développement Web\":[\"SQL\",\"NoSQL\",\"React\",\"Vue.js\",\"APIs REST\"],\"Systèmes & Outils\":[\"Linux (Fedora/Hyprland)\",\"Git\",\"Office 365\"],\"Géomatique & Ingénierie\":[\"Geomatics Engineering\",\"QGIS\",\"Photogrammétrie\",\"Géodésie mathématique et physique\"],\"Soft Skills\":[\"Création de contenu technique\",\"Analyse stratégique\",\"Autonomie\",\"Rigueur\",\"Leadership\"]},\"experience\":[{\"role\":\"Stagiaire en Développement\",\"organization\":null,\"startDate\":null,\"endDate\":null,\"description\":\"Direction de projets axés sur l'automatisation des flux de travail et l'intégration de bases de données sécurisées. Utilisation de Python pour le développement backend, en adéquation avec les exigences des stages en développement Python.\",\"highlights\":[\"Automatisation des flux de travail\",\"Intégration de bases de données sécurisées\",\"Développement backend avec Python\"]}],\"education\":[{\"degreeOrCertificate\":\"Élève ingénieur en 1ère année - SIG\",\"institution\":\"École Hassania des Travaux Publics (EHTP)\",\"dateRange\":null,\"details\":{\"Cours Spécialisés\":\"Algorithmique avancée, programmation système et géomatique.\"}},{\"degreeOrCertificate\":\"DEUG (Bac+2) - Informatique (MIP)\",\"institution\":\"Faculté des Sciences Ain Chock\",\"dateRange\":\"09/2023 - 09/2025\",\"details\":{\"Focus\":\"Études intensives en Mathématiques, Informatique et Physique. Compréhension approfondie des systèmes informatiques, des algorithmes et de la programmation logicielle.\"}}],\"projects\":[{\"name\":\"Développement de jeu en C++\",\"description\":\"Développement d'un jeu de plateau Kōnane entièrement fonctionnel à partir de zéro, améliorant les compétences en programmation et en gestion de projet.\",\"tags\":[\"C++\",\"ncurses\",\"SDL\",\"Optimisation\"]},{\"name\":\"Cybersécurité (CTF)\",\"description\":\"Analyse et exploitation de binaires dans le cadre de défis Capture The Flag (CTF). Rédaction de rapports détaillés sur les découvertes.\",\"tags\":[\"Rétro-ingénierie\",\"Vulnérabilités\",\"Analyse de données\"]},{\"name\":\"Géomatique & Analyse SIG\",\"description\":\"Réalisation d'un mini-projet appliquant les concepts de géodésie physique et mathématique et de photogrammétrie à l'aide de QGIS.\",\"tags\":[\"QGIS\",\"Photogrammétrie\",\"Analyse spatiale\"]}],\"additionalInfo\":{\"Languages\":[\"Anglais\",\"Arabe\",\"Français\"],\"Hobbies\":[\"Échecs\",\"CTFs (des challenges en cybersecurite)\",\"Programming\"]}}"
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

            String parsedInfo = objectMapper.readTree(response.body()).path("choices").get(0).path("message").path("content").asText(); // Get the content of the AI's response
            System.out.println(parsedInfo);

            Candidate candidate = objectMapper.readValue(parsedInfo, Candidate.class);
            // The response will contain the JSON array of skills the AI found!
            return candidate;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}