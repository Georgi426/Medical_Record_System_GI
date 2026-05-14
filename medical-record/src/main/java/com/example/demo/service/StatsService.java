package com.example.demo.service;

import com.example.demo.model.Diagnosis;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.SickLeaveRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public StatsService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, SickLeaveRepository sickLeaveRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.sickLeaveRepository = sickLeaveRepository;
    }

    public List<Patient> getPatientsByDiagnosis(Long diagnosisId) {
        return appointmentRepository.findPatientsByDiagnosisId(diagnosisId);
    }

    public Diagnosis getMostCommonDiagnosis() {
        return appointmentRepository.findMostCommonDiagnosis();
    }

    public List<Patient> getPatientsByGp(Long gpId) {
        return patientRepository.findByGeneralPractitionerId(gpId);
    }

    public BigDecimal getTotalPaidByPatients() {
        return appointmentRepository.sumPaidByPatients();
    }

    public List<Map<String, Object>> getPaidByPatientsGroupedByDoctor() {
        return appointmentRepository.sumPaidByPatientsGroupedByDoctor().stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("doctor", ((Doctor)obj[0]).getName());
            map.put("sum", obj[1]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPatientsCountByGp() {
        return patientRepository.countPatientsByGp().stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("doctor", ((Doctor)obj[0]).getName());
            map.put("count", obj[1]);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAppointmentsCountByDoctor() {
        return appointmentRepository.countAppointmentsGroupedByDoctor().stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("doctor", ((Doctor)obj[0]).getName());
            map.put("count", obj[1]);
            return map;
        }).collect(Collectors.toList());
    }

    public Integer getMonthWithMostSickLeaves() {
        return sickLeaveRepository.findMonthWithMostSickLeaves();
    }

    public Doctor getDoctorWithMostSickLeaves() {
        return sickLeaveRepository.findDoctorWithMostSickLeaves();
    }

    public List<com.example.demo.model.Appointment> getPatientHistory(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<com.example.demo.model.Appointment> searchAppointments(Long doctorId, LocalDate startDate, LocalDate endDate) {
        if (doctorId != null && startDate != null && endDate != null) {
            return appointmentRepository.findByDoctorIdAndDateBetween(doctorId, startDate, endDate);
        } else if (doctorId != null) {
            return appointmentRepository.findByDoctorId(doctorId);
        } else if (startDate != null && endDate != null) {
            return appointmentRepository.findAll().stream()
                    .filter(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        return appointmentRepository.findAll();
    }
}
