package com.example.demo.controller;

import com.example.demo.model.Invoice;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;

    public InvoiceController(InvoiceRepository invoiceRepository, PatientRepository patientRepository) {
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Invoice create(@Valid @RequestBody com.example.demo.dto.InvoiceDTO dto) {
        Invoice invoice = new Invoice();
        invoice.setPatient(patientRepository.findById(dto.getPatient().getId()).orElseThrow());
        invoice.setAmount(dto.getAmount());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setPaid(dto.isPaid());
        return invoiceRepository.save(invoice);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Invoice update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.InvoiceDTO dto) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setPatient(patientRepository.findById(dto.getPatient().getId()).orElseThrow());
        invoice.setAmount(dto.getAmount());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setPaid(dto.isPaid());
        return invoiceRepository.save(invoice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
