package com.example.demo.controller;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Patient create(@Valid @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient patientDetails) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.setName(patientDetails.getName());
        patient.setEgn(patientDetails.getEgn());
        patient.setInsured(patientDetails.isInsured());
        patient.setGeneralPractitioner(patientDetails.getGeneralPractitioner());
        return patientRepository.save(patient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
