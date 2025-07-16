package com.example.QA_Project.service;

import com.example.QA_Project.model.GroupAssignedProcess;
import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.model.TaskAssignmentStatus;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import com.example.QA_Project.repository.AssignedProcessRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final GroupAssignedProcessRepository assignedRepo;
    private final AssignedProcessRepository individualRepo;

    public NotificationService(GroupAssignedProcessRepository assignedRepo,
                               AssignedProcessRepository individualRepo) {
        this.assignedRepo = assignedRepo;
        this.individualRepo = individualRepo;
    }


    public void notifyParticipants(ProcessChatMessage message) {
        String processName = message.getname();
        String sender = message.getSender();

        System.out.println("📌 notifyParticipants ΚΛΗΘΗΚΕ για process: " + processName);
        System.out.println("📌 Sender: " + sender);

        List<GroupAssignedProcess> groups = assignedRepo.findAll().stream()
                .filter(p -> p.getProcessName().equalsIgnoreCase(processName))
                .collect(Collectors.toList());

        for (GroupAssignedProcess group : groups) {
            System.out.println("👥 Βρέθηκε group: " + group.getGroupName());

            String[] membersArray = group.getMembers().split(",\\s*");

            for (String member : membersArray) {
                System.out.println("👉 Μέλος: " + member);
                if (!member.equalsIgnoreCase(sender)) {
                    System.out.println("📢 Ειδοποίηση στον " + member);
                }
            }
        }
    }

    public void notifyAssignmentToIndividual(AssignedProcess process) {
        System.out.println("📌 notifyAssignmentToIndividual ΚΛΗΘΗΚΕ για process: " + process.getProcessName());
        System.out.println("📢 Ειδοποίηση νέας ανάθεσης στον " + process.getFullName());
    }

    public void notifyAssignmentToGroup(GroupAssignedProcess process) {
        System.out.println("📌 notifyAssignmentToGroup ΚΛΗΘΗΚΕ για process: " + process.getProcessName());
        String[] membersArray = process.getMembers().split(",\\s*");
        for (String member : membersArray) {
            System.out.println("📢 Ειδοποίηση νέας ανάθεσης στον " + member);
        }
    }

    public void notifyTaskCompletion(TaskAssignmentStatus status) {
        System.out.println("📌 notifyTaskCompletion ΚΛΗΘΗΚΕ για διάγραμμα: " + status.getDiagramName());

        // Ειδοποίηση σε μέλη group διαδικασιών
        assignedRepo.findAll().stream()
                .filter(p -> p.getBpmnFileName().equalsIgnoreCase(status.getDiagramName()))
                .forEach(p -> {
                    for (String member : p.getMembers().split(",\\s*")) {
                        System.out.println("📢 Ειδοποίηση ολοκλήρωσης στον " + member);
                    }
                });

        // Ειδοποίηση σε ατομικές αναθέσεις
        individualRepo.findByBpmnFileName(status.getDiagramName()).forEach(p -> {
            System.out.println("📢 Ειδοποίηση ολοκλήρωσης στον " + p.getFullName());
        });
    }

}
