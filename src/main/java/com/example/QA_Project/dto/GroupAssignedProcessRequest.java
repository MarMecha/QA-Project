package com.example.QA_Project.dto;

import java.time.LocalDate;

public record GroupAssignedProcessRequest(
        String processName,
        String description,
        String bpmnFileName,
        LocalDate expiryDate,
        String groupName,
        String members,
        String leader
) {
}
