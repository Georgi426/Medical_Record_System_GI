package com.example.demo.controller;

import com.example.demo.model.Diagnosis;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.service.StatsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/patients-by-diagnosis/{diagnosisId}")
    public List<Patient> getPatientsByDiagnosis(@PathVariable Long diagnosisId) {
        return statsService.getPatientsByDiagnosis(diagnosisId);
    }

    @GetMapping("/most-common-diagnosis")
    public Diagnosis getMostCommonDiagnosis() {
        return statsService.getMostCommonDiagnosis();
    }

    @GetMapping("/patients-by-gp/{gpId}")
    public List<Patient> getPatientsByGp(@PathVariable Long gpId) {
        return statsService.getPatientsByGp(gpId);
    }

    @GetMapping("/total-paid-by-patients")
    public BigDecimal getTotalPaidByPatients() {
        return statsService.getTotalPaidByPatients();
    }

    @GetMapping("/paid-by-patients-grouped-by-doctor")
    public List<Map<String, Object>> getPaidByPatientsGroupedByDoctor() {
        return statsService.getPaidByPatientsGroupedByDoctor();
    }

    @GetMapping("/patients-count-by-gp")
    public List<Map<String, Object>> getPatientsCountByGp() {
        return statsService.getPatientsCountByGp();
    }

    @GetMapping("/appointments-count-by-doctor")
    public List<Map<String, Object>> getAppointmentsCountByDoctor() {
        return statsService.getAppointmentsCountByDoctor();
    }

    @GetMapping("/month-most-sick-leaves")
    public Integer getMonthWithMostSickLeaves() {
        return statsService.getMonthWithMostSickLeaves();
    }

    @GetMapping("/doctor-most-sick-leaves")
    public Doctor getDoctorWithMostSickLeaves() {
        return statsService.getDoctorWithMostSickLeaves();
    }

    @GetMapping("/patient-history/{patientId}")
    public List<com.example.demo.model.Appointment> getPatientHistory(@PathVariable Long patientId) {
        return statsService.getPatientHistory(patientId);
    }

    @GetMapping("/appointments-search")
    public List<com.example.demo.model.Appointment> searchAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        return statsService.searchAppointments(doctorId, startDate, endDate);
    }
}
