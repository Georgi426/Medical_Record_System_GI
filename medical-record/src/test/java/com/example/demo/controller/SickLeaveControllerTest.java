package com.example.demo.controller;

import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.model.SickLeave;
import com.example.demo.model.User;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.SickLeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SickLeaveControllerTest {

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private SickLeaveController sickLeaveController;

    private Authentication adminAuth;
    private Authentication doctorAuth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminAuth = mock(Authentication.class);
        Collection adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(adminAuth.getAuthorities()).thenReturn(adminAuthorities);
        when(adminAuth.getName()).thenReturn("admin");

        doctorAuth = mock(Authentication.class);
        Collection doctorAuthorities = List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"));
        when(doctorAuth.getAuthorities()).thenReturn(doctorAuthorities);
        when(doctorAuth.getName()).thenReturn("doctor1");
    }

    @Test
    void testGetAll() {
        SickLeave sl = new SickLeave();
        sl.setStartDate(LocalDate.of(2026, 3, 1));
        sl.setDurationDays(7);

        when(sickLeaveRepository.findAll()).thenReturn(Arrays.asList(sl));

        List<SickLeave> result = sickLeaveController.getAll();
        assertEquals(1, result.size());
        assertEquals(7, result.get(0).getDurationDays());
    }

    @Test
    void testCreateAsAdmin() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        SickLeave sl = new SickLeave();
        sl.setStartDate(LocalDate.of(2026, 4, 1));
        sl.setDurationDays(5);
        sl.setDoctor(doctor);

        com.example.demo.dto.SickLeaveDTO dto = new com.example.demo.dto.SickLeaveDTO();
        dto.setStartDate(LocalDate.of(2026, 4, 1));
        dto.setDurationDays(5);
        com.example.demo.dto.EntityReference doctorRef = new com.example.demo.dto.EntityReference();
        doctorRef.setId(1L);
        dto.setDoctor(doctorRef);
        com.example.demo.dto.EntityReference patientRef = new com.example.demo.dto.EntityReference();
        patientRef.setId(1L);
        dto.setPatient(patientRef);

        when(sickLeaveRepository.save(any(SickLeave.class))).thenReturn(sl);

        SickLeave result = sickLeaveController.create(dto, adminAuth);
        assertNotNull(result);
        assertEquals(5, result.getDurationDays());
    }

    @Test
    void testCreateAsDoctorOwner() {
        User user = new User();
        user.setUsername("doctor1");

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(user);

        SickLeave sl = new SickLeave();
        sl.setStartDate(LocalDate.of(2026, 4, 1));
        sl.setDurationDays(10);
        sl.setDoctor(doctor);

        com.example.demo.dto.SickLeaveDTO dto = new com.example.demo.dto.SickLeaveDTO();
        dto.setStartDate(LocalDate.of(2026, 4, 1));
        dto.setDurationDays(10);
        com.example.demo.dto.EntityReference doctorRef = new com.example.demo.dto.EntityReference();
        doctorRef.setId(1L);
        dto.setDoctor(doctorRef);
        com.example.demo.dto.EntityReference patientRef = new com.example.demo.dto.EntityReference();
        patientRef.setId(1L);
        dto.setPatient(patientRef);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(sickLeaveRepository.save(any(SickLeave.class))).thenReturn(sl);

        SickLeave result = sickLeaveController.create(dto, doctorAuth);
        assertNotNull(result);
        assertEquals(10, result.getDurationDays());
    }

    @Test
    void testCreateAsDoctorNotOwnerThrows() {
        User user = new User();
        user.setUsername("doctor2");

        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setUser(user);

        com.example.demo.dto.SickLeaveDTO dto = new com.example.demo.dto.SickLeaveDTO();
        com.example.demo.dto.EntityReference doctorRef = new com.example.demo.dto.EntityReference();
        doctorRef.setId(2L);
        dto.setDoctor(doctorRef);

        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));

        assertThrows(AccessDeniedException.class, () -> sickLeaveController.create(dto, doctorAuth));
    }

    @Test
    void testDelete() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        SickLeave existing = new SickLeave();
        existing.setId(1L);
        existing.setDoctor(doctor);

        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(sickLeaveRepository).deleteById(1L);

        ResponseEntity<Void> result = sickLeaveController.delete(1L, adminAuth);
        assertEquals(200, result.getStatusCode().value());
        verify(sickLeaveRepository, times(1)).deleteById(1L);
    }
}
