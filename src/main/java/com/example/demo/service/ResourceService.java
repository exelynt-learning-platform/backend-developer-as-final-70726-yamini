package com.example.demo.service;

import com.example.demo.dto.ResourceDto;
import com.example.demo.entity.Resource;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ResourceMapper;
import com.example.demo.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<ResourceDto> findAll() {
        return resourceRepository.findAll().stream().map(ResourceMapper::toDto).collect(Collectors.toList());
    }

    public ResourceDto findById(Long id) {
        Resource r = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + id));
        return ResourceMapper.toDto(r);
    }

    public ResourceDto create(ResourceDto dto) {
        Resource r = ResourceMapper.toEntity(dto);
        Resource saved = resourceRepository.save(r);
        return ResourceMapper.toDto(saved);
    }

    public ResourceDto update(Long id, ResourceDto dto) {
        Resource existing = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        Resource saved = resourceRepository.save(existing);
        return ResourceMapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found: " + id);
        }
        resourceRepository.deleteById(id);
    }
}
package com.example.demo.service;

import com.example.demo.dto.resource.ResourceRequest;
import com.example.demo.dto.resource.ResourceResponse;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse getResourceById(Long id);

    List<ResourceResponse> getAllResources();

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);
}