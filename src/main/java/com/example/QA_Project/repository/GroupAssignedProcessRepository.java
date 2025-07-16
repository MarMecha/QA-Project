package com.example.QA_Project.repository;

import com.example.QA_Project.model.GroupAssignedProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupAssignedProcessRepository extends JpaRepository<GroupAssignedProcess, Long> {

    // Find processes assigned to groups that contain a specific employee name
    java.util.List<GroupAssignedProcess> findByMembersContaining(String fullName);
    
    java.util.List<GroupAssignedProcess> findByBpmnFileName(String bpmnFileName);
}