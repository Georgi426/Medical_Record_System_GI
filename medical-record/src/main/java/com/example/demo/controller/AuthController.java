package com.example.demo.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.model.User;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          DoctorRepository doctorRepository, PatientRepository patientRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@jakarta.validation.Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

            String role = auth.getAuthorities().iterator().next().getAuthority();
            
            AuthResponse response = new AuthResponse("success", role, "Успешен вход");
            

            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user != null) {
                if (role.equals("ROLE_DOCTOR")) {
                    Optional<Doctor> doctor = doctorRepository.findByUserId(user.getId());
                    doctor.ifPresent(d -> response.setId(d.getId()));
                } else if (role.equals("ROLE_PATIENT")) {
                    Optional<Patient> patient = patientRepository.findByUserId(user.getId());
                    patient.ifPresent(p -> response.setId(p.getId()));
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("error", null, "Грешно потребителско име или парола"));
        }
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@jakarta.validation.Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse("error", null, "Потребителското име вече съществува"));
        }

        if (request.getRole() == User.Role.ROLE_DOCTOR) {
            if (request.getName() == null || request.getName().isBlank()) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", null, "Името на лекаря е задължително"));
            }
            if (request.getUin() == null || !request.getUin().matches("^\\d{10}$")) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", null, "УИН трябва да съдържа точно 10 цифри"));
            }
            if (request.getSpecialty() == null || request.getSpecialty().isBlank()) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", null, "Специалността е задължителна"));
            }
        } else if (request.getRole() == User.Role.ROLE_PATIENT) {
            if (request.getName() == null || request.getName().isBlank()) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", null, "Името на пациента е задължително"));
            }
            if (request.getEgn() == null || !request.getEgn().matches("^\\d{10}$")) {
                return ResponseEntity.badRequest().body(new AuthResponse("error", null, "ЕГН трябва да съдържа точно 10 цифри"));
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user = userRepository.save(user);

        if (request.getRole() == User.Role.ROLE_DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setName(request.getName());
            doctor.setUin(request.getUin());
            doctor.setSpecialty(request.getSpecialty());
            doctor.setGeneralPractitioner(request.isGeneralPractitioner());
            doctor.setUser(user);
            doctorRepository.save(doctor);
        } else if (request.getRole() == User.Role.ROLE_PATIENT) {
            Patient patient = new Patient();
            patient.setName(request.getName());
            patient.setEgn(request.getEgn());
            patient.setInsured(request.isInsured());
            patient.setUser(user);
            
            if (request.getGeneralPractitionerId() != null) {
                doctorRepository.findById(request.getGeneralPractitionerId())
                        .ifPresent(patient::setGeneralPractitioner);
            }
            
            patientRepository.save(patient);
        }

        return ResponseEntity.ok(new AuthResponse("success", user.getRole().name(), "Регистрацията е успешна"));
    }
}
