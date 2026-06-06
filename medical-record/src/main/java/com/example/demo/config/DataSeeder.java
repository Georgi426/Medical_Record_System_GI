package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, DoctorRepository doctorRepository, 
                      PatientRepository patientRepository, DiagnosisRepository diagnosisRepository, 
                      AppointmentRepository appointmentRepository, SickLeaveRepository sickLeaveRepository, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.appointmentRepository = appointmentRepository;
        this.sickLeaveRepository = sickLeaveRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // ALWAYS clear database first to ensure fresh data
        sickLeaveRepository.deleteAll();
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        diagnosisRepository.deleteAll();
        userRepository.deleteAll();

        System.out.println("===> СТАРИТЕ ДАННИ БЯХА ИЗТРИТИ, ЗАПОЧВА ЗАРЕЖДАНЕ НА НОВИТЕ <===");

        // =========================
        // 1. СЪЗДАВАНЕ НА АДМИН
        // =========================
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(User.Role.ROLE_ADMIN);
        userRepository.save(admin);

        // =========================
        // 2. СЪЗДАВАНЕ НА ЛЕКАРИ
        // =========================
        // Лекар 1 (Кардиолог)
        User docUser1 = new User();
        docUser1.setUsername("doctor");
        docUser1.setPassword(passwordEncoder.encode("doctor"));
        docUser1.setRole(User.Role.ROLE_DOCTOR);
        Doctor doctor1 = new Doctor();
        doctor1.setName("Д-р Петров");
        doctor1.setSpecialty("Кардиолог");
        doctor1.setUin("1234567890");
        doctor1.setGeneralPractitioner(true);
        doctor1.setUser(docUser1);
        doctorRepository.save(doctor1);

        // Лекар 2 (Невролог)
        User docUser2 = new User();
        docUser2.setUsername("doctor2");
        docUser2.setPassword(passwordEncoder.encode("doctor2"));
        docUser2.setRole(User.Role.ROLE_DOCTOR);
        Doctor doctor2 = new Doctor();
        doctor2.setName("Д-р Георгиев");
        doctor2.setSpecialty("Невролог");
        doctor2.setUin("0987654321");
        doctor2.setGeneralPractitioner(true);
        doctor2.setUser(docUser2);
        doctorRepository.save(doctor2);

        // Лекар 3 (Ортопед)
        User docUser3 = new User();
        docUser3.setUsername("doctor3");
        docUser3.setPassword(passwordEncoder.encode("doctor3"));
        docUser3.setRole(User.Role.ROLE_DOCTOR);
        Doctor doctor3 = new Doctor();
        doctor3.setName("Д-р Маринова");
        doctor3.setSpecialty("Ортопед");
        doctor3.setUin("5647382910");
        doctor3.setGeneralPractitioner(false); // Този е само специалист
        doctor3.setUser(docUser3);
        doctorRepository.save(doctor3);

        // =========================
        // 3. СЪЗДАВАНЕ НА ПАЦИЕНТИ
        // =========================
        // Пациент 1
        User patUser1 = new User();
        patUser1.setUsername("patient");
        patUser1.setPassword(passwordEncoder.encode("patient"));
        patUser1.setRole(User.Role.ROLE_PATIENT);
        Patient patient1 = new Patient();
        patient1.setName("Иван Иванов");
        patient1.setEgn("9001011234");
        patient1.setInsured(true);
        patient1.setGeneralPractitioner(doctor1);
        patient1.setUser(patUser1);
        patientRepository.save(patient1);

        // Пациент 2
        User patUser2 = new User();
        patUser2.setUsername("patient2");
        patUser2.setPassword(passwordEncoder.encode("patient2"));
        patUser2.setRole(User.Role.ROLE_PATIENT);
        Patient patient2 = new Patient();
        patient2.setName("Мария Димитрова");
        patient2.setEgn("8505054321");
        patient2.setInsured(false); // Този пациент няма осигуровки
        patient2.setGeneralPractitioner(doctor2);
        patient2.setUser(patUser2);
        patientRepository.save(patient2);

        // Пациент 3
        User patUser3 = new User();
        patUser3.setUsername("patient3");
        patUser3.setPassword(passwordEncoder.encode("patient3"));
        patUser3.setRole(User.Role.ROLE_PATIENT);
        Patient patient3 = new Patient();
        patient3.setName("Георги Стоянов");
        patient3.setEgn("7808089988");
        patient3.setInsured(true);
        patient3.setGeneralPractitioner(doctor1); // Същият личен лекар като на пациент 1
        patient3.setUser(patUser3);
        patientRepository.save(patient3);


        // =========================
        // 4. СЪЗДАВАНЕ НА ДИАГНОЗИ
        // =========================
        Diagnosis diagnosis1 = new Diagnosis();
        diagnosis1.setName("Грип");
        diagnosis1.setDescription("Вирусна инфекция с висока температура");
        diagnosisRepository.save(diagnosis1);

        Diagnosis diagnosis2 = new Diagnosis();
        diagnosis2.setName("Хипертония");
        diagnosis2.setDescription("Високо кръвно налягане");
        diagnosisRepository.save(diagnosis2);

        Diagnosis diagnosis3 = new Diagnosis();
        diagnosis3.setName("Фрактура");
        diagnosis3.setDescription("Счупване на кост");
        diagnosisRepository.save(diagnosis3);


        // =========================
        // 5. ПРЕГЛЕДИ
        // =========================
        // Преглед 1
        Appointment app1 = new Appointment();
        app1.setDate(LocalDate.now().minusDays(2));
        app1.setDoctor(doctor1);
        app1.setPatient(patient1);
        app1.setDiagnosis(diagnosis1);
        app1.setTreatment("Почивка, чай и витамини");
        app1.setPrice(BigDecimal.ZERO); // Има осигуровки
        app1.setPaidByNzok(true);
        app1.setPaid(true);
        appointmentRepository.save(app1);

        // Преглед 2
        Appointment app2 = new Appointment();
        app2.setDate(LocalDate.now().minusDays(1));
        app2.setDoctor(doctor2);
        app2.setPatient(patient2);
        app2.setDiagnosis(diagnosis2);
        app2.setTreatment("Следене на кръвно налягане");
        app2.setPrice(new BigDecimal("50.00")); // Няма осигуровки, плаща сам
        app2.setPaidByNzok(false);
        app2.setPaid(false);
        appointmentRepository.save(app2);

        // Преглед 3
        Appointment app3 = new Appointment();
        app3.setDate(LocalDate.now());
        app3.setDoctor(doctor3);
        app3.setPatient(patient3);
        app3.setDiagnosis(diagnosis3);
        app3.setTreatment("Обездвижване и гипс");
        app3.setPrice(BigDecimal.ZERO); 
        app3.setPaidByNzok(true);
        app3.setPaid(true);
        appointmentRepository.save(app3);


        // =========================
        // 6. БОЛНИЧНИ ЛИСТОВЕ
        // =========================
        SickLeave sickLeave1 = new SickLeave();
        sickLeave1.setDoctor(doctor1);
        sickLeave1.setPatient(patient1);
        sickLeave1.setStartDate(LocalDate.now().minusDays(2));
        sickLeave1.setDurationDays(5);
        sickLeaveRepository.save(sickLeave1);

        SickLeave sickLeave2 = new SickLeave();
        sickLeave2.setDoctor(doctor3);
        sickLeave2.setPatient(patient3);
        sickLeave2.setStartDate(LocalDate.now());
        sickLeave2.setDurationDays(30);
        sickLeaveRepository.save(sickLeave2);

        System.out.println("===> БАЗАТА ДАННИ БЕШЕ УСПЕШНО ЗАРЕДЕНА С 3-МА ДОКТОРИ И 3-МА ПАЦИЕНТИ (SEEDER) <===");
    }
}
