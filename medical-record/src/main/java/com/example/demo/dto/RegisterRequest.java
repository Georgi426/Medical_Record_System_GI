package com.example.demo.dto;

import com.example.demo.model.User.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Потребителското име е задължително")
    @Size(min = 3, max = 50, message = "Потребителското име трябва да е между 3 и 50 символа")
    private String username;

    @NotBlank(message = "Паролата е задължителна")
    @Size(min = 6, max = 100, message = "Паролата трябва да е между 6 и 100 символа")
    private String password;

    @NotNull(message = "Ролята е задължителна")
    private Role role;


    private String name;
    private String uin;
    private String specialty;
    private boolean isGeneralPractitioner;


    private String egn;
    private boolean isInsured;
    @Valid
    private EntityReference generalPractitioner;


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public boolean isGeneralPractitioner() { return isGeneralPractitioner; }
    public void setGeneralPractitioner(boolean generalPractitioner) { this.isGeneralPractitioner = generalPractitioner; }

    public String getEgn() { return egn; }
    public void setEgn(String egn) { this.egn = egn; }

    public boolean isInsured() { return isInsured; }
    public void setInsured(boolean insured) { this.isInsured = insured; }

    public EntityReference getGeneralPractitioner() { return generalPractitioner; }
    public void setGeneralPractitioner(EntityReference generalPractitioner) { this.generalPractitioner = generalPractitioner; }
}
