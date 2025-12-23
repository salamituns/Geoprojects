package com.geoscience.sampleapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoscience.sampleapi.dto.SampleRequest;
import com.geoscience.sampleapi.dto.SampleResponse;
import com.geoscience.sampleapi.model.GeologicalSample;
import com.geoscience.sampleapi.service.SampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SampleController.class)
class SampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SampleService sampleService;

    private SampleRequest sampleRequest;
    private SampleResponse sampleResponse;
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

        sampleResponse = SampleResponse.builder()
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
    void testCreateSample() throws Exception {
        when(sampleService.createSample(any(SampleRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.sampleIdentifier").value("GS-2024-001"))
                .andExpect(jsonPath("$.sampleName").value("Granite Sample"));

        verify(sampleService, times(1)).createSample(any(SampleRequest.class));
    }

    @Test
    void testGetAllSamples() throws Exception {
        List<SampleResponse> samples = Arrays.asList(sampleResponse);
        Page<SampleResponse> samplePage = new PageImpl<>(samples, PageRequest.of(0, 20), 1);
        
        when(sampleService.getAllSamples(any())).thenReturn(samplePage);

        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(sampleService, times(1)).getAllSamples(any());
    }

    @Test
    void testGetSampleById() throws Exception {
        when(sampleService.getSampleById(testId)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/samples/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.sampleIdentifier").value("GS-2024-001"));

        verify(sampleService, times(1)).getSampleById(testId);
    }

    @Test
    void testUpdateSample() throws Exception {
        when(sampleService.updateSample(eq(testId), any(SampleRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/samples/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.sampleIdentifier").value("GS-2024-001"));

        verify(sampleService, times(1)).updateSample(eq(testId), any(SampleRequest.class));
    }

    @Test
    void testDeleteSample() throws Exception {
        doNothing().when(sampleService).deleteSample(testId);

        mockMvc.perform(delete("/api/v1/samples/" + testId))
                .andExpect(status().isNoContent());

        verify(sampleService, times(1)).deleteSample(testId);
    }

    @Test
    void testCreateSampleWithInvalidData() throws Exception {
        SampleRequest invalidRequest = SampleRequest.builder()
                .sampleIdentifier("")  // Invalid: empty identifier
                .sampleName("")        // Invalid: empty name
                .build();

        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(sampleService, never()).createSample(any());
    }
}

