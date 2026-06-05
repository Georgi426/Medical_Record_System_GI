package com.example.demo.repository;

import com.example.demo.model.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthInsuranceRepository extends JpaRepository<HealthInsurance, Long> {
    List<HealthInsurance> findByPatientId(Long patientId);
}
