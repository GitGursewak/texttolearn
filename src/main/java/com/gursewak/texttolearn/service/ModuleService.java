package com.gursewak.texttolearn.service;

import com.gursewak.texttolearn.model.Module;
import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.repository.ModuleRepository;
import com.gursewak.texttolearn.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Get all modules for a course
    public List<Module> getModulesByCourseId(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    // Get module by ID
    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    // Create module for a course
    public Module createModule(Long courseId, Module module) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        module.setCourse(course);
        return moduleRepository.save(module);
    }

    // Update module
    public Module updateModule(Long id, Module moduleDetails) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));

        module.setTitle(moduleDetails.getTitle());
        module.setDescription(moduleDetails.getDescription());
        module.setOrderIndex(moduleDetails.getOrderIndex());

        return moduleRepository.save(module);
    }

    // Delete module
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }
}