package com.gursewak.texttolearn.controller;

import com.gursewak.texttolearn.model.Lesson;
import com.gursewak.texttolearn.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    // Get all lessons for a module
    // GET /api/lessons/module/1
    @GetMapping("/module/{moduleId}")
    public List<Lesson> getLessonsByModule(@PathVariable Long moduleId) {
        return lessonService.getLessonsByModuleId(moduleId);
    }

    // Get lesson by ID
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create lesson for a module
    // POST /api/lessons/module/1
    @PostMapping("/module/{moduleId}")
    public Lesson createLesson(@PathVariable Long moduleId, @RequestBody Lesson lesson) {
        return lessonService.createLesson(moduleId, lesson);
    }

    // Update lesson
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        try {
            return ResponseEntity.ok(lessonService.updateLesson(id, lesson));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete lesson
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok().build();
    }
}