package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DiagnosisDTO {
    @NotBlank(message = "Името на диагнозата е задължително")
    @Size(min = 2, max = 255, message = "Името на диагнозата трябва да е между 2 и 255 символа")
    private String name;

    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
