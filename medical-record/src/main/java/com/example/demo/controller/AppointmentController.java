package com.example.demo.controller;

import com.example.demo.model.Appointment;
import com.example.demo.model.Patient;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final com.example.demo.repository.DoctorRepository doctorRepository;

    public AppointmentController(AppointmentRepository appointmentRepository, PatientRepository patientRepository, com.example.demo.repository.DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public List<Appointment> getMyAppointments(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient().getUser().getUsername().equals(username))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Appointment create(@Valid @RequestBody Appointment appointment, org.springframework.security.core.Authentication authentication) {
        enforceDoctorOwnership(appointment.getDoctor().getId(), authentication);
        Patient patient = patientRepository.findById(appointment.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Невалиден пациент"));
        
        appointment.setPaidByNzok(patient.isInsured());
        return appointmentRepository.save(appointment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Appointment update(@PathVariable Long id, @Valid @RequestBody Appointment appointmentDetails, org.springframework.security.core.Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        enforceDoctorOwnership(appointment.getDoctor().getId(), authentication);
        
        Patient patient = patientRepository.findById(appointmentDetails.getPatient().getId()).orElseThrow();
        
        appointment.setDate(appointmentDetails.getDate());
        appointment.setDoctor(appointmentDetails.getDoctor());
        appointment.setPatient(patient);
        appointment.setDiagnosis(appointmentDetails.getDiagnosis());
        appointment.setTreatment(appointmentDetails.getTreatment());
        appointment.setPrice(appointmentDetails.getPrice());
        appointment.setPaidByNzok(patient.isInsured());
        
        return appointmentRepository.save(appointment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        enforceDoctorOwnership(appointment.getDoctor().getId(), authentication);
        appointmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void enforceDoctorOwnership(Long doctorId, org.springframework.security.core.Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return;
        }
        String username = authentication.getName();
        com.example.demo.model.Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        if (!doctor.getUser().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Можете да редактирате само свои прегледи.");
        }
    }
}
