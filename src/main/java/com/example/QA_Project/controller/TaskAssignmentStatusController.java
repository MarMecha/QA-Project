package com.example.QA_Project.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.QA_Project.model.TaskAssignmentStatus;
import com.example.QA_Project.repository.TaskAssignmentStatusRepository;

@RestController
@RequestMapping("/api/bpmn")
public class TaskAssignmentStatusController {

    @Autowired
    private TaskAssignmentStatusRepository repository;

    @PostMapping("/assign-status")
    public ResponseEntity<?> saveStatus(@RequestBody TaskAssignmentStatus incoming) {
        Optional<TaskAssignmentStatus> existingOpt = repository.findByDiagramNameAndTaskId(
            incoming.getDiagramName(), incoming.getTaskId()
        );

        TaskAssignmentStatus entity = existingOpt.orElse(new TaskAssignmentStatus());
        entity.setDiagramName(incoming.getDiagramName());
        entity.setTaskId(incoming.getTaskId());
        entity.setAssignee(incoming.getAssignee());
        entity.setCompleted(incoming.isCompleted());

        repository.save(entity);

        return ResponseEntity.ok().body("✅ Αποθηκεύτηκε");
    }

    @GetMapping("/assign-status/{diagramName}")
    public ResponseEntity<List<TaskAssignmentStatus>> getStatus(@PathVariable String diagramName) {
        return ResponseEntity.ok(repository.findByDiagramName(diagramName));
    }
}
