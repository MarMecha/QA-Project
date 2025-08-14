package com.example.QA_Project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AssignedProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processName;

    @Column(length = 2000)
    private String description;

    private String bpmnFileName;

    private LocalDateTime assignedAt;

    private String fullName;
    private String position;
    private String leader;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBpmnFileName() { return bpmnFileName; }
    public void setBpmnFileName(String bpmnFileName) { this.bpmnFileName = bpmnFileName; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
}
