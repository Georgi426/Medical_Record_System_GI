package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final PasswordEncoder passwordEncoder;

    // New Repositories
    private final MedicalRecordRepository medicalRecordRepository;
    private final DepartmentRepository departmentRepository;
    private final TreatmentRepository treatmentRepository;
    private final HealthInsuranceRepository healthInsuranceRepository;
    private final InvoiceRepository invoiceRepository;

    public DataSeeder(UserRepository userRepository, DoctorRepository doctorRepository, 
                      PatientRepository patientRepository, DiagnosisRepository diagnosisRepository, 
                      AppointmentRepository appointmentRepository, SickLeaveRepository sickLeaveRepository, 
                      PasswordEncoder passwordEncoder,
                      MedicalRecordRepository medicalRecordRepository,
                      DepartmentRepository departmentRepository,
                      TreatmentRepository treatmentRepository,
                      HealthInsuranceRepository healthInsuranceRepository,
                      InvoiceRepository invoiceRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.appointmentRepository = appointmentRepository;
        this.sickLeaveRepository = sickLeaveRepository;
        this.passwordEncoder = passwordEncoder;
        this.medicalRecordRepository = medicalRecordRepository;
        this.departmentRepository = departmentRepository;
        this.treatmentRepository = treatmentRepository;
        this.healthInsuranceRepository = healthInsuranceRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // Създаване на Администратор
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(User.Role.ROLE_ADMIN);
            userRepository.save(admin);

            // Създаване на Лекар
            User docUser = new User();
            docUser.setUsername("doctor");
            docUser.setPassword(passwordEncoder.encode("doctor"));
            docUser.setRole(User.Role.ROLE_DOCTOR);
            userRepository.save(docUser);

            Doctor doctor = new Doctor();
            doctor.setName("Д-р Петров");
            doctor.setSpecialty("Кардиолог");
            doctor.setUin("1234567890");
            doctor.setGeneralPractitioner(true);
            doctor.setUser(docUser);
            doctorRepository.save(doctor);

            // Създаване на Пациент
            User patUser = new User();
            patUser.setUsername("patient");
            patUser.setPassword(passwordEncoder.encode("patient"));
            patUser.setRole(User.Role.ROLE_PATIENT);
            userRepository.save(patUser);

            Patient patient = new Patient();
            patient.setName("Иван Иванов");
            patient.setEgn("9001011234");
            patient.setInsured(true);
            patient.setGeneralPractitioner(doctor);
            patient.setUser(patUser);
            patientRepository.save(patient);

            // Създаване на Диагноза
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setName("Грип");
            diagnosis.setDescription("Вирусна инфекция с висока температура");
            diagnosisRepository.save(diagnosis);

            // Създаване на Преглед
            Appointment appointment = new Appointment();
            appointment.setDate(LocalDate.now());
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setDiagnosis(diagnosis);
            appointment.setTreatment("Почивка, чай и витамини");
            appointment.setPrice(BigDecimal.ZERO);
            appointment.setPaidByNzok(true);
            appointmentRepository.save(appointment);

            // Създаване на Болничен
            SickLeave sickLeave = new SickLeave();
            sickLeave.setDoctor(doctor);
            sickLeave.setPatient(patient);
            sickLeave.setStartDate(LocalDate.now());
            sickLeave.setDurationDays(5);
            sickLeaveRepository.save(sickLeave);
            
            // --- НОВИ ДАННИ ЗА НОВИТЕ МОДЕЛИ --- //

            // 1. Department (Отделение)
            Department department = new Department();
            department.setName("Кардиология");
            department.setHeadDoctor(doctor);
            departmentRepository.save(department);

            // 2. MedicalRecord (Медицинско досие / Посещение)
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setPatient(patient);
            medicalRecord.setDoctor(doctor);
            medicalRecord.setVisitDate(LocalDate.now());
            medicalRecord.setComplaints("Сърцебиене и лека болка в гърдите");
            medicalRecordRepository.save(medicalRecord);

            // 3. Treatment (Лечение)
            Treatment treatment = new Treatment();
            treatment.setMedicalRecord(medicalRecord);
            treatment.setDescription("Наблюдение и контрол на кръвното налягане");
            treatment.setMedication("Карведилол");
            treatment.setDosage("1/2 таблетка дневно");
            treatmentRepository.save(treatment);

            // 4. HealthInsurance (Здравна осигуровка)
            HealthInsurance healthInsurance = new HealthInsurance();
            healthInsurance.setPatient(patient);
            healthInsurance.setProvider("НЗОК");
            healthInsurance.setPolicyNumber("POL-123456789");
            healthInsurance.setValidUntil(LocalDate.now().plusYears(1));
            healthInsuranceRepository.save(healthInsurance);

            // 5. Invoice (Фактура)
            Invoice invoice = new Invoice();
            invoice.setPatient(patient);
            invoice.setAmount(new BigDecimal("45.50"));
            invoice.setIssueDate(LocalDate.now());
            invoice.setPaid(false);
            invoiceRepository.save(invoice);

            System.out.println("===> БАЗАТА ДАННИ БЕШЕ УСПЕШНО ЗАРЕДЕНА С НАЧАЛНИ ДАННИ (SEEDER) <===");
        }
    }
}
