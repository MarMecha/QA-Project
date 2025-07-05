package com.example.QA_Project.controller;

import com.example.QA_Project.model.BpmnDiagram;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bpmn")
public class BpmnDiagramController {

    @Autowired
    private BpmnDiagramRepository repository;

    @PostMapping
    public ResponseEntity<?> saveDiagram(@RequestBody BpmnDiagram diagram) {
        if (diagram.getName() == null || diagram.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing diagram name");
        }
        repository.save(diagram);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<BpmnDiagram> listDiagrams(@RequestParam(required = false) Boolean published) {
        System.out.println("Param published = " + published);

        if (published != null) {
            return repository.findByPublished(published);
        }
        return repository.findAll();
    }

    @GetMapping("/{name}")
    public ResponseEntity<BpmnDiagram> getDiagram(@PathVariable String name) {
        Optional<BpmnDiagram> diagram = repository.findById(name);
        return diagram.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{name}/toggle")
    public ResponseEntity<BpmnDiagram> togglePublish(@PathVariable String name) {
        Optional<BpmnDiagram> diagram = repository.findById(name);
        if (diagram.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BpmnDiagram d = diagram.get();
        d.setPublished(!d.isPublished());
        repository.save(d);
        return ResponseEntity.ok(d);
    }
}