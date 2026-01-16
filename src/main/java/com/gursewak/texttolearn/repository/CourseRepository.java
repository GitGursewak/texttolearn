package com.gursewak.texttolearn.repository;

import com.gursewak.texttolearn.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Spring automatically implements these methods!

    // Find by title
    Course findByTitle(String title);

    // Find by difficulty
    List<Course> findByDifficulty(String difficulty);

    // Find courses with title containing keyword
    List<Course> findByTitleContaining(String keyword);
}