package com.example.demo.controller;

import com.example.demo.model.Diagnosis;
import com.example.demo.repository.DiagnosisRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {

    private final DiagnosisRepository diagnosisRepository;
    private final com.example.demo.repository.AppointmentRepository appointmentRepository;

    public DiagnosisController(DiagnosisRepository diagnosisRepository, com.example.demo.repository.AppointmentRepository appointmentRepository) {
        this.diagnosisRepository = diagnosisRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public List<Diagnosis> getAll() {
        return diagnosisRepository.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public List<Diagnosis> getMyDiagnoses(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient().getUser().getUsername().equals(username))
                .map(com.example.demo.model.Appointment::getDiagnosis)
                .distinct()
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Diagnosis create(@Valid @RequestBody com.example.demo.dto.DiagnosisDTO dto) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName(dto.getName());
        diagnosis.setDescription(dto.getDescription());
        return diagnosisRepository.save(diagnosis);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Diagnosis update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.DiagnosisDTO dto) {
        Diagnosis diagnosis = diagnosisRepository.findById(id).orElseThrow();
        diagnosis.setName(dto.getName());
        diagnosis.setDescription(dto.getDescription());
        return diagnosisRepository.save(diagnosis);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diagnosisRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
