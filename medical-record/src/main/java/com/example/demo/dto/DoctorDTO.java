package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DoctorDTO {
    @NotBlank(message = "УИН е задължителен")
    @Pattern(regexp = "^\\d{10}$", message = "УИН трябва да съдържа точно 10 цифри")
    private String uin;

    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 100, message = "Името трябва да е между 2 и 100 символа")
    private String name;

    @NotBlank(message = "Специалността е задължителна")
    @Size(min = 2, max = 100, message = "Специалността трябва да е между 2 и 100 символа")
    private String specialty;

    private boolean generalPractitioner;

    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public boolean isGeneralPractitioner() { return generalPractitioner; }
    public void setGeneralPractitioner(boolean generalPractitioner) { this.generalPractitioner = generalPractitioner; }
}
