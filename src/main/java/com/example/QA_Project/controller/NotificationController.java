package com.example.QA_Project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import com.example.QA_Project.repository.ProcessChatMessageRepository;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private ProcessChatMessageRepository chatRepo;

    @Autowired
    private BpmnDiagramRepository diagramRepo;

    @GetMapping("/notifications")
    public ResponseEntity<List<Map<String, String>>> getNotifications(@RequestParam String user) {
        List<ProcessChatMessage> messages = chatRepo.findRecentMessagesForUser(user);

        List<Map<String, String>> notifications = messages.stream().map(msg -> {
            Map<String, String> map = new HashMap<>();
            map.put("message", "💬 Νέο σχόλιο από " + msg.getSender() + " στη διαδικασία: " + msg.getname());
            map.put("diagram", diagramRepo.findByName(msg.getname())  // Ή lookup από GroupAssignedProcess
                            .map(d -> d.getName()).orElse(""));     // μπορεί να προσαρμοστεί
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }
}
