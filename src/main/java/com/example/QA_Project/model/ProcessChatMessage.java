package com.example.QA_Project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ProcessChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String sender;

    @Column(length = 2000)
    private String message;

    private LocalDateTime sentAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getname() { return name; }
    public void setname(String name) { this.name = name; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}