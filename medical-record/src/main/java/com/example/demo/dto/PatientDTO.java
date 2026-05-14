package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientDTO {
    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 100, message = "Името трябва да е между 2 и 100 символа")
    private String name;

    @NotBlank(message = "ЕГН е задължително")
    @Size(min = 10, max = 10, message = "ЕГН трябва да е точно 10 цифри")
    @Pattern(regexp = "^\\d{10}$", message = "ЕГН трябва да съдържа само цифри")
    private String egn;

    @Valid
    private EntityReference generalPractitioner;

    private boolean insured;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEgn() { return egn; }
    public void setEgn(String egn) { this.egn = egn; }

    public EntityReference getGeneralPractitioner() { return generalPractitioner; }
    public void setGeneralPractitioner(EntityReference generalPractitioner) { this.generalPractitioner = generalPractitioner; }

    public boolean isInsured() { return insured; }
    public void setInsured(boolean insured) { this.insured = insured; }
}
