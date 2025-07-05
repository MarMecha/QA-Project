package com.example.QA_Project.repository;

import com.example.QA_Project.model.BpmnDiagram;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BpmnDiagramRepository extends JpaRepository<BpmnDiagram, String> {
    java.util.List<BpmnDiagram> findByPublished(boolean published);

    Optional<BpmnDiagram> findByName(String name);
}