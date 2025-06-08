package com.example.QA_Project.controller;

import com.example.QA_Project.model.EvaluationResponse;
import com.example.QA_Project.repository.EvaluationResponseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/responses")
public class EvaluationResponseController {

    @Autowired
    private EvaluationResponseRepository responseRepo;

    @PostMapping
    public ResponseEntity<?> submitResponses(@RequestBody List<EvaluationResponse> responses) {
        if (responses.isEmpty()) return ResponseEntity.badRequest().body("Empty response list");

        String userId = responses.get(0).getUserId();
        Long formId = responses.get(0).getFormId();

        boolean alreadySubmitted = responseRepo.existsByFormIdAndUserId(formId, userId);
        if (alreadySubmitted) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ο χρήστης έχει ήδη αξιολογήσει αυτή τη φόρμα.");
        }

        responseRepo.saveAll(responses);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/averages/{formId}")
    public Map<String, Double> getAverages(@PathVariable Long formId) {
        List<Object[]> raw = responseRepo.findAveragesByFormId(formId);
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : raw) {
            String question = (String) row[0];
            Double average = (Double) row[1];
            result.put(question, Math.round(average * 100.0) / 100.0); // Στρογγυλοποίηση 2 δεκαδικά
        }
        return result;
    }
}
