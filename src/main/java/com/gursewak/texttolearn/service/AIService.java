package com.gursewak.texttolearn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.model.Module;
import com.gursewak.texttolearn.model.Lesson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AIService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public Course generateCourse(String topic, String difficulty) {
        try {
            // Create prompt for AI
            String prompt = createPrompt(topic, difficulty);

            // Call OpenAI API
            String aiResponse = callOpenAI(prompt);

            // Parse response and create course
            return parseCourseFromAI(aiResponse, topic, difficulty);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate course: " + e.getMessage());
        }
    }

    private String createPrompt(String topic, String difficulty) {
        return String.format("""
            Create a comprehensive course structure for: "%s" at %s level.
            
            Return ONLY a JSON object with this exact structure:
            {
              "description": "brief course description",
              "modules": [
                {
                  "title": "module title",
                  "description": "module description",
                  "lessons": [
                    {
                      "title": "lesson title",
                      "content": "detailed lesson content in markdown format (at least 200 words)"
                    }
                  ]
                }
              ]
            }
            
            Requirements:
            - Create 3-5 modules
            - Each module should have 2-4 lessons
            - Lesson content should be educational and detailed
            - Use markdown formatting in content (headers, lists, code blocks)
            - Focus on practical, actionable information
            
            Return ONLY the JSON, no additional text.
            """, topic, difficulty);
    }

//    private String callOpenAI(String prompt) throws Exception {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", model);
//        requestBody.put("messages", new Object[]{
//                Map.of("role", "user", "content", prompt)
//        });
//        requestBody.put("temperature", 0.7);
//        requestBody.put("max_tokens", 3000);
//
//        String response = webClient.post()
//                .uri(apiUrl)
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        // Extract content from OpenAI response
//        JsonNode root = objectMapper.readTree(response);
//        return root.path("choices").get(0).path("message").path("content").asText();
//    }
private String callOpenAI(String prompt) throws Exception {
    Map<String, Object> requestBody = new HashMap<>();

    // 1. Ensure this is a valid Groq model name from your properties
    requestBody.put("model", model);

    requestBody.put("messages", new Object[]{
            Map.of("role", "user", "content", prompt)
    });

    requestBody.put("temperature", 0.7);

    // 2. IMPORTANT: Groq prefers max_completion_tokens
    requestBody.put("max_completion_tokens", 3000);

    // 3. Force JSON mode to prevent 400 errors when asking for JSON
    requestBody.put("response_format", Map.of("type", "json_object"));

    String response = webClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    JsonNode root = objectMapper.readTree(response);
    return root.path("choices").get(0).path("message").path("content").asText();
}
    private Course parseCourseFromAI(String aiResponse, String topic, String difficulty) throws Exception {
        // Remove mark down code blocks if present
        aiResponse = aiResponse.replaceAll("```json\\n?", "").replaceAll("```\\n?", "").trim();

        JsonNode courseData = objectMapper.readTree(aiResponse);

        // Create Course
        Course course = new Course();
        course.setTitle(topic);
        course.setDescription(courseData.path("description").asText());
        course.setDifficulty(difficulty);
        course.setModules(new ArrayList<>());

        // Create Modules
        JsonNode modulesNode = courseData.path("modules");
        for (int i = 0; i < modulesNode.size(); i++) {
            JsonNode moduleNode = modulesNode.get(i);

            Module module = new Module();
            module.setTitle(moduleNode.path("title").asText());
            module.setDescription(moduleNode.path("description").asText());
            module.setOrderIndex(i + 1);
            module.setCourse(course);
            module.setLessons(new ArrayList<>());

            // Create Lessons
            JsonNode lessonsNode = moduleNode.path("lessons");
            for (int j = 0; j < lessonsNode.size(); j++) {
                JsonNode lessonNode = lessonsNode.get(j);

                Lesson lesson = new Lesson();
                lesson.setTitle(lessonNode.path("title").asText());
                lesson.setContent(lessonNode.path("content").asText());
                lesson.setOrderIndex(j + 1);
                lesson.setModule(module);

                module.getLessons().add(lesson);
            }

            course.getModules().add(module);
        }

        return course;
    }
}