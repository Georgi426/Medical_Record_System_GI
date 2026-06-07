package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DiagnosisRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.SickLeaveRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.StatsService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MedicalSystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SickLeaveRepository sickLeaveRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private StatsService statsService;

    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeEach
    public void setup() {
        sickLeaveRepository.deleteAll();
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        diagnosisRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.flush();

        // Setup User for Patient
        User pUser = new User();
        pUser.setUsername("testpatient");
        pUser.setPassword("password");
        pUser.setRole(User.Role.ROLE_PATIENT);
        userRepository.save(pUser);

        testPatient = new Patient();
        testPatient.setName("Test Patient");
        testPatient.setEgn("1111111111");
        testPatient.setInsured(true);
        testPatient.setUser(pUser);
        testPatient = patientRepository.save(testPatient);

        // Setup User for Doctor
        User dUser = new User();
        dUser.setUsername("testdoctor");
        dUser.setPassword("password");
        dUser.setRole(User.Role.ROLE_DOCTOR);
        userRepository.save(dUser);

        testDoctor = new Doctor();
        testDoctor.setName("Test Doctor");
        testDoctor.setUin("2222222222");
        testDoctor.setSpecialty("Cardiology");
        testDoctor.setGeneralPractitioner(false);
        testDoctor.setUser(dUser);
        testDoctor = doctorRepository.save(testDoctor);
    }

    // 1. Security / Authentication Test
    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isForbidden()); // In REST, unauthenticated gets 403/401 instead of 302 redirect
    }

    // 2. Security / Authorization Test
    @Test
    @WithMockUser(username = "testpatient", roles = { "PATIENT" })
    public void testPatientRoleForbiddenFromAllAppointments() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }

    // 3. Business Logic Test (Booking)
    @Test
    @WithMockUser(username = "testpatient", roles = { "PATIENT" })
    public void testBookAppointmentSuccess() throws Exception {
        String requestJson = String.format("{\"doctorId\":%d, \"date\":\"%s\"}", testDoctor.getId(),
                LocalDate.now().plusDays(1).toString());

        mockMvc.perform(post("/api/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        List<Appointment> apps = appointmentRepository.findAll();
        assertEquals(1, apps.size());
        assertEquals(testPatient.getId(), apps.get(0).getPatient().getId());
    }

    @Test
    @WithMockUser(username = "testpatient", roles = { "PATIENT" })
    public void testBookAppointmentPastDateFails() throws Exception {
        String requestJson = String.format("{\"doctorId\":%d, \"date\":\"%s\"}", testDoctor.getId(),
                LocalDate.now().minusDays(1).toString());

        mockMvc.perform(post("/api/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Не можете да запазвате час за минала дата."));
    }

    // 5. Validation Test (Conflict)
    @Test
    @WithMockUser(username = "testpatient", roles = { "PATIENT" })
    public void testBookAppointmentDoctorConflictFails() throws Exception {
        // Book first appointment
        Appointment app = new Appointment();
        app.setDoctor(testDoctor);
        app.setPatient(testPatient);
        app.setDate(LocalDate.now().plusDays(2));
        app.setPrice(BigDecimal.ZERO);
        app.setPaidByNzok(true);
        app.setPaid(true);
        app.setTreatment("");
        appointmentRepository.save(app);

        // Attempt second booking for the exact same doctor and date
        String requestJson = String.format("{\"doctorId\":%d, \"date\":\"%s\"}", testDoctor.getId(),
                LocalDate.now().plusDays(2).toString());

        mockMvc.perform(post("/api/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Този лекар вече има записан час за избраната дата."));
    }

    // 6. Payment Logic
    @Test
    @WithMockUser(username = "testpatient", roles = { "PATIENT" })
    public void testPaymentLogicNzok() throws Exception {
        // Patient is already insured in setup()
        String requestJson = String.format("{\"doctorId\":%d, \"date\":\"%s\"}", testDoctor.getId(),
                LocalDate.now().plusDays(3).toString());

        mockMvc.perform(post("/api/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        Appointment app = appointmentRepository.findAll().get(0);
        assertTrue(app.isPaidByNzok());
        assertTrue(app.isPaid());
    }

    // 7. Payment Logic (Cash Payment Endpoint)
    @Test
    @WithMockUser(username = "testpatient", roles = { "PATIENT" })
    public void testPayAppointmentEndpoint() throws Exception {
        // Make patient
        testPatient.setInsured(false);
        patientRepository.save(testPatient);

        Appointment app = new Appointment();
        app.setDoctor(testDoctor);
        app.setPatient(testPatient);
        app.setDate(LocalDate.now().plusDays(1));
        app.setPrice(new BigDecimal("50.00"));
        app.setPaidByNzok(false);
        app.setPaid(false);
        app.setTreatment("");
        app = appointmentRepository.save(app);

        // Call the pay endpoint
        mockMvc.perform(post("/api/appointments/" + app.getId() + "/pay"))
                .andExpect(status().isOk());

        Appointment paidApp = appointmentRepository.findById(app.getId()).get();
        assertTrue(paidApp.isPaid());
    }

}
