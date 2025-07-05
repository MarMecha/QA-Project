package com.example.QA_Project.controller;

import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.repository.ProcessChatMessageRepository;
import com.example.QA_Project.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ProcessChatController {
    
    @Autowired
    private ProcessChatMessageRepository chatRepo;

    @Autowired
    private NotificationService notificationService; // ✅

    @PostMapping
    public ProcessChatMessage sendMessage(@RequestBody ProcessChatMessage message) {
        ProcessChatMessage saved = chatRepo.save(message);

        // 🔥 Trigger ειδοποίησης
        notificationService.notifyParticipants(saved);

        return saved;
    }

    @GetMapping("/{name}")
    public List<ProcessChatMessage> getMessages(@PathVariable String name) {
        return chatRepo.findBynameOrderBySentAtAsc(name);
    }
}