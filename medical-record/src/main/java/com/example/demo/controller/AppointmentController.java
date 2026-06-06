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
    public List<Appointment> getAll(org.springframework.security.core.Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return appointmentRepository.findAll();
        }
        String username = authentication.getName();
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getUser().getUsername().equals(username))
                .toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public List<Appointment> getMyAppointments(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient().getUser().getUsername().equals(username))
                .toList();
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    public Appointment bookAppointment(@Valid @RequestBody com.example.demo.dto.PatientBookAppointmentDTO dto, org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        Patient patient = patientRepository.findAll().stream()
                .filter(p -> p.getUser() != null && p.getUser().getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Пациентът не е намерен"));

        if (dto.getDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Не можете да запазвате час за минала дата.");
        }

        boolean isBooked = appointmentRepository.findAll().stream()
                .anyMatch(a -> a.getDoctor().getId().equals(dto.getDoctorId()) && a.getDate().equals(dto.getDate()));
        if (isBooked) {
            throw new IllegalArgumentException("Този лекар вече има записан час за избраната дата.");
        }

        com.example.demo.model.Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Лекарят не е намерен"));

        Appointment appointment = new Appointment();
        appointment.setDate(dto.getDate());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnosis(null);
        appointment.setPrice(java.math.BigDecimal.ZERO);
        appointment.setTreatment("");
        appointment.setAdditionalInfo("");
        appointment.setPaidByNzok(patient.isInsured());
        appointment.setPaid(patient.isInsured());

        return appointmentRepository.save(appointment);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Appointment create(@Valid @RequestBody com.example.demo.dto.AppointmentDTO dto, org.springframework.security.core.Authentication authentication) {
        enforceDoctorOwnership(dto.getDoctor().getId(), authentication);
        Patient patient = patientRepository.findById(dto.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Невалиден пациент"));
        
        Appointment appointment = new Appointment();
        appointment.setDate(dto.getDate());
        appointment.setPrice(dto.getPrice() != null ? dto.getPrice() : java.math.BigDecimal.ZERO);
        
        com.example.demo.model.Doctor doctor = new com.example.demo.model.Doctor();
        doctor.setId(dto.getDoctor().getId());
        appointment.setDoctor(doctor);
        
        appointment.setPatient(patient);
        
        com.example.demo.model.Diagnosis diagnosis = new com.example.demo.model.Diagnosis();
        diagnosis.setId(dto.getDiagnosis().getId());
        appointment.setDiagnosis(diagnosis);
        
        appointment.setPaidByNzok(patient.isInsured());
        appointment.setPaid(patient.isInsured() || appointment.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0);
        return appointmentRepository.save(appointment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Appointment update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.AppointmentDTO dto, org.springframework.security.core.Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        enforceDoctorOwnership(dto.getDoctor().getId(), authentication);
        
        Patient patient = patientRepository.findById(dto.getPatient().getId()).orElseThrow();
        
        appointment.setDate(dto.getDate());
        
        com.example.demo.model.Doctor doctor = new com.example.demo.model.Doctor();
        doctor.setId(dto.getDoctor().getId());
        appointment.setDoctor(doctor);
        
        appointment.setPatient(patient);
        
        com.example.demo.model.Diagnosis diagnosis = new com.example.demo.model.Diagnosis();
        diagnosis.setId(dto.getDiagnosis().getId());
        appointment.setDiagnosis(diagnosis);
        appointment.setTreatment(dto.getTreatment());
        appointment.setAdditionalInfo(dto.getAdditionalInfo());
        appointment.setPrice(dto.getPrice() != null ? dto.getPrice() : java.math.BigDecimal.ZERO);
        appointment.setPaidByNzok(patient.isInsured());
        appointment.setPaid(patient.isInsured() || appointment.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0);
        
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

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('PATIENT')")
    public Appointment pay(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();
        String username = authentication.getName();
        if (!appointment.getPatient().getUser().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Можете да плащате само свои прегледи.");
        }
        if (appointment.isPaidByNzok() || appointment.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Този преглед е безплатен или покрит от НЗОК.");
        }
        if (appointment.isPaid()) {
            throw new IllegalArgumentException("Този преглед вече е платен.");
        }
        appointment.setPaid(true);
        return appointmentRepository.save(appointment);
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
