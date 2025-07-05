package com.example.QA_Project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.QA_Project.model.ProcessChatMessage;
import com.example.QA_Project.repository.ProcessChatMessageRepository;

@Service
public class ProcessChatService {
    private final ProcessChatMessageRepository ProcesschatRepository;

    public ProcessChatService(ProcessChatMessageRepository chatRepository) {
        this.ProcesschatRepository = chatRepository;
    }

    public List<ProcessChatMessage> getChatByname(String name) {
        return ProcesschatRepository.findBynameOrderBySentAtAsc(name);
    }
}
