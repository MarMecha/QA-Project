package com.example.QA_Project.security;

public record AuthenticatedEmployee(
        Long id,
        String username,
        String fullName,
        String position
) {
}
