package com.example.QA_Project.repository;

import com.example.QA_Project.model.AssignedProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignedProcessRepository extends JpaRepository<AssignedProcess, Long> {

    // Retrieve all processes assigned to a specific employee
    java.util.List<AssignedProcess> findByFullName(String fullName);
}