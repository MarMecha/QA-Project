package com.example.QA_Project.controller;

import com.example.QA_Project.model.EmployeeGroup;
import com.example.QA_Project.model.GroupAssignedProcess;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.EmployeeGroupRepository;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qa")
public class GroupAssignedProcessController {

    @Autowired
    private GroupAssignedProcessRepository groupAssignedRepo;

    @Autowired
    private EmployeeGroupRepository groupRepo;

    @Autowired
    private BpmnDiagramRepository diagramRepo;

    @PostMapping("/assign-group")
    public ResponseEntity<?> assignToGroup(
            @RequestParam String processName, // μπορείς να το κρατήσεις για εμφάνιση, όχι για process logic
            @RequestParam Long groupId,
            @RequestParam String description,
            @RequestParam(required = false) String diagramName,
            @RequestParam(value = "bpmnFile", required = false) MultipartFile file
    ) {
        EmployeeGroup group = groupRepo.findById(groupId).orElse(null);
        if (group == null) {
            return ResponseEntity.badRequest().body("Το group δεν βρέθηκε.");
        }

        if ((diagramName == null || diagramName.isBlank()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body("Απαιτείται BPMN αρχείο ή όνομα διαγράμματος.");
        }

        GroupAssignedProcess process = new GroupAssignedProcess();

        // 🔁 Χρήση BPMN διαγράμματος ως process name (για να δένει με chat & notifications)
        if (diagramName != null && !diagramName.isBlank()) {
            process.setProcessName(diagramName);
            process.setBpmnFileName(diagramName);
        } else {
            process.setProcessName(file.getOriginalFilename());
            process.setBpmnFileName(file.getOriginalFilename());
        }

        process.setDescription(description);
        process.setAssignedAt(LocalDateTime.now());
        process.setGroupName(group.getName());

        String members = group.getMembers().stream()
                .map(Employee::getFullName)
                .collect(Collectors.joining(", "));
        process.setMembers(members);

        groupAssignedRepo.save(process);

        return ResponseEntity.ok(process);
    }


    @GetMapping("/group-all")
    public java.util.List<GroupAssignedProcess> getAllGroupProcesses() {
        return groupAssignedRepo.findAll();
    }

    @PutMapping("/group/{id}")
    public GroupAssignedProcess updateGroup(@PathVariable Long id, @RequestBody GroupAssignedProcess updated) {
        return groupAssignedRepo.findById(id).map(p -> {
            p.setProcessName(updated.getProcessName());
            p.setDescription(updated.getDescription());
            p.setBpmnFileName(updated.getBpmnFileName());
            p.setGroupName(updated.getGroupName());
            p.setMembers(updated.getMembers());
            return groupAssignedRepo.save(p);
        }).orElseThrow(() -> new RuntimeException("Process not found with id: " + id));
    }

    @DeleteMapping("/group/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupAssignedRepo.deleteById(id);
    }
}