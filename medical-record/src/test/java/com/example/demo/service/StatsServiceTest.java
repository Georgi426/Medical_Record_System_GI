package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.SickLeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StatsServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @InjectMocks
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMostCommonDiagnosis() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setName("Грип");

        when(appointmentRepository.findMostCommonDiagnosis()).thenReturn(diagnosis);

        Diagnosis result = statsService.getMostCommonDiagnosis();
        assertEquals("Грип", result.getName());
    }

    @Test
    void testGetMostCommonDiagnosisReturnsNull() {
        when(appointmentRepository.findMostCommonDiagnosis()).thenReturn(null);
        Diagnosis result = statsService.getMostCommonDiagnosis();
        assertNull(result);
    }

    @Test
    void testGetTotalPaidByPatients() {
        BigDecimal expectedSum = new BigDecimal("150.00");
        when(appointmentRepository.sumPaidByPatients()).thenReturn(expectedSum);

        BigDecimal result = statsService.getTotalPaidByPatients();
        assertEquals(expectedSum, result);
    }

    @Test
    void testGetTotalPaidByPatientsReturnsNull() {
        when(appointmentRepository.sumPaidByPatients()).thenReturn(null);
        BigDecimal result = statsService.getTotalPaidByPatients();
        assertNull(result);
    }

    @Test
    void testGetPatientsByDiagnosis() {
        Patient p1 = new Patient();
        p1.setName("Иван");
        Patient p2 = new Patient();
        p2.setName("Мария");

        when(appointmentRepository.findPatientsByDiagnosisId(1L)).thenReturn(Arrays.asList(p1, p2));

        List<Patient> result = statsService.getPatientsByDiagnosis(1L);
        assertEquals(2, result.size());
        assertEquals("Иван", result.get(0).getName());
    }

    @Test
    void testGetPatientsByGp() {
        Patient p1 = new Patient();
        p1.setName("Петър");

        when(patientRepository.findByGeneralPractitionerId(1L)).thenReturn(Arrays.asList(p1));

        List<Patient> result = statsService.getPatientsByGp(1L);
        assertEquals(1, result.size());
        assertEquals("Петър", result.get(0).getName());
    }

    @Test
    void testGetPaidByPatientsGroupedByDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("Д-р Иванов");

        Object[] row = new Object[]{doctor, new BigDecimal("200.00")};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        when(appointmentRepository.sumPaidByPatientsGroupedByDoctor()).thenReturn(rows);

        List<Map<String, Object>> result = statsService.getPaidByPatientsGroupedByDoctor();
        assertEquals(1, result.size());
        assertEquals("Д-р Иванов", result.get(0).get("doctor"));
        assertEquals(new BigDecimal("200.00"), result.get(0).get("sum"));
    }

    @Test
    void testGetPatientsCountByGp() {
        Doctor doctor = new Doctor();
        doctor.setName("Д-р Петрова");

        Object[] row = new Object[]{doctor, 5L};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        when(patientRepository.countPatientsByGp()).thenReturn(rows);

        List<Map<String, Object>> result = statsService.getPatientsCountByGp();
        assertEquals(1, result.size());
        assertEquals("Д-р Петрова", result.get(0).get("doctor"));
        assertEquals(5L, result.get(0).get("count"));
    }

    @Test
    void testGetAppointmentsCountByDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("Д-р Георгиев");

        Object[] row = new Object[]{doctor, 10L};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        when(appointmentRepository.countAppointmentsGroupedByDoctor()).thenReturn(rows);

        List<Map<String, Object>> result = statsService.getAppointmentsCountByDoctor();
        assertEquals(1, result.size());
        assertEquals("Д-р Георгиев", result.get(0).get("doctor"));
        assertEquals(10L, result.get(0).get("count"));
    }

    @Test
    void testGetMonthWithMostSickLeaves() {
        when(sickLeaveRepository.findMonthWithMostSickLeaves()).thenReturn(3);

        Integer result = statsService.getMonthWithMostSickLeaves();
        assertEquals(3, result);
    }

    @Test
    void testGetDoctorWithMostSickLeaves() {
        Doctor doctor = new Doctor();
        doctor.setName("Д-р Стоянов");

        when(sickLeaveRepository.findDoctorWithMostSickLeaves()).thenReturn(doctor);

        Doctor result = statsService.getDoctorWithMostSickLeaves();
        assertEquals("Д-р Стоянов", result.getName());
    }

    @Test
    void testGetPatientHistory() {
        Appointment a1 = new Appointment();
        a1.setDate(LocalDate.of(2026, 1, 15));
        Appointment a2 = new Appointment();
        a2.setDate(LocalDate.of(2026, 3, 20));

        when(appointmentRepository.findByPatientId(1L)).thenReturn(Arrays.asList(a1, a2));

        List<Appointment> result = statsService.getPatientHistory(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchAppointmentsByDoctorAndPeriod() {
        Appointment a1 = new Appointment();
        a1.setDate(LocalDate.of(2026, 2, 1));

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);

        when(appointmentRepository.findByDoctorIdAndDateBetween(1L, start, end)).thenReturn(List.of(a1));

        List<Appointment> result = statsService.searchAppointments(1L, start, end);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchAppointmentsByDoctorOnly() {
        Appointment a1 = new Appointment();

        when(appointmentRepository.findByDoctorId(1L)).thenReturn(List.of(a1));

        List<Appointment> result = statsService.searchAppointments(1L, null, null);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchAppointmentsByPeriodOnly() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);

        Appointment a1 = new Appointment();
        a1.setDate(LocalDate.of(2026, 3, 15));
        Appointment a2 = new Appointment();
        a2.setDate(LocalDate.of(2026, 8, 1));

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<Appointment> result = statsService.searchAppointments(null, start, end);
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 3, 15), result.get(0).getDate());
    }

    @Test
    void testSearchAppointmentsNoFilters() {
        Appointment a1 = new Appointment();
        Appointment a2 = new Appointment();

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<Appointment> result = statsService.searchAppointments(null, null, null);
        assertEquals(2, result.size());
    }
}
