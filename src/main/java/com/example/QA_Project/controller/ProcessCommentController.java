package com.example.QA_Project.controller;

import com.example.QA_Project.model.ProcessComment;
import com.example.QA_Project.repository.ProcessCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ProcessComments")
public class ProcessCommentController {

    @Autowired
    private ProcessCommentRepository commentRepo;

    @PostMapping
    public ProcessComment createComment(@RequestBody ProcessComment comment) {
        return commentRepo.save(comment);
    }

    @GetMapping("/{processId}")
    public List<ProcessComment> getComments(@PathVariable Long processId) {
        return commentRepo.findByProcessIdOrderByCreatedAtAsc(processId);
    }
}