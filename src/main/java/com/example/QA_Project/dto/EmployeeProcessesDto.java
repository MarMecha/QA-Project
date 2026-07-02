package com.example.QA_Project.dto;

import java.util.List;

public record EmployeeProcessesDto(
        List<AssignedProcessDto> assigned,
        List<GroupAssignedProcessDto> group
) {
}
