package com.example.QA_Project.controller;

import com.example.QA_Project.dto.DtoMapper;
import com.example.QA_Project.dto.EmployeeDto;
import com.example.QA_Project.dto.EmployeeRequest;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public EmployeeDto create(@RequestBody EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setFullName(request.fullName());
        employee.setPosition(request.position());
        employee.setUsername(request.username());
        employee.setPassword(passwordEncoder.encode(request.password()));
        return DtoMapper.toEmployeeDto(employeeRepo.save(employee));
    }

    @GetMapping
    public List<EmployeeDto> getAll() {
        return employeeRepo.findAll().stream()
                .map(DtoMapper::toEmployeeDto)
                .toList();
    }

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable Long id) {
        return employeeRepo.findById(id)
            .map(DtoMapper::toEmployeeDto)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeRepo.deleteById(id);
    }

    @PutMapping("/{id}")
    public EmployeeDto update(@PathVariable Long id, @RequestBody EmployeeRequest updated) {
        return employeeRepo.findById(id).map(emp -> {
            emp.setFullName(updated.fullName());
            emp.setPosition(updated.position());
            emp.setUsername(updated.username());
            emp.setPassword(passwordEncoder.encode(updated.password()));
            return DtoMapper.toEmployeeDto(employeeRepo.save(emp));
        }).orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
}
