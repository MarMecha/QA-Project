package com.example.QA_Project.model;

import jakarta.persistence.*;

@Entity
public class EvaluationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long formId;

    private String userId;

    private String question;

    private int score;

    // --- Getters και Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
