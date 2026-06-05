package com.example.demo.repository;

import com.example.demo.model.Patient;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    public void whenSavePatient_thenReturnPatient() {
        // given - инициализиране на данни, без външни зависимости
        User user = new User();
        user.setUsername("testpatient");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_PATIENT);
        entityManager.persist(user);

        Patient patient = new Patient();
        patient.setName("Тест Пациент");
        patient.setEgn("1234567890");
        patient.setInsured(true);
        patient.setUser(user);
        
        entityManager.persist(patient);
        entityManager.flush();

        // when - тестваната функционалност (заявка)
        Patient found = patientRepository.findById(patient.getId()).orElse(null);

        // then - проверка на резултата
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(patient.getName());
        assertThat(found.getEgn()).isEqualTo(patient.getEgn());
    }
}
