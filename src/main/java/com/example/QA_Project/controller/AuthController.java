package com.example.QA_Project.controller;

import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Employee login) {
        if (login.getUsername() == null || login.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing credentials");
        }
        Employee emp = employeeRepo.findByUsername(login.getUsername());
        if (emp == null || !emp.getPassword().equals(login.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        return ResponseEntity.ok(emp);
    }
}