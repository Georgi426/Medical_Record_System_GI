package com.example.demo.controller;

import com.example.demo.model.MedicalRecord;
import com.example.demo.repository.MedicalRecordRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public MedicalRecordController(MedicalRecordRepository medicalRecordRepository, 
                                   PatientRepository patientRepository,
                                   DoctorRepository doctorRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<MedicalRecord> getAll() {
        return medicalRecordRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public MedicalRecord create(@Valid @RequestBody com.example.demo.dto.MedicalRecordDTO dto) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patientRepository.findById(dto.getPatient().getId()).orElseThrow());
        record.setDoctor(doctorRepository.findById(dto.getDoctor().getId()).orElseThrow());
        record.setVisitDate(dto.getVisitDate());
        record.setComplaints(dto.getComplaints());
        return medicalRecordRepository.save(record);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MedicalRecord update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.MedicalRecordDTO dto) {
        MedicalRecord record = medicalRecordRepository.findById(id).orElseThrow();
        record.setPatient(patientRepository.findById(dto.getPatient().getId()).orElseThrow());
        record.setDoctor(doctorRepository.findById(dto.getDoctor().getId()).orElseThrow());
        record.setVisitDate(dto.getVisitDate());
        record.setComplaints(dto.getComplaints());
        return medicalRecordRepository.save(record);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalRecordRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
