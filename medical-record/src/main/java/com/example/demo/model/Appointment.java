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

    @NotNull(message = "Датата е задължителна")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Лекарят е задължителен")
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Пациентът е задължителен")
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Диагнозата е задължителна")
    @ManyToOne(optional = false)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;

    @Size(max = 2000, message = "Лечението не може да надвишава 2000 символа")
    @Column(columnDefinition = "TEXT")
    private String treatment;

    @NotNull(message = "Цената е задължителна")
    @DecimalMin(value = "0.00", message = "Цената не може да бъде отрицателна")
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean paidByNzok;



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

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isPaidByNzok() { return paidByNzok; }
    public void setPaidByNzok(boolean paidByNzok) { this.paidByNzok = paidByNzok; }
}
