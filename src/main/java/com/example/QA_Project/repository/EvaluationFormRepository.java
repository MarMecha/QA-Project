package com.example.QA_Project.repository;

import com.example.QA_Project.model.EvaluationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationFormRepository extends JpaRepository<EvaluationForm, Long> {
}
