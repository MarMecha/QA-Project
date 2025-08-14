package com.example.QA_Project.controller;

import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.AssignedProcessRepository;
import com.example.QA_Project.repository.EmployeeRepository;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import com.example.QA_Project.service.NotificationService;

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

    @Autowired
    private GroupAssignedProcessRepository groupAssignedRepo;

    @Autowired
    private NotificationService notificationService;

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
        process.setLeader(employee.getFullName());

        assignedRepo.save(process);

        notificationService.notifyAssignmentToIndividual(process);

        return ResponseEntity.ok(process);
    }

    @GetMapping("/all")
    public List<AssignedProcess> getAllProcesses() {
        return assignedRepo.findAll();
    }

    // Return individual and group QA processes for a specific employee
    @GetMapping("/employee/{id}")
    public ResponseEntity<?> getProcessesForEmployee(@PathVariable Long id) {
        return employeeRepo.findById(id).map(emp -> {
            var assigned = assignedRepo.findByFullName(emp.getFullName());
            var group = groupAssignedRepo.findByMembersContaining(emp.getFullName());
            java.util.Map<String, java.util.List<?>> res = new java.util.HashMap<>();
            res.put("assigned", assigned);
            res.put("group", group);
            return ResponseEntity.ok(res);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/leader")
    public java.util.Map<String, Boolean> isLeader(@RequestParam String diagramName,
                                                   @RequestParam String employeeName) {
        boolean leader = assignedRepo.findByBpmnFileName(diagramName).stream()
                .anyMatch(p -> employeeName.equals(p.getLeader()));
        if (!leader) {
            leader = groupAssignedRepo.findByBpmnFileName(diagramName).stream()
                    .anyMatch(p -> employeeName.equals(p.getLeader()));
        }
        return java.util.Map.of("leader", leader);
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
