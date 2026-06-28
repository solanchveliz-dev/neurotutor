package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.StudentModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentModuleProgressRepository extends JpaRepository<StudentModuleProgress, Long> {
    Optional<StudentModuleProgress> findByStudentIdAndModuloId(Long studentId, Long moduloId);
    List<StudentModuleProgress> findByStudentId(Long studentId);

    @Query("select avg(progress.progressPercentage) from StudentModuleProgress progress")
    Double findAverageProgressPercentage();
}
