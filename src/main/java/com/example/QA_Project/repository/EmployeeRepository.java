package com.example.QA_Project.repository;

import com.example.QA_Project.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @NonNull
    @Override
    Optional<Employee> findById(@NonNull Long id);

    Employee findByFullName(String fullName);

    Employee findByUsername(String username);
}