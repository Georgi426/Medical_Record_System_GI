package com.example.demo.controller;

import com.example.demo.model.Treatment;
import com.example.demo.repository.TreatmentRepository;
import com.example.demo.repository.MedicalRecordRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {

    private final TreatmentRepository treatmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public TreatmentController(TreatmentRepository treatmentRepository, MedicalRecordRepository medicalRecordRepository) {
        this.treatmentRepository = treatmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<Treatment> getAll() {
        return treatmentRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Treatment create(@Valid @RequestBody com.example.demo.dto.TreatmentDTO dto) {
        Treatment treatment = new Treatment();
        treatment.setMedicalRecord(medicalRecordRepository.findById(dto.getMedicalRecord().getId()).orElseThrow());
        treatment.setDescription(dto.getDescription());
        treatment.setMedication(dto.getMedication());
        treatment.setDosage(dto.getDosage());
        return treatmentRepository.save(treatment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Treatment update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.TreatmentDTO dto) {
        Treatment treatment = treatmentRepository.findById(id).orElseThrow();
        treatment.setMedicalRecord(medicalRecordRepository.findById(dto.getMedicalRecord().getId()).orElseThrow());
        treatment.setDescription(dto.getDescription());
        treatment.setMedication(dto.getMedication());
        treatment.setDosage(dto.getDosage());
        return treatmentRepository.save(treatment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        treatmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
