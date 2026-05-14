package com.example.demo.controller;

import com.example.demo.model.Diagnosis;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DiagnosisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiagnosisControllerTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private DiagnosisController diagnosisController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Diagnosis d1 = new Diagnosis();
        d1.setName("Грип");
        Diagnosis d2 = new Diagnosis();
        d2.setName("Ангина");

        when(diagnosisRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Diagnosis> result = diagnosisController.getAll();
        assertEquals(2, result.size());
        assertEquals("Грип", result.get(0).getName());
    }

    @Test
    void testCreate() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName("Бронхит");
        diagnosis.setDescription("Възпаление на бронхите");

        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(diagnosis);

        Diagnosis result = diagnosisController.create(diagnosis);
        assertNotNull(result);
        assertEquals("Бронхит", result.getName());
        assertEquals("Възпаление на бронхите", result.getDescription());
    }

    @Test
    void testUpdate() {
        Diagnosis existing = new Diagnosis();
        existing.setId(1L);
        existing.setName("Грип");
        existing.setDescription("Сезонен грип");

        Diagnosis updated = new Diagnosis();
        updated.setName("Грип A");
        updated.setDescription("Грип тип A");

        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(existing);

        Diagnosis result = diagnosisController.update(1L, updated);
        assertEquals("Грип A", result.getName());
        assertEquals("Грип тип A", result.getDescription());
    }

    @Test
    void testDelete() {
        doNothing().when(diagnosisRepository).deleteById(1L);

        ResponseEntity<Void> result = diagnosisController.delete(1L);
        assertEquals(200, result.getStatusCode().value());
        verify(diagnosisRepository, times(1)).deleteById(1L);
    }
}
