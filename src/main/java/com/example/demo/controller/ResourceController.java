package com.example.demo.controller;

import com.example.demo.dto.ResourceDto;
import com.example.demo.service.ResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceDto> list() {
        return resourceService.findAll();
    }

    @GetMapping("/{id}")
    public ResourceDto get(@PathVariable Long id) {
        return resourceService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceDto> create(@RequestBody ResourceDto dto) {
        ResourceDto created = resourceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDto update(@PathVariable Long id, @RequestBody ResourceDto dto) {
        return resourceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

