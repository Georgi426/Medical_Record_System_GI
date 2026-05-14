package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public class EntityReference {
    @NotNull(message = "ID е задължително")
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
