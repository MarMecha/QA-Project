package com.example.QA_Project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignedProcessDto(
        Long id,
        String processName,
        String description,
        String bpmnFileName,
        LocalDateTime assignedAt,
        LocalDate expiryDate,
        String fullName,
        String position,
        String leader
) {
}
