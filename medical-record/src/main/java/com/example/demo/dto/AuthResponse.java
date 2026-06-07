package com.example.demo.dto;

public class AuthResponse {
    private String status;
    private String role;
    private String message;
    private Long id;
    private String name;
    
    public AuthResponse(String status, String role, String message) {
        this.status = status;
        this.role = role;
        this.message = message;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
