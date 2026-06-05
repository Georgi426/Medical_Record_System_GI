package com.example.demo.controller;

import com.example.demo.model.HealthInsurance;
import com.example.demo.repository.HealthInsuranceRepository;
import com.example.demo.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-insurances")
public class HealthInsuranceController {

    private final HealthInsuranceRepository healthInsuranceRepository;
    private final PatientRepository patientRepository;

    public HealthInsuranceController(HealthInsuranceRepository healthInsuranceRepository, PatientRepository patientRepository) {
        this.healthInsuranceRepository = healthInsuranceRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<HealthInsurance> getAll() {
        return healthInsuranceRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HealthInsurance create(@Valid @RequestBody com.example.demo.dto.HealthInsuranceDTO dto) {
        HealthInsurance insurance = new HealthInsurance();
        insurance.setPatient(patientRepository.findById(dto.getPatient().getId()).orElseThrow());
        insurance.setProvider(dto.getProvider());
        insurance.setPolicyNumber(dto.getPolicyNumber());
        insurance.setValidUntil(dto.getValidUntil());
        return healthInsuranceRepository.save(insurance);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HealthInsurance update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.HealthInsuranceDTO dto) {
        HealthInsurance insurance = healthInsuranceRepository.findById(id).orElseThrow();
        insurance.setPatient(patientRepository.findById(dto.getPatient().getId()).orElseThrow());
        insurance.setProvider(dto.getProvider());
        insurance.setPolicyNumber(dto.getPolicyNumber());
        insurance.setValidUntil(dto.getValidUntil());
        return healthInsuranceRepository.save(insurance);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        healthInsuranceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
