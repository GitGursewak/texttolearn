package com.gursewak.texttolearn.repository;

import com.gursewak.texttolearn.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // Find all lessons for a specific module
    List<Lesson> findByModuleId(Long moduleId);

    // Find lessons ordered by index
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
}