package com.gursewak.texttolearn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    // Map courseId to SseEmitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribeToCourse(Long courseId) {
        // Timeout 15 minutes (in ms)
        SseEmitter emitter = new SseEmitter(15L * 60L * 1000L);
        emitters.put(courseId, emitter);

        emitter.onCompletion(() -> emitters.remove(courseId));
        emitter.onTimeout(() -> emitters.remove(courseId));
        emitter.onError((e) -> emitters.remove(courseId));

        try {
            // Send initial connection event
            emitter.send(SseEmitter.event().name("INIT").data("Connected for course " + courseId));
        } catch (IOException e) {
            emitters.remove(courseId);
        }

        return emitter;
    }

    public void notifyCourseStatus(Long courseId, String status) {
        SseEmitter emitter = emitters.get(courseId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(status).data("Course generation " + status.toLowerCase()));
                emitter.complete();
            } catch (IOException e) {
                emitters.remove(courseId);
            }
        }
    }
}
