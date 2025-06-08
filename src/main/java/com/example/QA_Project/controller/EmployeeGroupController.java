package com.example.QA_Project.controller;

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
    public EmployeeGroup create(@RequestBody GroupRequest request) {
        EmployeeGroup group = new EmployeeGroup();
        group.setName(request.getName());
        List<Employee> members = employeeRepo.findAllById(request.getMemberIds());
        group.setMembers(members);
        return groupRepo.save(group);
    }

    @GetMapping
    public List<EmployeeGroup> getAll() {
        return groupRepo.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupRepo.deleteById(id);
    }

    public static class GroupRequest {
        private String name;
        private List<Long> memberIds;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Long> getMemberIds() { return memberIds; }
        public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
    }
}