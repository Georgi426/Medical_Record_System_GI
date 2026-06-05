package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TreatmentDTO {

    @NotNull(message = "Медицинското досие е задължително")
    @Valid
    private EntityReference medicalRecord;

    @NotBlank(message = "Описанието на лечението е задължително")
    private String description;

    private String medication;

    private String dosage;

    public EntityReference getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(EntityReference medicalRecord) { this.medicalRecord = medicalRecord; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
}
