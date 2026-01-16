package com.gursewak.texttolearn.repository;

import com.gursewak.texttolearn.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    // Find all modules for a specific course
    List<Module> findByCourseId(Long courseId);

    // Find modules ordered by index
    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}