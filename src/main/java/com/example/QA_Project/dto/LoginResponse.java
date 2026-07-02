package com.example.QA_Project.dto;

public record LoginResponse(
        String token,
        EmployeeDto employee
) {
}
