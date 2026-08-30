package com.example.demo.service;

import com.example.demo.dto.ResourceDto;
import com.example.demo.entity.Resource;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = new Resource();
        testResource.setId(1L);
        testResource.setName("Conference Room A");
        testResource.setDescription("Accommodates 12 people");
        testResource.setPrice(BigDecimal.valueOf(50.00));
    }

    @Test
    void findAll_ReturnsListOfResources() {
        when(resourceRepository.findAll()).thenReturn(List.of(testResource));

        List<ResourceDto> result = resourceService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Conference Room A");
    }

    @Test
    void findById_Found_ReturnsResourceDto() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));

        ResourceDto result = resourceService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Conference Room A");
    }

    @Test
    void findById_NotFound_ThrowsResourceNotFoundException() {
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found: 999");
    }

    @Test
    void create_ValidDto_SavesAndReturnsDto() {
        ResourceDto input = new ResourceDto();
        input.setName("New Projector");
        input.setDescription("4K Projector");
        input.setPrice(BigDecimal.valueOf(25.00));

        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> {
            Resource r = invocation.getArgument(0);
            r.setId(2L);
            return r;
        });

        ResourceDto result = resourceService.create(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("New Projector");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(25.00));
    }

    @Test
    void update_Found_UpdatesAndReturnsDto() {
        ResourceDto updateDto = new ResourceDto();
        updateDto.setName("Updated Room A");
        updateDto.setDescription("Updated Description");
        updateDto.setPrice(BigDecimal.valueOf(75.00));

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));
        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResourceDto result = resourceService.update(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Room A");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(75.00));
    }

    @Test
    void update_NotFound_ThrowsResourceNotFoundException() {
        ResourceDto updateDto = new ResourceDto();
        updateDto.setName("Updated Room A");

        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.update(999L, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found: 999");
    }

    @Test
    void delete_Found_DeletesResource() {
        when(resourceRepository.existsById(1L)).thenReturn(true);
        doNothing().when(resourceRepository).deleteById(1L);

        resourceService.delete(1L);

        verify(resourceRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound_ThrowsResourceNotFoundException() {
        when(resourceRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> resourceService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found: 999");
    }
}

