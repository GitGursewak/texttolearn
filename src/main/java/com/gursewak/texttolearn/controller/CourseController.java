package com.gursewak.texttolearn.controller;

import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
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

    // Generate course using AI
    // POST /api/courses/generate?topic=Java Basics&difficulty=beginner
//    @PostMapping("/generate")
//    public ResponseEntity<Course> generateCourse(
//            @RequestParam String topic,
//            @RequestParam(defaultValue = "beginner") String difficulty) {
//        try {
//            Course course = courseService.generateCourse(topic, difficulty);
//            return ResponseEntity.ok(course);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
    @PostMapping("/generate")
    public ResponseEntity<Course> generateCourse(
            @RequestParam String topic,
            @RequestParam(defaultValue = "beginner") String difficulty) {
        try {
            Course course = courseService.generateCourse(topic, difficulty);
            return ResponseEntity.ok(course);
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // ADD THIS LINE TO SEE THE ERROR IN CONSOLE
            System.err.println("Groq Error Response: " + e.getResponseBodyAsString());
            return ResponseEntity.badRequest().build();
        }
    }
}