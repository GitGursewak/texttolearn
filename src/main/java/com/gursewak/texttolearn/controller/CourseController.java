package com.gursewak.texttolearn.controller;

import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.service.CourseService;
import com.gursewak.texttolearn.service.CourseGenerationPublisher;
import com.gursewak.texttolearn.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseGenerationPublisher publisher;

    @Autowired
    private SseService sseService;

    @GetMapping
    public List<Course> getAllCourses(Authentication authentication) {
        String userId = authentication.getName();
        return courseService.getAllCourses(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            return ResponseEntity.ok(courseService.updateCourse(id, course));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }

    @GetMapping("/difficulty/{difficulty}")
    public List<Course> getCoursesByDifficulty(@PathVariable String difficulty) {
        return courseService.getCoursesByDifficulty(difficulty);
    }

    // Async course generation: save skeleton -> publish to Pub/Sub -> return 202
    @PostMapping("/generate")
    public ResponseEntity<Course> generateCourse(
            @RequestParam String topic,
            @RequestParam(defaultValue = "beginner") String difficulty,
            Authentication authentication) {
        try {
            String userId = authentication.getName();

            // 1. Save an empty "PENDING" course instantly
            Course pendingCourse = courseService.createPendingCourse(topic, difficulty, userId);

            // 2. Publish the generation task to Pub/Sub
            publisher.publishCourseGeneration(pendingCourse.getId(), topic, difficulty, userId);

            // 3. Return 202 Accepted with the skeleton course (has the ID the frontend needs)
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingCourse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // SSE endpoint: frontend subscribes to get real-time notification when generation completes
    @GetMapping(value = "/{courseId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamCourseStatus(@PathVariable Long courseId) {
        return sseService.subscribeToCourse(courseId);
    }
}