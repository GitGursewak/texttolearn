package com.gursewak.texttolearn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public YouTubeService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    // Search for a video related to the lesson
    public String searchVideo(String query) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("www.googleapis.com")
                            .path("/youtube/v3/search")
                            .queryParam("part", "snippet")
                            .queryParam("q", query)
                            .queryParam("type", "video")
                            .queryParam("maxResults", "1")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Extract video ID from response
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            if (items.isArray() && items.size() > 0) {
                String videoId = items.get(0).path("id").path("videoId").asText();
                return "https://www.youtube.com/watch?v=" + videoId;
            }

            return null;
        } catch (Exception e) {
            System.err.println("YouTube API error: " + e.getMessage());
            return null;
        }
    }
}