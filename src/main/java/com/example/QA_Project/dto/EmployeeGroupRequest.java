package com.example.QA_Project.dto;

import java.util.List;

public record EmployeeGroupRequest(
        String name,
        List<Long> memberIds
) {
}
