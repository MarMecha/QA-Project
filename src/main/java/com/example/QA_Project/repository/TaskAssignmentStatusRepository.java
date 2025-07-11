package com.example.QA_Project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.QA_Project.model.TaskAssignmentStatus;

public interface TaskAssignmentStatusRepository extends JpaRepository<TaskAssignmentStatus, Long> {
    List<TaskAssignmentStatus> findByDiagramName(String diagramName);
    Optional<TaskAssignmentStatus> findByDiagramNameAndTaskId(String diagramName, String taskId);
}
