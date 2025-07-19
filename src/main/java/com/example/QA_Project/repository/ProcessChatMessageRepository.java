package com.example.QA_Project.repository;

import com.example.QA_Project.model.ProcessChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProcessChatMessageRepository extends JpaRepository<ProcessChatMessage, Long> {
    List<ProcessChatMessage> findBynameOrderBySentAtAsc(String name);

    @Query("SELECT m FROM ProcessChatMessage m WHERE m.sender <> :user AND m.name IN (" +
           "SELECT p.processName FROM GroupAssignedProcess p WHERE p.members LIKE CONCAT('%', :user, '%')) " +
           "ORDER BY m.sentAt DESC")
    List<ProcessChatMessage> findRecentMessagesForUser(String user);
}