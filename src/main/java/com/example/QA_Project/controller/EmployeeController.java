package com.example.QA_Project.controller;

import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return employeeRepo.save(employee);
    }

    @GetMapping
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return employeeRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeRepo.deleteById(id);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @RequestBody Employee updated) {
        return employeeRepo.findById(id).map(emp -> {
            emp.setFullName(updated.getFullName());
            emp.setPosition(updated.getPosition());
            emp.setUsername(updated.getUsername());
            emp.setPassword(updated.getPassword());
            return employeeRepo.save(emp);
        }).orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
}