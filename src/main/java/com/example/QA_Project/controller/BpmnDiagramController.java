package com.example.QA_Project.controller;

import com.example.QA_Project.model.BpmnDiagram;
import com.example.QA_Project.repository.BpmnDiagramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import java.util.Iterator;

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
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(diagram.getXml())));
            doc.getDocumentElement().normalize();

            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new NamespaceContext() {
                @Override
                public String getNamespaceURI(String prefix) {
                    if ("bpmn".equals(prefix)) {
                        return "http://www.omg.org/spec/BPMN/20100524/MODEL";
                    } else if ("qa".equals(prefix)) {
                        return "http://example.com/qa";
                    }
                    return XMLConstants.NULL_NS_URI;
                }

                @Override public String getPrefix(String uri) { return null; }
                @Override public Iterator<String> getPrefixes(String uri) { return null; }
            });

            // Όλα τα userTasks
            Double total = (Double) xpath.evaluate("count(//bpmn:userTask)", doc, XPathConstants.NUMBER);
            diagram.setUserTaskCount(total.intValue());

            // Ολοκληρωμένα userTasks (με qa:completed="true")
            Double completed = (Double) xpath.evaluate("count(//bpmn:userTask[@qa:completed='true'])", doc, XPathConstants.NUMBER);
            diagram.setCompletedUserTaskCount(completed.intValue());

            System.out.println("UserTasks: " + total.intValue() + " | Completed: " + completed.intValue());

        } catch (Exception e) {
            e.printStackTrace();
            diagram.setUserTaskCount(0);
            diagram.setCompletedUserTaskCount(0);
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

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteDiagram(@PathVariable String name) {
        if (!repository.existsById(name)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(name);
        return ResponseEntity.ok().build();
    }
}