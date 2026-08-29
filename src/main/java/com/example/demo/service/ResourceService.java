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