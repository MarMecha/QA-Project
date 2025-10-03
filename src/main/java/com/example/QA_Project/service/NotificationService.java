package com.example.QA_Project.service;

import com.example.QA_Project.model.GroupAssignedProcess;
import com.example.QA_Project.model.AssignedProcess;
import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.model.TaskAssignmentStatus;
import com.example.QA_Project.model.BpmnDiagram;
import com.example.QA_Project.model.DeadlineNotification;
import com.example.QA_Project.model.Employee;
import com.example.QA_Project.repository.GroupAssignedProcessRepository;
import com.example.QA_Project.repository.AssignedProcessRepository;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import com.example.QA_Project.repository.DeadlineNotificationRepository;
import com.example.QA_Project.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final GroupAssignedProcessRepository assignedRepo;
    private final AssignedProcessRepository individualRepo;
    private final BpmnDiagramRepository diagramRepo;
    private final DeadlineNotificationRepository deadlineRepo;
    private final EmployeeRepository employeeRepo;

    public NotificationService(GroupAssignedProcessRepository assignedRepo,
                               AssignedProcessRepository individualRepo,
                               BpmnDiagramRepository diagramRepo,
                               DeadlineNotificationRepository deadlineRepo,
                               EmployeeRepository employeeRepo) {
        this.assignedRepo = assignedRepo;
        this.individualRepo = individualRepo;
        this.diagramRepo = diagramRepo;
        this.deadlineRepo = deadlineRepo;
        this.employeeRepo = employeeRepo;
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
    public void notifyProcessCompletion(String diagramName) {
        System.out.println("📌 notifyProcessCompletion ΚΛΗΘΗΚΕ για διάγραμμα: " + diagramName);
        System.out.println("📢 Ο QA-expert ενημερώθηκε ότι το διάγραμμα " + diagramName + " ολοκληρώθηκε");
    }

    private void notifyUsers(String diagramName, String message, List<String> users) {
        for (String u : users) {
            System.out.println("📢 Ειδοποίηση στον " + u + ": " + message);
            if (!deadlineRepo.existsByRecipientAndMessageAndDiagram(u, message, diagramName)) {
                DeadlineNotification n = new DeadlineNotification();
                n.setRecipient(u);
                n.setMessage(message);
                n.setDiagram(diagramName);
                deadlineRepo.save(n);
            }
        }
    }

    private void evaluateProcess(String processName, String diagramName,
                                 LocalDate assignedAt, LocalDate expiryDate,
                                 List<String> recipients) {
        if (expiryDate == null || assignedAt == null) return;
        LocalDate today = LocalDate.now();
        long total = ChronoUnit.DAYS.between(assignedAt, expiryDate);
        long elapsed = ChronoUnit.DAYS.between(assignedAt, today);
        long daysLeft = ChronoUnit.DAYS.between(today, expiryDate);
        if (total <= 0) return;

        int progress = 0;
        BpmnDiagram diagram = diagramRepo.findById(diagramName).orElse(null);
        if (diagram != null && diagram.getUserTaskCount() > 0) {
            progress = (int) Math.round((diagram.getCompletedUserTaskCount() * 100.0) / diagram.getUserTaskCount());
        }

        long quarter = total / 4;
        long half = total / 2;
        long threeQuarter = (total * 3) / 4;

        if (elapsed >= half && progress == 0) {
            notifyUsers(diagramName, "Running out of time on \"" + processName + "\" process.", recipients);
        } else if (elapsed >= quarter && progress < 25) {
            notifyUsers(diagramName, "We got through the quarter of the available time on \"" + processName + "\" process but still havent completed the 25%.", recipients);
        } else if (elapsed >= half && progress < 50) {
            notifyUsers(diagramName, "We got through the half of the available time on \"" + processName + "\" process but still havent completed the 50%.", recipients);
        } else if (elapsed >= threeQuarter && progress < 75) {
            notifyUsers(diagramName, "We got through the 3/4 of the available time on \"" + processName + "\" process but still havent completed the 75%.", recipients);
        }

        if (daysLeft <= 2) {
            notifyUsers(diagramName, "There are two days remaining on \"" + processName + "\" process before the expiring date .", recipients);
        }
    }

    private List<String> qaExperts() {
        return employeeRepo.findByPositionContainingIgnoreCase("qa").stream()
                .map(Employee::getFullName)
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void checkDeadlines() {
        List<String> qa = qaExperts();

        individualRepo.findAll().forEach(p -> {
            List<String> rec = new ArrayList<>();
            rec.add(p.getFullName());
            rec.addAll(qa);
            evaluateProcess(p.getProcessName(), p.getBpmnFileName(),
                    p.getAssignedAt() != null ? p.getAssignedAt().toLocalDate() : null,
                    p.getExpiryDate(), rec);
        });

        assignedRepo.findAll().forEach(p -> {
            List<String> rec = new ArrayList<>(Arrays.asList(p.getMembers().split(",\\s*")));
            rec.addAll(qa);
            evaluateProcess(p.getProcessName(), p.getBpmnFileName(),
                    p.getAssignedAt() != null ? p.getAssignedAt().toLocalDate() : null,
                    p.getExpiryDate(), rec);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        checkDeadlines();
    }
}
