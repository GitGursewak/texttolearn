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

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
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