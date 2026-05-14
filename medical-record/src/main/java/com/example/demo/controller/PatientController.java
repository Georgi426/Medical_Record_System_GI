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
    public Patient create(@Valid @RequestBody com.example.demo.dto.PatientDTO dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEgn(dto.getEgn());
        patient.setInsured(dto.isInsured());
        if (dto.getGeneralPractitioner() != null && dto.getGeneralPractitioner().getId() != null) {
            com.example.demo.model.Doctor gp = new com.example.demo.model.Doctor();
            gp.setId(dto.getGeneralPractitioner().getId());
            patient.setGeneralPractitioner(gp);
        }
        return patientRepository.save(patient);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Patient update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.PatientDTO dto) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.setName(dto.getName());
        patient.setEgn(dto.getEgn());
        patient.setInsured(dto.isInsured());
        if (dto.getGeneralPractitioner() != null && dto.getGeneralPractitioner().getId() != null) {
            com.example.demo.model.Doctor gp = new com.example.demo.model.Doctor();
            gp.setId(dto.getGeneralPractitioner().getId());
            patient.setGeneralPractitioner(gp);
        } else {
            patient.setGeneralPractitioner(null);
        }
        return patientRepository.save(patient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
