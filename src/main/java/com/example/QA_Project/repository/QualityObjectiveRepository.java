package com.example.QA_Project.repository;

import com.example.QA_Project.model.QualityObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityObjectiveRepository extends JpaRepository<QualityObjective, Long> {
}