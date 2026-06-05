package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceDTO {

    @NotNull(message = "Пациентът е задължителен")
    @Valid
    private EntityReference patient;

    @NotNull(message = "Сумата е задължителна")
    @DecimalMin(value = "0.01", message = "Сумата трябва да е по-голяма от 0")
    private BigDecimal amount;

    @NotNull(message = "Датата на издаване е задължителна")
    private LocalDate issueDate;

    private boolean isPaid;

    public EntityReference getPatient() { return patient; }
    public void setPatient(EntityReference patient) { this.patient = patient; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { this.isPaid = paid; }
}
