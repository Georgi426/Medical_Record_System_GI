package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DoctorRepository doctorRepository,
                           PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Create Admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ROLE_ADMIN);
            userRepository.save(admin);

            // Create Doctor
            User docUser = new User();
            docUser.setUsername("doctor");
            docUser.setPassword(passwordEncoder.encode("doctor123"));
            docUser.setRole(User.Role.ROLE_DOCTOR);
            userRepository.save(docUser);

            Doctor doctor = new Doctor();
            doctor.setName("Д-р Петров");
            doctor.setSpecialty("Обща медицина");
            doctor.setUin("1234567890");
            doctor.setGeneralPractitioner(true);
            doctor.setUser(docUser);
            doctorRepository.save(doctor);

            // Create Patient
            User patUser = new User();
            patUser.setUsername("patient");
            patUser.setPassword(passwordEncoder.encode("patient123"));
            patUser.setRole(User.Role.ROLE_PATIENT);
            userRepository.save(patUser);

            Patient patient = new Patient();
            patient.setName("Иван Иванов");
            patient.setEgn("9001011234");
            patient.setInsured(true);
            patient.setGeneralPractitioner(doctor);
            patient.setUser(patUser);
            patientRepository.save(patient);
        }
    }
}
