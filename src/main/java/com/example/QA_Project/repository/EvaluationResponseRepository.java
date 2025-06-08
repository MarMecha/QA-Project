package com.example.QA_Project.repository;

import com.example.QA_Project.model.EvaluationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationResponseRepository extends JpaRepository<EvaluationResponse, Long> {

    boolean existsByFormIdAndUserId(Long formId, String userId);

    @Query("SELECT r.question, AVG(r.score) FROM EvaluationResponse r WHERE r.formId = :formId GROUP BY r.question")
    List<Object[]> findAveragesByFormId(@Param("formId") Long formId);
}
