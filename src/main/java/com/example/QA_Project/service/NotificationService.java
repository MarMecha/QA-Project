package com.example.QA_Project.service;

import com.example.QA_Project.model.GroupAssignedProcess;
import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final GroupAssignedProcessRepository assignedRepo;

    public NotificationService(GroupAssignedProcessRepository assignedRepo) {
        this.assignedRepo = assignedRepo;
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
}
