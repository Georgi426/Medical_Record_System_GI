package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserId(Long userId);

    java.util.List<Patient> findByGeneralPractitionerId(Long gpId);

    @org.springframework.data.jpa.repository.Query("SELECT p.generalPractitioner, COUNT(p.id) FROM Patient p WHERE p.generalPractitioner IS NOT NULL GROUP BY p.generalPractitioner")
    java.util.List<Object[]> countPatientsByGp();
}
