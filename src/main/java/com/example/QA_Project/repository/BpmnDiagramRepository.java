package com.example.QA_Project.repository;

import com.example.QA_Project.model.BpmnDiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BpmnDiagramRepository extends JpaRepository<BpmnDiagram, String> {
}