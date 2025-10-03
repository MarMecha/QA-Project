package com.example.QA_Project.repository;

import com.example.QA_Project.model.DeadlineNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeadlineNotificationRepository extends JpaRepository<DeadlineNotification, Long> {
    List<DeadlineNotification> findByRecipient(String recipient);
    boolean existsByRecipientAndMessageAndDiagram(String recipient, String message, String diagram);
}