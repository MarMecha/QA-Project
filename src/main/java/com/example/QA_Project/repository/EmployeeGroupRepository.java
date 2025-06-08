package com.example.QA_Project.repository;

import com.example.QA_Project.model.EmployeeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeGroupRepository extends JpaRepository<EmployeeGroup, Long> {
}