package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentController appointmentController;

    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminAuth = mock(Authentication.class);
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(adminAuth.getAuthorities()).thenReturn(authorities);
        when(adminAuth.getName()).thenReturn("admin");
    }

    @Test
    void testGetAll() {
        Appointment a1 = new Appointment();
        a1.setDate(LocalDate.of(2026, 1, 1));

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(a1));

        List<Appointment> result = appointmentController.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void testCreate() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Д-р Иванов");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Иван");
        patient.setInsured(true);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setName("Грип");

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.of(2026, 5, 1));
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnosis(diagnosis);
        appointment.setTreatment("Лечение");
        appointment.setPrice(new BigDecimal("50.00"));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentController.create(appointment, adminAuth);
        assertNotNull(result);
        assertTrue(result.isPaidByNzok());
        assertEquals("Лечение", result.getTreatment());
    }

    @Test
    void testUpdate() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setInsured(false);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(1L);

        Appointment existing = new Appointment();
        existing.setId(1L);
        existing.setDoctor(doctor);
        existing.setPatient(patient);
        existing.setDate(LocalDate.of(2026, 1, 1));

        Appointment updated = new Appointment();
        updated.setDate(LocalDate.of(2026, 6, 1));
        updated.setDoctor(doctor);
        updated.setPatient(patient);
        updated.setDiagnosis(diagnosis);
        updated.setTreatment("Ново лечение");
        updated.setPrice(new BigDecimal("100.00"));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existing);

        Appointment result = appointmentController.update(1L, updated, adminAuth);
        assertEquals(LocalDate.of(2026, 6, 1), result.getDate());
        assertEquals("Ново лечение", result.getTreatment());
    }

    @Test
    void testDelete() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Appointment existing = new Appointment();
        existing.setId(1L);
        existing.setDoctor(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(appointmentRepository).deleteById(1L);

        ResponseEntity<Void> result = appointmentController.delete(1L, adminAuth);
        assertEquals(200, result.getStatusCode().value());
        verify(appointmentRepository, times(1)).deleteById(1L);
    }
}
