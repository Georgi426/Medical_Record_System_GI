package com.example.demo.controller;

import com.example.demo.model.Doctor;
import com.example.demo.repository.DoctorRepository;
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

class DoctorControllerTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorController doctorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Doctor d1 = new Doctor();
        d1.setName("Д-р Иванов");
        d1.setSpecialty("Кардиология");
        Doctor d2 = new Doctor();
        d2.setName("Д-р Петрова");
        d2.setSpecialty("Дерматология");

        when(doctorRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Doctor> result = doctorController.getAll();
        assertEquals(2, result.size());
        assertEquals("Д-р Иванов", result.get(0).getName());
        assertEquals("Д-р Петрова", result.get(1).getName());
    }

    @Test
    void testCreate() {
        Doctor doctor = new Doctor();
        doctor.setName("Д-р Георгиев");
        doctor.setUin("1234567890");
        doctor.setSpecialty("Хирургия");
        doctor.setGeneralPractitioner(false);

        com.example.demo.dto.DoctorDTO dto = new com.example.demo.dto.DoctorDTO();
        dto.setName("Д-р Георгиев");
        dto.setUin("1234567890");
        dto.setSpecialty("Хирургия");
        dto.setGeneralPractitioner(false);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorController.create(dto);
        assertNotNull(result);
        assertEquals("Д-р Георгиев", result.getName());
        assertEquals("Хирургия", result.getSpecialty());
    }

    @Test
    void testUpdate() {
        Doctor existing = new Doctor();
        existing.setId(1L);
        existing.setName("Д-р Иванов");
        existing.setSpecialty("Кардиология");
        existing.setUin("1234567890");
        existing.setGeneralPractitioner(false);

        com.example.demo.dto.DoctorDTO updatedDto = new com.example.demo.dto.DoctorDTO();
        updatedDto.setName("Д-р Иванов-Updated");
        updatedDto.setSpecialty("Неврология");
        updatedDto.setUin("0987654321");
        updatedDto.setGeneralPractitioner(true);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(existing);

        Doctor result = doctorController.update(1L, updatedDto);
        assertEquals("Д-р Иванов-Updated", result.getName());
        assertEquals("Неврология", result.getSpecialty());
        assertTrue(result.isGeneralPractitioner());
    }

    @Test
    void testDelete() {
        doNothing().when(doctorRepository).deleteById(1L);

        ResponseEntity<Void> result = doctorController.delete(1L);
        assertEquals(200, result.getStatusCode().value());
        verify(doctorRepository, times(1)).deleteById(1L);
    }
}
