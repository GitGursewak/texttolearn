package com.gursewak.texttolearn.service;

import com.gursewak.texttolearn.model.Lesson;
import com.gursewak.texttolearn.model.Module;
import com.gursewak.texttolearn.repository.LessonRepository;
import com.gursewak.texttolearn.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    // Get all lessons for a module
    public List<Lesson> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
    }

    // Get lesson by ID
    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    // Create lesson for a module
    public Lesson createLesson(Long moduleId, Lesson lesson) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + moduleId));

        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }

    // Update lesson
    public Lesson updateLesson(Long id, Lesson lessonDetails) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

        lesson.setTitle(lessonDetails.getTitle());
        lesson.setContent(lessonDetails.getContent());
        lesson.setOrderIndex(lessonDetails.getOrderIndex());
        lesson.setVideoUrl(lessonDetails.getVideoUrl());

        return lessonRepository.save(lesson);
    }

    // Delete lesson
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}