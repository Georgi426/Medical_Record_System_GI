package com.example.demo.controller;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientControllerIntegrationTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Patient p1 = new Patient();
        p1.setName("Иван");
        when(patientRepository.findAll()).thenReturn(Arrays.asList(p1));

        List<Patient> result = patientController.getAll();
        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getName());
    }

    @Test
    void testCreate() {
        Patient p = new Patient();
        p.setName("Мария");

        com.example.demo.dto.PatientDTO dto = new com.example.demo.dto.PatientDTO();
        dto.setName("Мария");

        when(patientRepository.save(any(Patient.class))).thenReturn(p);

        Patient result = patientController.create(dto);
        assertNotNull(result);
        assertEquals("Мария", result.getName());
    }

    @Test
    void testDelete() {
        doNothing().when(patientRepository).deleteById(1L);

        ResponseEntity<Void> result = patientController.delete(1L);
        assertEquals(200, result.getStatusCode().value());
        verify(patientRepository, times(1)).deleteById(1L);
    }
}
