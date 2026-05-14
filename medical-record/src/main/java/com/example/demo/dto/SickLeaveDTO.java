package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SickLeaveDTO {
    @NotNull(message = "Началната дата е задължителна")
    private LocalDate startDate;

    @Min(value = 1, message = "Болничният трябва да е поне 1 ден")
    @Max(value = 365, message = "Продължителността не може да надвишава 365 дни")
    private int durationDays;

    @NotNull(message = "Пациентът е задължителен")
    @Valid
    private EntityReference patient;

    @NotNull(message = "Лекарят е задължителен")
    @Valid
    private EntityReference doctor;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public EntityReference getPatient() { return patient; }
    public void setPatient(EntityReference patient) { this.patient = patient; }

    public EntityReference getDoctor() { return doctor; }
    public void setDoctor(EntityReference doctor) { this.doctor = doctor; }
}
