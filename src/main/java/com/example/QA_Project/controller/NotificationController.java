package com.example.QA_Project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.model.GroupAssignedProcess;
import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import com.example.QA_Project.repository.ProcessChatMessageRepository;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import com.example.QA_Project.repository.AssignedProcessRepository;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private ProcessChatMessageRepository chatRepo;

    @Autowired
    private BpmnDiagramRepository diagramRepo;

    @Autowired
    private GroupAssignedProcessRepository groupAssignedRepo;

    @Autowired
    private AssignedProcessRepository assignedRepo;

    @GetMapping("/notifications")
    public ResponseEntity<List<Map<String, String>>> getNotifications(@RequestParam String user) {
        List<Map<String, String>> notifications = new java.util.ArrayList<>();

        List<ProcessChatMessage> messages = chatRepo.findRecentMessagesForUser(user);
        notifications.addAll(
            messages.stream().map(msg -> {
                Map<String, String> map = new HashMap<>();
                map.put("message", "💬 Νέο σχόλιο από " + msg.getSender() + " στη διαδικασία: " + msg.getname());
                map.put("diagram", diagramRepo.findByName(msg.getname())
                                .map(d -> d.getName()).orElse(""));
                return map;
            }).collect(Collectors.toList())
        );

        for (GroupAssignedProcess gp : groupAssignedRepo.findByMembersContaining(user)) {
            Map<String, String> map = new HashMap<>();
            map.put("message", "✅ Ανατέθηκε νέα διαδικασία στο group " + gp.getGroupName() + ": " + gp.getProcessName());
            map.put("diagram", gp.getBpmnFileName());
            notifications.add(map);
        }

        for (AssignedProcess ap : assignedRepo.findByFullName(user)) {
            Map<String, String> map = new HashMap<>();
            map.put("message", "✅ Σας ανατέθηκε η διαδικασία: " + ap.getProcessName());
            map.put("diagram", ap.getBpmnFileName());
            notifications.add(map);
        }

        return ResponseEntity.ok(notifications);
    }
}
