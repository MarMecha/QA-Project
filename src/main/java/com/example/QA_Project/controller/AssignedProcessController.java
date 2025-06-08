package com.example.QA_Project.controller;

import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.AssignedProcessRepository;
import com.example.QA_Project.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.QA_Project.repository.BpmnDiagramRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/qa")
public class AssignedProcessController {

    @Autowired
    private AssignedProcessRepository assignedRepo;

    @Autowired
     private EmployeeRepository employeeRepo;

    @Autowired
    private BpmnDiagramRepository diagramRepo;

    @PostMapping("/assign")
    public ResponseEntity<?> assignProcess(
        @RequestParam String processName,
        @RequestParam String employeeName,
        @RequestParam String description,
        @RequestParam(required = false) String diagramName,
        @RequestParam(value = "bpmnFile", required = false) MultipartFile file
    ) {
        Employee employee = employeeRepo.findByFullName(employeeName);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Ο υπάλληλος δεν βρέθηκε.");
        }

        if ((diagramName == null || diagramName.isBlank()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body("Απαιτείται BPMN αρχείο ή όνομα διαγράμματος.");
        }

        AssignedProcess process = new AssignedProcess();
        process.setProcessName(processName);
        process.setDescription(description);
        if (diagramName != null && !diagramName.isBlank()) {
            process.setBpmnFileName(diagramName);
        } else if (file != null) {
            process.setBpmnFileName(file.getOriginalFilename());
        }
        process.setAssignedAt(LocalDateTime.now());
        process.setFullName(employee.getFullName());
        process.setPosition(employee.getPosition());

        assignedRepo.save(process);

        return ResponseEntity.ok(process);
    }

    @GetMapping("/all")
    public List<AssignedProcess> getAllProcesses() {
        return assignedRepo.findAll();
    }

      @PutMapping("/{id}")
    public AssignedProcess update(@PathVariable Long id, @RequestBody AssignedProcess updated) {
        return assignedRepo.findById(id).map(p -> {
            p.setProcessName(updated.getProcessName());
            p.setDescription(updated.getDescription());
            p.setBpmnFileName(updated.getBpmnFileName());
            p.setFullName(updated.getFullName());
            p.setPosition(updated.getPosition());
            return assignedRepo.save(p);
        }).orElseThrow(() -> new RuntimeException("Process not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        assignedRepo.deleteById(id);
    }

}
