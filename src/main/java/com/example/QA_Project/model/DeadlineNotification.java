package com.example.QA_Project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DeadlineNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;
    @Column(length = 1000)
    private String message;
    private String diagram;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDiagram() { return diagram; }
    public void setDiagram(String diagram) { this.diagram = diagram; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}