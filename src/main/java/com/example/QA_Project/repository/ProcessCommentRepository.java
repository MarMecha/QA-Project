package com.example.QA_Project.repository;

import com.example.QA_Project.model.ProcessComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProcessCommentRepository extends JpaRepository<ProcessComment, Long> {
    List<ProcessComment> findByProcessIdOrderByCreatedAtAsc(Long processId);
}