package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

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