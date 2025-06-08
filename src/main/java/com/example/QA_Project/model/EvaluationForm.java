package com.example.QA_Project.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class EvaluationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Τίτλος υποχρεωτικός
    private String title;

    @ElementCollection
    private List<String> questions;

    @Column(nullable = false)
    private Boolean active = false; // ✅ προσθέτουμε αυτό

    // --- Getters και Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<String> getQuestions() { return questions; }
    public void setQuestions(List<String> questions) { this.questions = questions; }

    public Boolean isActive() { return active; }           // ✅ getter
    public void setActive(Boolean active) { this.active = active; } // ✅ setter
}
