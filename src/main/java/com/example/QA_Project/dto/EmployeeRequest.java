package com.example.QA_Project.dto;

public record EmployeeRequest(
        String fullName,
        String position,
        String username,
        String password
) {
}
