package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "УИН е задължителен")
    @Pattern(regexp = "^\\d{10}$", message = "УИН трябва да съдържа точно 10 цифри")
    @Column(unique = true, nullable = false)
    private String uin;

    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 100, message = "Името трябва да е между 2 и 100 символа")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Специалността е задължителна")
    @Size(min = 2, max = 100, message = "Специалността трябва да е между 2 и 100 символа")
    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private boolean isGeneralPractitioner;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public boolean isGeneralPractitioner() { return isGeneralPractitioner; }
    public void setGeneralPractitioner(boolean generalPractitioner) { this.isGeneralPractitioner = generalPractitioner; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}