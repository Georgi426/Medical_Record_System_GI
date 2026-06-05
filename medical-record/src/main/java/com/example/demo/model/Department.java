package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "head_doctor_id")
    private Doctor headDoctor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Doctor getHeadDoctor() { return headDoctor; }
    public void setHeadDoctor(Doctor headDoctor) { this.headDoctor = headDoctor; }
}
