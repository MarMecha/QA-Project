package com.example.QA_Project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BpmnDiagram {

    @Id
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String xml;

    @Column(nullable = false)
    private boolean published = false;

    @Column(nullable = false)
    private int userTaskCount = 0;

    @Column(nullable = false)
    private int completedUserTaskCount = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getUserTaskCount() {
        return userTaskCount;
    }

    public void setUserTaskCount(int userTaskCount) {
        this.userTaskCount = userTaskCount;
    }

        
    public int getCompletedUserTaskCount() {
        return completedUserTaskCount;
    }

    public void setCompletedUserTaskCount(int count) {
        this.completedUserTaskCount = count;
    }
}