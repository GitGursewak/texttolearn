package com.gursewak.texttolearn.controller;

import com.gursewak.texttolearn.model.Module;
import com.gursewak.texttolearn.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    // Get all modules for a course
    // GET /api/modules/course/1
    @GetMapping("/course/{courseId}")
    public List<Module> getModulesByCourse(@PathVariable Long courseId) {
        return moduleService.getModulesByCourseId(courseId);
    }

    // Get module by ID
    @GetMapping("/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create module for a course
    // POST /api/modules/course/1
    @PostMapping("/course/{courseId}")
    public Module createModule(@PathVariable Long courseId, @RequestBody Module module) {
        return moduleService.createModule(courseId, module);
    }

    // Update module
    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(@PathVariable Long id, @RequestBody Module module) {
        try {
            return ResponseEntity.ok(moduleService.updateModule(id, module));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete module
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok().build();
    }
}