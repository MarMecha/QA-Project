package com.example.QA_Project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.QA_Project.model.TaskAssignmentStatus;

public interface TaskAssignmentStatusRepository extends JpaRepository<TaskAssignmentStatus, Long> {
    List<TaskAssignmentStatus> findByDiagramNameOrderByUpdatedAtDesc(String diagramName);
    Optional<TaskAssignmentStatus> findByDiagramNameAndTaskId(String diagramName, String taskId);

    // Retrieve all completed task statuses
    List<TaskAssignmentStatus> findByCompletedTrueOrderByUpdatedAtDesc();
    
    @Modifying
    @Transactional
    @Query("delete from TaskAssignmentStatus s where s.diagramName = :diagramName")
    void deleteByDiagramName(@Param("diagramName") String diagramName);
}
