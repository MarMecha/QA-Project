package com.example.QA_Project.controller;

import com.example.QA_Project.model.QualityObjective;
import com.example.QA_Project.repository.QualityObjectiveRepository;
import com.example.QA_Project.repository.EvaluationResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/objectives")
public class QualityObjectiveController {

    @Autowired
    private QualityObjectiveRepository objectiveRepo;

    @Autowired
    private EvaluationResponseRepository responseRepo;

    @PostMapping
    public QualityObjective createObjective(@RequestBody QualityObjective obj) {
        return objectiveRepo.save(obj);
    }

    @GetMapping
    public List<QualityObjective> getAllObjectives() {
        return objectiveRepo.findAll();
    }

    @GetMapping("/{id}")
    public QualityObjective getObjective(@PathVariable Long id) {
        return objectiveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Objective not found with id: " + id));
    }
    @PutMapping("/{id}")
    public QualityObjective updateObjective(@PathVariable Long id, @RequestBody QualityObjective updated) {
        return objectiveRepo.findById(id).map(obj -> {
            obj.setName(updated.getName());
            obj.setDescription(updated.getDescription());
            obj.setTargetValue(updated.getTargetValue());
            obj.setFormId(updated.getFormId());
            obj.setQuestionText(updated.getQuestionText());
            return objectiveRepo.save(obj);
        }).orElseThrow(() -> new RuntimeException("Objective not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteObjective(@PathVariable Long id) {
        objectiveRepo.deleteById(id);
    }

    
    @GetMapping("/{id}/status")
    public Map<String, Object> getStatus(@PathVariable Long id) {
        QualityObjective obj = objectiveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Objective not found with id: " + id));
        Map<String, Object> result = new HashMap<>();
            result.put("objective", obj.getName());
            result.put("target", obj.getTargetValue());

            if (obj.getFormId() != null && obj.getQuestionText() != null) {
                Double avg = responseRepo.findAverageByFormIdAndQuestion(obj.getFormId(), obj.getQuestionText());
                if (avg != null) {
                    double diff = avg - obj.getTargetValue();
                    result.put("latestValue", avg);
                    result.put("difference", diff);
                    result.put("onTarget", diff >= 0);
                    return result;
                }
            }

            return result;
    }
}