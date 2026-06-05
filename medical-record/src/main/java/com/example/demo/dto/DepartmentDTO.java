package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentDTO {

    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 100, message = "Името трябва да е между 2 и 100 символа")
    private String name;

    @Valid
    private EntityReference headDoctor;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public EntityReference getHeadDoctor() { return headDoctor; }
    public void setHeadDoctor(EntityReference headDoctor) { this.headDoctor = headDoctor; }
}
