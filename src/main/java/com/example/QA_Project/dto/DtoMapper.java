package com.example.QA_Project.dto;

import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.model.EmployeeGroup;
import com.example.QA_Project.model.GroupAssignedProcess;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static EmployeeDto toEmployeeDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getUsername()
        );
    }

    public static EmployeeGroupDto toEmployeeGroupDto(EmployeeGroup group) {
        return new EmployeeGroupDto(
                group.getId(),
                group.getName(),
                group.getMembers().stream()
                        .map(DtoMapper::toEmployeeDto)
                        .toList()
        );
    }

    public static AssignedProcessDto toAssignedProcessDto(AssignedProcess process) {
        return new AssignedProcessDto(
                process.getId(),
                process.getProcessName(),
                process.getDescription(),
                process.getBpmnFileName(),
                process.getAssignedAt(),
                process.getExpiryDate(),
                process.getFullName(),
                process.getPosition(),
                process.getLeader()
        );
    }

    public static GroupAssignedProcessDto toGroupAssignedProcessDto(GroupAssignedProcess process) {
        return new GroupAssignedProcessDto(
                process.getId(),
                process.getProcessName(),
                process.getDescription(),
                process.getBpmnFileName(),
                process.getAssignedAt(),
                process.getExpiryDate(),
                process.getGroupName(),
                process.getMembers(),
                process.getLeader()
        );
    }
}
