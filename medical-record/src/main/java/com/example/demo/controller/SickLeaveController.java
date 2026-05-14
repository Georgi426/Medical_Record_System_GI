package com.example.demo.controller;

import com.example.demo.model.Doctor;
import com.example.demo.model.SickLeave;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.SickLeaveRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sick-leaves")
public class SickLeaveController {

    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorRepository doctorRepository;

    public SickLeaveController(SickLeaveRepository sickLeaveRepository, DoctorRepository doctorRepository) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<SickLeave> getAll() {
        return sickLeaveRepository.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public List<SickLeave> getMySickLeaves(Authentication authentication) {
        String username = authentication.getName();
        return sickLeaveRepository.findAll().stream()
                .filter(s -> s.getPatient().getUser().getUsername().equals(username))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public SickLeave create(@Valid @RequestBody com.example.demo.dto.SickLeaveDTO dto, Authentication authentication) {
        enforceDoctorOwnership(dto.getDoctor().getId(), authentication);
        SickLeave sickLeave = new SickLeave();
        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDurationDays(dto.getDurationDays());

        com.example.demo.model.Patient patient = new com.example.demo.model.Patient();
        patient.setId(dto.getPatient().getId());
        sickLeave.setPatient(patient);

        com.example.demo.model.Doctor doctor = new com.example.demo.model.Doctor();
        doctor.setId(dto.getDoctor().getId());
        sickLeave.setDoctor(doctor);

        return sickLeaveRepository.save(sickLeave);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public SickLeave update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.SickLeaveDTO dto, Authentication authentication) {
        SickLeave sickLeave = sickLeaveRepository.findById(id).orElseThrow();
        enforceDoctorOwnership(dto.getDoctor().getId(), authentication);

        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDurationDays(dto.getDurationDays());

        com.example.demo.model.Patient patient = new com.example.demo.model.Patient();
        patient.setId(dto.getPatient().getId());
        sickLeave.setPatient(patient);

        com.example.demo.model.Doctor doctor = new com.example.demo.model.Doctor();
        doctor.setId(dto.getDoctor().getId());
        sickLeave.setDoctor(doctor);

        return sickLeaveRepository.save(sickLeave);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        SickLeave sickLeave = sickLeaveRepository.findById(id).orElseThrow();
        enforceDoctorOwnership(sickLeave.getDoctor().getId(), authentication);
        sickLeaveRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private void enforceDoctorOwnership(Long doctorId, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return;
        }
        String username = authentication.getName();
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        if (!doctor.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Можете да редактирате само свои болнични листове.");
        }
    }
}
