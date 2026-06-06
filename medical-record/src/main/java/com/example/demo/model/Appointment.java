package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = true)
    @JoinColumn(name = "diagnosis_id", nullable = true)
    private Diagnosis diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String additionalInfo;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean paidByNzok;

    @Column(nullable = false)
    private boolean isPaid;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Diagnosis getDiagnosis() { return diagnosis; }
    public void setDiagnosis(Diagnosis diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isPaidByNzok() { return paidByNzok; }
    public void setPaidByNzok(boolean paidByNzok) { this.paidByNzok = paidByNzok; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}
