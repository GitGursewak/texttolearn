package com.gursewak.texttolearn.controller;

import com.gursewak.texttolearn.model.Course;
import com.gursewak.texttolearn.model.Lesson;
import com.gursewak.texttolearn.service.CourseService;
import com.gursewak.texttolearn.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonService lessonService;

    // Home page - list all courses
    @GetMapping("/")
    public String home(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "index";
    }

    // View single course with modules
    @GetMapping("/course/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        model.addAttribute("course", course);
        return "course";
    }

    // View single lesson
    @GetMapping("/lesson/{id}")
    public String viewLesson(@PathVariable Long id, Model model) {
        Lesson lesson = lessonService.getLessonById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        model.addAttribute("lesson", lesson);
        return "lesson";
    }

    // Generate course page
    @GetMapping("/generate")
    public String generatePage() {
        return "generate";
    }
}