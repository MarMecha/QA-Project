package com.example.QA_Project.dto;

import java.util.List;

public record EmployeeGroupDto(
        Long id,
        String name,
        List<EmployeeDto> members
) {
}
