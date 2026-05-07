package com.gursewak.texttolearn.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CourseGenerationSubscriber implements DisposableBean {

    @Autowired
    private PubSubTemplate pubSubTemplate;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SseService sseService;

    private volatile boolean running = true;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        // Main subscriber — processes course generation
        pubSubTemplate.subscribe("course-generation-sub", (BasicAcknowledgeablePubsubMessage message) -> {
            if (!running) {
                message.nack();
                return;
            }

            Long courseId = null;
            try {
                PubsubMessage pubsubMessage = message.getPubsubMessage();
                String payload = pubsubMessage.getData().toStringUtf8();
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(payload);
                courseId = node.get("courseId").asLong();
                String topic = node.get("topic").asText();
                String difficulty = node.get("difficulty").asText();
                String userId = node.get("userId").asText();

                System.out.println("Processing course generation: " + courseId + " - " + topic);

                courseService.generateAndSaveCourseContent(courseId, topic, difficulty, userId);
                sseService.notifyCourseStatus(courseId, "COMPLETED");

                System.out.println("Course generation completed: " + courseId);
                message.ack();
            } catch (Exception e) {
                System.err.println("Course generation failed for courseId: " + courseId + ". NACKing for Pub/Sub retry.");
                e.printStackTrace();
                // NACK — Pub/Sub handles retry with exponential backoff
                message.nack();
            }
        });

        // Dead letter subscriber — marks course as permanently FAILED after all retries exhausted
        pubSubTemplate.subscribe("course-generation-dead-letter-sub", (BasicAcknowledgeablePubsubMessage message) -> {
            if (!running) {
                message.nack();
                return;
            }

            try {
                String payload = message.getPubsubMessage().getData().toStringUtf8();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(payload);
                Long courseId = node.get("courseId").asLong();

                System.err.println("Dead letter received for courseId: " + courseId + ". Marking as FAILED.");
                courseService.markCourseFailed(courseId);
                sseService.notifyCourseStatus(courseId, "FAILED");

                message.ack();
            } catch (Exception e) {
                System.err.println("Error processing dead letter message");
                e.printStackTrace();
                message.ack();
            }
        });
    }

    @Override
    public void destroy() {
        running = false;
    }
}
