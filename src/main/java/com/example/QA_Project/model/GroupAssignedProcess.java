package com.example.QA_Project.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class GroupAssignedProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processName;

    @Column(length = 2000)
    private String description;

    private String bpmnFileName;

    private LocalDateTime assignedAt;

    private LocalDate expiryDate;

    private String groupName;

    @Column(length = 1000)
    private String members;

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

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getMembers() { return members; }
    public void setMembers(String members) { this.members = members; }
    
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
}