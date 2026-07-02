package com.example.QA_Project.controller;

import com.example.QA_Project.dto.DtoMapper;
import com.example.QA_Project.dto.EmployeeGroupDto;
import com.example.QA_Project.dto.EmployeeGroupRequest;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.model.EmployeeGroup;
import com.example.QA_Project.repository.EmployeeGroupRepository;
import com.example.QA_Project.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class EmployeeGroupController {

    @Autowired
    private EmployeeGroupRepository groupRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @PostMapping
    public EmployeeGroupDto create(@RequestBody EmployeeGroupRequest request) {
        EmployeeGroup group = new EmployeeGroup();
        group.setName(request.name());
        List<Employee> members = employeeRepo.findAllById(request.memberIds());
        group.setMembers(members);
        return DtoMapper.toEmployeeGroupDto(groupRepo.save(group));
    }

    @GetMapping
    public List<EmployeeGroupDto> getAll() {
        return groupRepo.findAll().stream()
                .map(DtoMapper::toEmployeeGroupDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupRepo.deleteById(id);
    }
}
