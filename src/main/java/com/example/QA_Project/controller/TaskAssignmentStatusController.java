package com.example.QA_Project.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.QA_Project.model.TaskAssignmentStatus;
import com.example.QA_Project.model.BpmnDiagram;
import com.example.QA_Project.repository.TaskAssignmentStatusRepository;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import com.example.QA_Project.service.NotificationService;

@RestController
@RequestMapping("/api/bpmn")
public class TaskAssignmentStatusController {

    @Autowired
    private TaskAssignmentStatusRepository repository;

     @Autowired
    private BpmnDiagramRepository diagramRepo;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/assign-status")
    public ResponseEntity<?> saveStatus(@RequestBody TaskAssignmentStatus incoming) {
        Optional<TaskAssignmentStatus> existingOpt = repository
            .findTopByDiagramNameAndTaskIdOrderByUpdatedAtDesc(
                incoming.getDiagramName(),
                incoming.getTaskId()
            );
        boolean wasCompleted = false;

        TaskAssignmentStatus entity;

        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            wasCompleted = entity.isCompleted();
        } else {
            entity = new TaskAssignmentStatus();
            entity.setDiagramName(incoming.getDiagramName());
            entity.setTaskId(incoming.getTaskId());
        }

        entity.setAssignee(incoming.getAssignee());
        entity.setCompleted(incoming.isCompleted());
        entity.setUpdatedAt(java.time.LocalDateTime.now());

        repository.save(entity);

        // DEBUG LOGGING (προσωρινό)
        boolean nowCompleted = entity.isCompleted();
        System.out.println("🔁 wasCompleted: " + wasCompleted);
        System.out.println("🔁 nowCompleted: " + nowCompleted);

        if (!wasCompleted && nowCompleted) {
            notificationService.notifyTaskCompletion(entity);
            if (isProcessComplete(entity.getDiagramName())) {
                notificationService.notifyProcessCompletion(entity.getDiagramName());
            }
        }

        return ResponseEntity.ok().body("✅ Αποθηκεύτηκε");
    }

    private boolean isProcessComplete(String diagramName) {
        List<TaskAssignmentStatus> statuses = repository.findByDiagramNameOrderByUpdatedAtDesc(diagramName);
        java.util.Map<String, Boolean> latest = new java.util.HashMap<>();
        for (TaskAssignmentStatus st : statuses) {
            if (!latest.containsKey(st.getTaskId())) {
                latest.put(st.getTaskId(), st.isCompleted());
            }
        }
        int completed = (int) latest.values().stream().filter(Boolean::booleanValue).count();
        int total = diagramRepo.findById(diagramName).map(BpmnDiagram::getUserTaskCount).orElse(latest.size());
        return total > 0 && completed == total;
    }
    
    @GetMapping("/assign-status/{diagramName}")
    public ResponseEntity<List<TaskAssignmentStatus>> getStatus(@PathVariable String diagramName) {
        return ResponseEntity.ok(repository.findByDiagramNameOrderByUpdatedAtDesc(diagramName));
    }
}
