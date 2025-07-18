package com.example.QA_Project.repository;

import com.example.QA_Project.model.EvaluationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvaluationFormRepository extends JpaRepository<EvaluationForm, Long> {
    List<EvaluationForm> findByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime start, LocalDateTime end);
    EvaluationForm findTopByOrderByCreatedAtDesc();
}
