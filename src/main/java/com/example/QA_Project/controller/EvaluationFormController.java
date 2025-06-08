package com.example.QA_Project.controller;

import com.example.QA_Project.model.EvaluationForm;
import com.example.QA_Project.repository.EvaluationFormRepository;

import java.util.List;
import java.util.Optional;

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
