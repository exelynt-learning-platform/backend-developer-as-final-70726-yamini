package com.example.demo.mapper;

import com.example.demo.dto.ResourceDto;
import com.example.demo.entity.Resource;

public class ResourceMapper {

    public static ResourceDto toDto(Resource res) {
        if (res == null) return null;
        ResourceDto d = new ResourceDto();
        d.setId(res.getId());
        d.setName(res.getName());
        d.setDescription(res.getDescription());
        d.setPrice(res.getPrice());
        return d;
    }

    public static Resource toEntity(ResourceDto dto) {
        if (dto == null) return null;
        Resource r = new Resource();
        r.setId(dto.getId());
        r.setName(dto.getName());
        r.setDescription(dto.getDescription());
        r.setPrice(dto.getPrice());
        return r;
    }
}
