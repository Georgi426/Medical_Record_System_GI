package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PatientBookAppointmentDTO {

    @NotNull(message = "Датата е задължителна")
    private LocalDate date;

    @NotNull(message = "Изборът на лекар е задължителен")
    private Long doctorId;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}
