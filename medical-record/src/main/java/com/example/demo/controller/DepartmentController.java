package com.example.demo.controller;

import com.example.demo.model.Department;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    public DepartmentController(DepartmentRepository departmentRepository, DoctorRepository doctorRepository) {
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Department create(@Valid @RequestBody com.example.demo.dto.DepartmentDTO dto) {
        Department department = new Department();
        department.setName(dto.getName());
        if (dto.getHeadDoctor() != null && dto.getHeadDoctor().getId() != null) {
            department.setHeadDoctor(doctorRepository.findById(dto.getHeadDoctor().getId()).orElseThrow());
        }
        return departmentRepository.save(department);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Department update(@PathVariable Long id, @Valid @RequestBody com.example.demo.dto.DepartmentDTO dto) {
        Department department = departmentRepository.findById(id).orElseThrow();
        department.setName(dto.getName());
        if (dto.getHeadDoctor() != null && dto.getHeadDoctor().getId() != null) {
            department.setHeadDoctor(doctorRepository.findById(dto.getHeadDoctor().getId()).orElseThrow());
        } else {
            department.setHeadDoctor(null);
        }
        return departmentRepository.save(department);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
