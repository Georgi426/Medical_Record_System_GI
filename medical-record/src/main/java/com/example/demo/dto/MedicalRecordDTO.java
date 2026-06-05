package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MedicalRecordDTO {

    @NotNull(message = "Пациентът е задължителен")
    @Valid
    private EntityReference patient;

    @NotNull(message = "Лекарят е задължителен")
    @Valid
    private EntityReference doctor;

    @NotNull(message = "Датата на посещение е задължителна")
    private LocalDate visitDate;

    @NotBlank(message = "Оплакванията са задължителни")
    private String complaints;

    public EntityReference getPatient() { return patient; }
    public void setPatient(EntityReference patient) { this.patient = patient; }

    public EntityReference getDoctor() { return doctor; }
    public void setDoctor(EntityReference doctor) { this.doctor = doctor; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }

    public String getComplaints() { return complaints; }
    public void setComplaints(String complaints) { this.complaints = complaints; }
}
