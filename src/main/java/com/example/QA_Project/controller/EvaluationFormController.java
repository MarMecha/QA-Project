package com.example.QA_Project.controller;

import com.example.QA_Project.model.EvaluationForm;
import com.example.QA_Project.repository.EvaluationFormRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/forms")
public class EvaluationFormController {

    @Autowired
    private EvaluationFormRepository repository;

    @PostMapping
    public EvaluationForm createForm(@RequestBody EvaluationForm form) {
        form.setActive(false); // πάντα ξεκινά ανενεργή
        return repository.save(form);
    }

    @GetMapping
    public List<EvaluationForm> getAllForms() {
        return repository.findAll();
    }

    @GetMapping("/history")
    public List<EvaluationForm> getLastYearForms() {
        int lastYear = LocalDate.now().getYear() - 1;
        LocalDateTime start = LocalDate.of(lastYear, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(lastYear, 12, 31).atTime(23, 59, 59);
        return repository.findByCreatedAtBetweenOrderByCreatedAtAsc(start, end);
    }

    @GetMapping("/current")
    public List<EvaluationForm> getCurrentYearForms() {
        int year = LocalDate.now().getYear();
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        return repository.findByCreatedAtBetweenOrderByCreatedAtAsc(start, end);
    }

    @GetMapping("/{id}")
    public EvaluationForm getFormById(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));
    }

    @PutMapping("/{id}")
    public EvaluationForm updateForm(@PathVariable Long id, @RequestBody EvaluationForm updatedForm) {
        Optional<EvaluationForm> existing = repository.findById(id);
        if (existing.isPresent()) {
            EvaluationForm form = existing.get();
            form.setTitle(updatedForm.getTitle()); // ✅ προστέθηκε
            form.setQuestions(updatedForm.getQuestions());
            form.setActive(updatedForm.isActive());
            return repository.save(form);
        } else {
            throw new RuntimeException("Form not found with id: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteForm(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Η φόρμα με id " + id + " δεν βρέθηκε.");
        }
        repository.deleteById(id);
    }

    @PutMapping("/{id}/toggle")
    public EvaluationForm toggleActive(@PathVariable Long id) {
        EvaluationForm form = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));
        form.setActive(!form.isActive());
        return repository.save(form);
    }
}
