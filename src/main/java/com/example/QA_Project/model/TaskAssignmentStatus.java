package com.example.QA_Project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_assignment_status")
public class TaskAssignmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagramName;
    private String taskId;
    private String assignee;

    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();

    private boolean completed;
    public Long getId() { return id; }
    public String getDiagramName() { return diagramName; }
    public void setDiagramName(String diagramName) { this.diagramName = diagramName; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
