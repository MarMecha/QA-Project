package com.example.QA_Project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.model.GroupAssignedProcess;
import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.TaskAssignmentStatus;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import com.example.QA_Project.repository.ProcessChatMessageRepository;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import com.example.QA_Project.repository.AssignedProcessRepository;
import com.example.QA_Project.repository.TaskAssignmentStatusRepository;

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

    @Autowired
    private TaskAssignmentStatusRepository statusRepo;

    @GetMapping("/notifications")
    public ResponseEntity<List<Map<String, String>>> getNotifications(@RequestParam String user,
                                                                     @RequestParam(required = false) String position) {
        List<Map<String, String>> notifications = new java.util.ArrayList<>();
        boolean isQAExpert = false;
        if (position != null) {
            isQAExpert = position.toLowerCase().contains("qa");
        } else if ("QA-expert".equalsIgnoreCase(user)) {
            isQAExpert = true;
        }

        List<ProcessChatMessage> messages = chatRepo.findRecentMessagesForUser(user);
        if (isQAExpert) {
            notifications.addAll(
                messages.stream().map(msg -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("message", "💬 Νέο σχόλιο από " + msg.getSender() + " στη διαδικασία: " + msg.getname());
                    map.put("diagram", diagramRepo.findByName(msg.getname())
                                    .map(d -> d.getName()).orElse(""));
                    map.put("time", msg.getSentAt().toString());
                    return map;
                }).collect(Collectors.toList())
            );
        }

        
        if (!isQAExpert) {
            for (GroupAssignedProcess gp : groupAssignedRepo.findByMembersContaining(user)) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "✅ Ανατέθηκε νέα διαδικασία στο group " + gp.getGroupName() + ": " + gp.getProcessName());
                map.put("diagram", gp.getBpmnFileName());
                if (gp.getAssignedAt() != null) map.put("time", gp.getAssignedAt().toString());
                notifications.add(map);
            }

            for (AssignedProcess ap : assignedRepo.findByFullName(user)) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "✅ Σας ανατέθηκε η διαδικασία: " + ap.getProcessName());
                map.put("diagram", ap.getBpmnFileName());
                if (ap.getAssignedAt() != null) map.put("time", ap.getAssignedAt().toString());
                notifications.add(map);
            }

            java.util.Set<String> diagrams = new java.util.HashSet<>();
            groupAssignedRepo.findByMembersContaining(user).forEach(gp -> diagrams.add(gp.getBpmnFileName()));
            assignedRepo.findByFullName(user).forEach(ap -> diagrams.add(ap.getBpmnFileName()));

            for (String d : diagrams) {
                statusRepo.findByDiagramNameOrderByUpdatedAtDesc(d).stream()
                    .filter(TaskAssignmentStatus::isCompleted)
                    .forEach(st -> {
                        Map<String, String> map = new HashMap<>();
                        String who = st.getAssignee();
                        map.put("message", "✅ " + (who != null && !who.isBlank() ? who + " ολοκλήρωσε" : "Ολοκληρώθηκε") +
                                " task στο διάγραμμα: " + d);
                        map.put("diagram", d);
                        if (st.getUpdatedAt() != null) {
                            map.put("time", st.getUpdatedAt().toString());
                        }
                        notifications.add(map);
                    });
            }
        } else {
            statusRepo.findByCompletedTrueOrderByUpdatedAtDesc().forEach(st -> {
                Map<String, String> map = new HashMap<>();
                String who = st.getAssignee();
                map.put("message", "✅ " + (who != null && !who.isBlank() ? who + " ολοκλήρωσε" : "Ολοκληρώθηκε") +
                        " task στο διάγραμμα: " + st.getDiagramName());
                map.put("diagram", st.getDiagramName());
                if (st.getUpdatedAt() != null) {
                    map.put("time", st.getUpdatedAt().toString());
                }
                notifications.add(map);
            });

            diagramRepo.findAll().forEach(d -> {
                if (d.getUserTaskCount() > 0 && d.getUserTaskCount() == d.getCompletedUserTaskCount()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("message", "🏁 Ολοκληρώθηκε η διαδικασία: " + d.getName());
                    map.put("diagram", d.getName());
                    statusRepo.findByDiagramNameOrderByUpdatedAtDesc(d.getName()).stream()
                        .filter(TaskAssignmentStatus::isCompleted)
                        .findFirst()
                        .ifPresent(st -> map.put("time", st.getUpdatedAt().toString()));
                    notifications.add(map);
                }
            });
        }

        
        notifications.sort(Comparator.comparing(m -> m.getOrDefault("time", ""), Comparator.reverseOrder()));
        notifications.forEach(m -> m.remove("time"));
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/message-notifications")
    public ResponseEntity<List<Map<String, String>>> getMessageNotifications(@RequestParam String user) {
        List<ProcessChatMessage> messages = chatRepo.findRecentMessagesForUser(user);
        Map<String, List<ProcessChatMessage>> grouped =
            messages.stream().collect(Collectors.groupingBy(ProcessChatMessage::getname));

        List<Map<String, String>> result = grouped.entrySet().stream().map(entry -> {
            List<ProcessChatMessage> groupMsgs = entry.getValue();
            ProcessChatMessage latest = groupMsgs.stream()
                    .max(Comparator.comparing(ProcessChatMessage::getSentAt))
                    .orElse(null);

            Map<String, String> map = new HashMap<>();
            map.put("message", "💬 νέα μηνύματα στη διαδικασία: " + latest.getname());
            map.put("diagram", diagramRepo.findByName(latest.getname())
                            .map(d -> d.getName()).orElse(""));
            map.put("time", latest.getSentAt().toString());
            return map;
        }).sorted(Comparator.comparing(m -> m.getOrDefault("time", ""), Comparator.reverseOrder()))
          .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
