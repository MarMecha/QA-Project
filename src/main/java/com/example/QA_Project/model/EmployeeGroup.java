package com.example.QA_Project.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EmployeeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> members = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Employee> getMembers() { return members; }
    public void setMembers(List<Employee> members) { this.members = members; }
}