package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "diagnoses")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Името на диагнозата е задължително")
    @Size(min = 2, max = 200, message = "Името трябва да е между 2 и 200 символа")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 1000, message = "Описанието не може да надвишава 1000 символа")
    @Column(columnDefinition = "TEXT")
    private String description;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
