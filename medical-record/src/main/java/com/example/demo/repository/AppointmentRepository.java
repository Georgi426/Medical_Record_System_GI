package com.example.demo.repository;

import com.example.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.diagnosis.id = :diagnosisId")
    List<com.example.demo.model.Patient> findPatientsByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    @Query("SELECT a.diagnosis FROM Appointment a GROUP BY a.diagnosis ORDER BY COUNT(a.id) DESC LIMIT 1")
    com.example.demo.model.Diagnosis findMostCommonDiagnosis();

    @Query("SELECT SUM(a.price) FROM Appointment a WHERE a.paidByNzok = false")
    java.math.BigDecimal sumPaidByPatients();

    @Query("SELECT a.doctor, SUM(a.price) FROM Appointment a WHERE a.paidByNzok = false GROUP BY a.doctor")
    List<Object[]> sumPaidByPatientsGroupedByDoctor();

    @Query("SELECT a.doctor, COUNT(a.id) FROM Appointment a GROUP BY a.doctor")
    List<Object[]> countAppointmentsGroupedByDoctor();

    List<Appointment> findByDoctorIdAndDateBetween(Long doctorId, LocalDate start, LocalDate end);
}
