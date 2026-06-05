-- =========================================================================
-- ТЕСТОВ С КРИПТ ЗА ПРОВЕРКА НА БАЗАТА ДАННИ (medical_java_f113327)
-- =========================================================================

-- 1. Избиране на базата данни за работа
USE medical_java_f113327;

-- 2. Изчистване на таблиците от стари данни (преди вкарване на нови тестови)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE patient_allergies;
TRUNCATE TABLE sick_leaves;
TRUNCATE TABLE appointments;
TRUNCATE TABLE patients;
TRUNCATE TABLE doctors;
TRUNCATE TABLE users;
TRUNCATE TABLE diagnoses;
TRUNCATE TABLE allergies;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Вкарване на тестови Потребители (users)
-- (Паролите са тестови хешове)
INSERT INTO users (id, username, password, role) VALUES
(1, 'doctor_petrov', '$2a$10$8.uX53RVMJizM3qh159d7.xR2Gq1x.y1W5Yt6qM4J', 'ROLE_DOCTOR'),
(2, 'doctor_ivanova', '$2a$10$8.uX53RVMJizM3qh159d7.xR2Gq1x.y1W5Yt6qM4J', 'ROLE_DOCTOR'),
(3, 'patient_ivan', '$2a$10$8.uX53RVMJizM3qh159d7.xR2Gq1x.y1W5Yt6qM4J', 'ROLE_PATIENT'),
(4, 'patient_elena', '$2a$10$8.uX53RVMJizM3qh159d7.xR2Gq1x.y1W5Yt6qM4J', 'ROLE_PATIENT');

-- 4. Вкарване на тестови Лекари (doctors)
INSERT INTO doctors (id, uin, name, specialty, is_general_practitioner, user_id) VALUES
(1, '1234567890', 'д-р Георги Петров', 'Обща медицина', 1, 1), -- Личен лекар
(2, '0987654321', 'д-р Мария Иванова', 'Кардиология', 0, 2);    -- Кардиолог

-- 5. Вкарване на тестови Пациенти (patients)
INSERT INTO patients (id, name, egn, is_insured, doctor_id, user_id) VALUES
(1, 'Иван Иванов', '9501011234', 1, 1, 3), -- Пациент с личен лекар д-р Петров
(2, 'Елена Георгиева', '9805125678', 0, 1, 4); -- Пациент без здравни осигуровки

-- 6. Вкарване на тестови Диагнози (diagnoses)
INSERT INTO diagnoses (id, name, description) VALUES
(1, 'Есенциална хипертония', 'Високо кръвно налягане с неуточнен произход'),
(2, 'Остър бронхит', 'Възпаление на бронхите, причинено от вирусна инфекция');

-- 7. Вкарване на тестови Алергии (allergies) - НОВАТА НИ ФУНКЦИОНАЛНОСТ!
INSERT INTO allergies (id, name, description) VALUES
(1, 'Пеницилин', 'Тежка алергична реакция с обрив към бета-лактамни антибиотици'),
(2, 'Полени', 'Сезонна сенна хрема, причиняваща хрема и сълзене на очите');

-- 8. Свързване на Пациенти с Алергии (patient_allergies)
INSERT INTO patient_allergies (patient_id, allergy_id) VALUES
(1, 1), -- Иван има алергия към Пеницилин
(1, 2), -- Иван също така има алергия към Полени
(2, 2); -- Елена има алергия само към Полени

-- 9. Вкарване на Прегледи (appointments)
INSERT INTO appointments (id, date, doctor_id, patient_id, diagnosis_id, treatment, price, paid_by_nzok) VALUES
(1, '2026-05-20', 1, 1, 1, 'Диета с ниско съдържание на сол. Предписани хапчета за кръвно сутрин.', 30.00, 1),
(2, '2026-05-25', 2, 2, 2, 'Почивка, прием на топли течности и витамин C. Контролен преглед след 7 дни.', 50.00, 0);

-- 10. Вкарване на Болнични листове (sick_leaves) - СВЪРЗАНИ С ПРЕГЛЕДА!
INSERT INTO sick_leaves (id, start_date, duration_days, doctor_id, patient_id, appointment_id) VALUES
(1, '2026-05-20', 5, 1, 1, 1); -- Болничен на Иван за 5 дни, издаден от д-р Петров по време на преглед №1


-- =========================================================================
-- СЕКЦИЯ ЗА ПРОВЕРКА - SQL ЗАЯВКИ (SELECT)
-- (Маркирайте всяка заявка отделно и я стартирайте с бутона с формата на светкавица)
-- =========================================================================

-- ПРОВЕРКА 1: Виждане на всички Пациенти и техните Лични лекари
SELECT 
    p.name AS 'Име на пациент', 
    p.egn AS 'ЕГН', 
    IF(p.is_insured = 1, 'Да', 'Не') AS 'Осигурен',
    d.name AS 'Личен Лекар (GP)'
FROM patients p
LEFT JOIN doctors d ON p.doctor_id = d.id;


-- ПРОВЕРКА 2: Виждане на Пациентите и техните Алергии (нашата нова Many-to-Many връзка!)
SELECT 
    p.name AS 'Име на пациент', 
    a.name AS 'Алерген', 
    a.description AS 'Описание на алергията'
FROM patients p
JOIN patient_allergies pa ON p.id = pa.patient_id
JOIN allergies a ON pa.allergy_id = a.id;


-- ПРОВЕРКА 3: Информация за всички извършени Прегледи, поставената Диагноза и лечението
SELECT 
    app.date AS 'Дата на преглед', 
    p.name AS 'Пациент', 
    d.name AS 'Прегледал лекар', 
    diag.name AS 'Диагноза', 
    app.treatment AS 'Предписано лечение', 
    app.price AS 'Цена (лв)', 
    IF(app.paid_by_nzok = 1, 'Да', 'Не') AS 'Поето от НЗОК'
FROM appointments app
JOIN patients p ON app.patient_id = p.id
JOIN doctors d ON app.doctor_id = d.id
JOIN diagnoses diag ON app.diagnosis_id = diag.id;


-- ПРОВЕРКА 4: Виждане на Болничните листове и към кой преглед се отнасят те
SELECT 
    sl.start_date AS 'Начало на болничен', 
    sl.duration_days AS 'Продължителност (дни)', 
    p.name AS 'Пациент', 
    d.name AS 'Издал Лекар',
    diag.name AS 'Причина (Диагноза от прегледа)'
FROM sick_leaves sl
JOIN patients p ON sl.patient_id = p.id
JOIN doctors d ON sl.doctor_id = d.id
LEFT JOIN appointments app ON sl.appointment_id = app.id
LEFT JOIN diagnoses diag ON app.diagnosis_id = diag.id;
