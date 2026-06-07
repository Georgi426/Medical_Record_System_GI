package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AppointmentDTO {
    @NotNull(message = "Датата е задължителна")
    private LocalDate date;

    @Size(max = 255, message = "Лечението не може да надвишава 255 символа")
    private String treatment;

    private String additionalInfo;

    @Min(value = 0, message = "Цената не може да бъде отрицателна")
    private java.math.BigDecimal price;

    private boolean healthy;

    @NotNull(message = "Пациентът е задължителен")
    @Valid
    private EntityReference patient;

    @NotNull(message = "Лекарят е задължителен")
    @Valid
    private EntityReference doctor;

    @NotNull(message = "Диагнозата е задължителна")
    @Valid
    private EntityReference diagnosis;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }

    public EntityReference getPatient() { return patient; }
    public void setPatient(EntityReference patient) { this.patient = patient; }

    public EntityReference getDoctor() { return doctor; }
    public void setDoctor(EntityReference doctor) { this.doctor = doctor; }

    public EntityReference getDiagnosis() { return diagnosis; }
    public void setDiagnosis(EntityReference diagnosis) { this.diagnosis = diagnosis; }

    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }
}
