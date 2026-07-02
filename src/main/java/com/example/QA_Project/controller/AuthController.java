package com.example.QA_Project.controller;

import com.example.QA_Project.dto.DtoMapper;
import com.example.QA_Project.dto.EmployeeDto;
import com.example.QA_Project.dto.LoginRequest;
import com.example.QA_Project.dto.LoginResponse;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.EmployeeRepository;
import com.example.QA_Project.security.AuthenticatedEmployee;
import com.example.QA_Project.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        if (login.username() == null || login.password() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing credentials");
        }
        Employee emp = employeeRepo.findByUsername(login.username());
        if (emp == null || !passwordMatches(login.password(), emp.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        return ResponseEntity.ok(new LoginResponse(jwtService.generateToken(emp), DtoMapper.toEmployeeDto(emp)));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedEmployee principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        return ResponseEntity.ok(new EmployeeDto(
                principal.id(),
                principal.fullName(),
                principal.position(),
                principal.username()
        ));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }
}
