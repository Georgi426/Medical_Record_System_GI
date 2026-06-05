package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class HealthInsuranceDTO {

    @NotNull(message = "Пациентът е задължителен")
    @Valid
    private EntityReference patient;

    @NotBlank(message = "Осигурителят е задължителен")
    private String provider;

    @NotBlank(message = "Номерът на полицата е задължителен")
    private String policyNumber;

    @NotNull(message = "Датата на валидност е задължителна")
    private LocalDate validUntil;

    public EntityReference getPatient() { return patient; }
    public void setPatient(EntityReference patient) { this.patient = patient; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
}
