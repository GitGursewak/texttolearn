package com.gursewak.texttolearn.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseGenerationPublisher {

    @Autowired
    private PubSubTemplate pubSubTemplate;

    public void publishCourseGeneration(Long courseId, String topic, String difficulty, String userId) {
        // Build JSON manually for simplicity
        String payload = String.format("{\"courseId\":%d, \"topic\":\"%s\", \"difficulty\":\"%s\", \"userId\":\"%s\"}", 
            courseId, topic, difficulty, userId);
            
        pubSubTemplate.publish("course-generation-topic", payload);
    }
}
