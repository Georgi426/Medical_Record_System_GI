package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 100, message = "Името трябва да е между 2 и 100 символа")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "ЕГН е задължително")
    @Size(min = 10, max = 10, message = "ЕГН трябва да е точно 10 цифри")
    @Pattern(regexp = "^\\d{10}$", message = "ЕГН трябва да съдържа само цифри")
    @Column(unique = true, nullable = false)
    private String egn;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor generalPractitioner;

    @Column(nullable = false)
    private boolean isInsured;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEgn() { return egn; }
    public void setEgn(String egn) { this.egn = egn; }

    public Doctor getGeneralPractitioner() { return generalPractitioner; }
    public void setGeneralPractitioner(Doctor generalPractitioner) { this.generalPractitioner = generalPractitioner; }

    public boolean isInsured() { return isInsured; }
    public void setInsured(boolean insured) { this.isInsured = insured; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}