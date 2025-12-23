package com.geoscience.sampleapi.service;

import com.geoscience.sampleapi.dto.SampleRequest;
import com.geoscience.sampleapi.dto.SampleResponse;
import com.geoscience.sampleapi.exception.SampleNotFoundException;
import com.geoscience.sampleapi.model.GeologicalSample;
import com.geoscience.sampleapi.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository sampleRepository;

    @InjectMocks
    private SampleService sampleService;

    private SampleRequest sampleRequest;
    private GeologicalSample sampleEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        
        sampleRequest = SampleRequest.builder()
                .sampleIdentifier("GS-2024-001")
                .sampleName("Granite Sample")
                .sampleType(GeologicalSample.SampleType.ROCK)
                .collectionDate(LocalDate.of(2024, 1, 15))
                .latitude(40.7128)
                .longitude(-74.0060)
                .locationName("Central Park")
                .collectorName("Dr. Jane Smith")
                .description("Fine-grained granite sample")
                .storageLocation("Lab-A-Shelf-12")
                .build();

        sampleEntity = GeologicalSample.builder()
                .id(testId)
                .sampleIdentifier("GS-2024-001")
                .sampleName("Granite Sample")
                .sampleType(GeologicalSample.SampleType.ROCK)
                .collectionDate(LocalDate.of(2024, 1, 15))
                .latitude(40.7128)
                .longitude(-74.0060)
                .locationName("Central Park")
                .collectorName("Dr. Jane Smith")
                .description("Fine-grained granite sample")
                .storageLocation("Lab-A-Shelf-12")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateSample_Success() {
        when(sampleRepository.existsBySampleIdentifier("GS-2024-001")).thenReturn(false);
        when(sampleRepository.save(any(GeologicalSample.class))).thenReturn(sampleEntity);

        SampleResponse response = sampleService.createSample(sampleRequest);

        assertNotNull(response);
        assertEquals("GS-2024-001", response.getSampleIdentifier());
        assertEquals("Granite Sample", response.getSampleName());
        verify(sampleRepository, times(1)).existsBySampleIdentifier("GS-2024-001");
        verify(sampleRepository, times(1)).save(any(GeologicalSample.class));
    }

    @Test
    void testCreateSample_DuplicateIdentifier() {
        when(sampleRepository.existsBySampleIdentifier("GS-2024-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            sampleService.createSample(sampleRequest);
        });

        verify(sampleRepository, times(1)).existsBySampleIdentifier("GS-2024-001");
        verify(sampleRepository, never()).save(any());
    }

    @Test
    void testGetAllSamples_Success() {
        List<GeologicalSample> samples = Arrays.asList(sampleEntity);
        Page<GeologicalSample> samplePage = new PageImpl<>(samples, PageRequest.of(0, 20), 1);
        
        when(sampleRepository.findAll(any(PageRequest.class))).thenReturn(samplePage);

        Page<SampleResponse> response = sampleService.getAllSamples(PageRequest.of(0, 20));

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("GS-2024-001", response.getContent().get(0).getSampleIdentifier());
        verify(sampleRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetSampleById_Success() {
        when(sampleRepository.findById(testId)).thenReturn(Optional.of(sampleEntity));

        SampleResponse response = sampleService.getSampleById(testId);

        assertNotNull(response);
        assertEquals(testId, response.getId());
        assertEquals("GS-2024-001", response.getSampleIdentifier());
        verify(sampleRepository, times(1)).findById(testId);
    }

    @Test
    void testGetSampleById_NotFound() {
        when(sampleRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(SampleNotFoundException.class, () -> {
            sampleService.getSampleById(testId);
        });

        verify(sampleRepository, times(1)).findById(testId);
    }

    @Test
    void testUpdateSample_Success() {
        // Same identifier - existsBySampleIdentifier check is skipped
        when(sampleRepository.findById(testId)).thenReturn(Optional.of(sampleEntity));
        when(sampleRepository.save(any(GeologicalSample.class))).thenReturn(sampleEntity);

        SampleResponse response = sampleService.updateSample(testId, sampleRequest);

        assertNotNull(response);
        assertEquals(testId, response.getId());
        verify(sampleRepository, times(1)).findById(testId);
        verify(sampleRepository, times(1)).save(any(GeologicalSample.class));
    }

    @Test
    void testUpdateSample_NotFound() {
        when(sampleRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(SampleNotFoundException.class, () -> {
            sampleService.updateSample(testId, sampleRequest);
        });

        verify(sampleRepository, times(1)).findById(testId);
        verify(sampleRepository, never()).save(any());
    }

    @Test
    void testDeleteSample_Success() {
        when(sampleRepository.existsById(testId)).thenReturn(true);
        doNothing().when(sampleRepository).deleteById(testId);

        sampleService.deleteSample(testId);

        verify(sampleRepository, times(1)).existsById(testId);
        verify(sampleRepository, times(1)).deleteById(testId);
    }

    @Test
    void testDeleteSample_NotFound() {
        when(sampleRepository.existsById(testId)).thenReturn(false);

        assertThrows(SampleNotFoundException.class, () -> {
            sampleService.deleteSample(testId);
        });

        verify(sampleRepository, times(1)).existsById(testId);
        verify(sampleRepository, never()).deleteById(any());
    }
}

