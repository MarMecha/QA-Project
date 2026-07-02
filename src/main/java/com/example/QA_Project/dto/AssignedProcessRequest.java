package com.example.QA_Project.dto;

import java.time.LocalDate;

public record AssignedProcessRequest(
        String processName,
        String description,
        String bpmnFileName,
        LocalDate expiryDate,
        String fullName,
        String position,
        String leader
) {
}
