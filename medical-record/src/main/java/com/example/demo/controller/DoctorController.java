package com.example.demo.controller;

import com.example.demo.model.Doctor;
import com.example.demo.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Doctor create(@Valid @RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Doctor update(@PathVariable Long id, @Valid @RequestBody Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow();
        doctor.setName(doctorDetails.getName());
        doctor.setSpecialty(doctorDetails.getSpecialty());
        doctor.setUin(doctorDetails.getUin());
        doctor.setGeneralPractitioner(doctorDetails.isGeneralPractitioner());
        return doctorRepository.save(doctor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
