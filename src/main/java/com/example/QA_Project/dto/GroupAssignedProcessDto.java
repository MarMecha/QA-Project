package com.example.QA_Project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GroupAssignedProcessDto(
        Long id,
        String processName,
        String description,
        String bpmnFileName,
        LocalDateTime assignedAt,
        LocalDate expiryDate,
        String groupName,
        String members,
        String leader
) {
}
