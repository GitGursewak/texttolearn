package com.gursewak.texttolearn.service;

import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AIService aiService;

    // Create a pending course skeleton instantly
    public Course createPendingCourse(String topic, String difficulty, String userId) {
        Course course = new Course();
        course.setTitle(topic); // Temp title
        course.setDescription("Generating curriculum...");
        course.setDifficulty(difficulty);
        course.setUserId(userId);
        course.setStatus("PENDING");
        return courseRepository.save(course);
    }

    // Heavy AI generation to be called by Pub/Sub worker
    public void generateAndSaveCourseContent(Long courseId, String topic, String difficulty, String userId) {
        // 1. Generate heavy content
        Course generatedCourse = aiService.generateCourse(topic, difficulty);
        
        // 2. Retrieve the pending skeleton
        Course existingCourse = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Pending course not found"));
            
        // 3. Update the skeleton with the real content
        existingCourse.setTitle(generatedCourse.getTitle());
        existingCourse.setDescription(generatedCourse.getDescription());
        existingCourse.setModules(generatedCourse.getModules());
        
        // Ensure bidirectional relationship is set
        if (existingCourse.getModules() != null) {
            existingCourse.getModules().forEach(module -> {
                module.setCourse(existingCourse);
                if (module.getLessons() != null) {
                    module.getLessons().forEach(lesson -> lesson.setModule(module));
                }
            });
        }
        
        existingCourse.setStatus("COMPLETED");
        
        // 4. Save
        courseRepository.save(existingCourse);
    }

    // Mark a course as failed
    public void markCourseFailed(Long courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setStatus("FAILED");
            courseRepository.save(course);
        });
    }

    // Get all courses for user
    public List<Course> getAllCourses(String userId) {
        return courseRepository.findByUserId(userId);
    }

    // Get course by ID
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    // Create course
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // Update course
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setDifficulty(courseDetails.getDifficulty());

        return courseRepository.save(course);
    }

    // Delete course
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // Search courses
    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByTitleContaining(keyword);
    }

    // Filter by difficulty
    public List<Course> getCoursesByDifficulty(String difficulty) {
        return courseRepository.findByDifficulty(difficulty);
    }
}