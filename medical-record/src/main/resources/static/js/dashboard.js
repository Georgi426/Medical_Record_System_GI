function showDashboard() {
    loginView.classList.add('hidden');
    dashboardView.classList.remove('hidden');
    renderMenu();
    loadHome();
}

function renderMenu() {
    navMenu.innerHTML = '';
    const items = [{ id: 'home', label: 'Начало', fn: loadHome }];

    if (currentRole === 'ROLE_ADMIN') {
        items.push({ id: 'stats', label: 'Справки и Статистики', fn: loadStats });
        items.push({ id: 'doctors', label: 'Лекари', fn: () => renderTable('doctors', '/api/doctors', ['name', 'uin', 'specialty', 'generalPractitioner']) });
        items.push({ id: 'patients', label: 'Пациенти', fn: () => renderTable('patients', '/api/patients', ['name', 'egn', 'insured']) });
        items.push({ id: 'diagnoses', label: 'Диагнози', fn: () => renderTable('diagnoses', '/api/diagnoses', ['name', 'description']) });
        items.push({ id: 'appointments', label: 'Прегледи', fn: () => renderTable('appointments', '/api/appointments', ['date', 'treatment', 'additionalInfo', 'price', 'paidByNzok', 'patient.name', 'doctor.name', 'diagnosis.name', 'healthy']) });
        items.push({ id: 'sickLeaves', label: 'Болнични', fn: () => renderTable('sickLeaves', '/api/sick-leaves', ['startDate', 'durationDays', 'patient.name', 'doctor.name']) });
    } else if (currentRole === 'ROLE_DOCTOR') {
        items.push({ id: 'patients', label: 'Пациенти', fn: () => renderTable('patients', '/api/patients', ['name', 'egn', 'insured'], false) });
        items.push({ id: 'diagnoses', label: 'Диагнози', fn: () => renderTable('diagnoses', '/api/diagnoses', ['name', 'description'], false) });
        items.push({ id: 'appointments', label: 'Моите прегледи', fn: () => renderTable('appointments', '/api/appointments', ['date', 'treatment', 'additionalInfo', 'price', 'paidByNzok', 'patient.name', 'diagnosis.name', 'healthy'], true, currentId) });
        items.push({ id: 'sickLeaves', label: 'Болнични', fn: () => renderTable('sickLeaves', '/api/sick-leaves', ['startDate', 'durationDays', 'patient.name'], true, currentId) });
    } else if (currentRole === 'ROLE_PATIENT') {
        items.push({ id: 'myAppointments', label: 'Моите прегледи', fn: () => renderTable('appointments', '/api/appointments/me', ['date', 'treatment', 'additionalInfo', 'price', 'paidByNzok', 'doctor.name', 'diagnosis.name', 'healthy'], false) });
        items.push({ id: 'myDiagnoses', label: 'Моите диагнози', fn: () => renderTable('diagnoses', '/api/diagnoses/me', ['name', 'description'], false) });
        items.push({ id: 'mySickLeaves', label: 'Моите болнични', fn: () => renderTable('sickLeaves', '/api/sick-leaves/me', ['startDate', 'durationDays', 'doctor.name'], false) });
    }

    items.forEach(item => {
        const li = document.createElement('li');
        const a = document.createElement('a');
        a.href = '#';
        a.textContent = item.label;
        a.onclick = (e) => {
            e.preventDefault();
            document.querySelectorAll('#navMenu a').forEach(el => el.classList.remove('active'));
            a.classList.add('active');
            item.fn();
        };
        li.appendChild(a);
        navMenu.appendChild(li);
    });
    navMenu.querySelector('a').classList.add('active');
}

function loadHome() {
    pageTitle.textContent = 'Начало';
    let cards = '';
    if (currentRole === 'ROLE_ADMIN') {
        cards = `
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[1].click()"><div class="icon">📊</div><h3>Справки</h3><p>Статистики и анализи</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[2].click()"><div class="icon">👨‍⚕️</div><h3>Лекари</h3><p>Управление на лекари</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[3].click()"><div class="icon">👥</div><h3>Пациенти</h3><p>Управление на пациенти</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[4].click()"><div class="icon">🏥</div><h3>Диагнози</h3><p>Управление на диагнози</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[5].click()"><div class="icon">📋</div><h3>Прегледи</h3><p>Всички прегледи</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[6].click()"><div class="icon">📄</div><h3>Болнични</h3><p>Болнични листове</p></div>
        `;
    } else if (currentRole === 'ROLE_DOCTOR') {
        cards = `
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[1].click()"><div class="icon">👥</div><h3>Пациенти</h3><p>Списък с пациенти</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[2].click()"><div class="icon">🏥</div><h3>Диагнози</h3><p>Всички диагнози</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[3].click()"><div class="icon">📋</div><h3>Моите прегледи</h3><p>Вашите прегледи</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[4].click()"><div class="icon">📄</div><h3>Болнични</h3><p>Болнични листове</p></div>
        `;
    } else {
        cards = `
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[1].click()"><div class="icon">📋</div><h3>Моите прегледи</h3><p>История на прегледите</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[2].click()"><div class="icon">🏥</div><h3>Моите диагнози</h3><p>Поставени диагнози</p></div>
            <div class="welcome-card" onclick="document.querySelectorAll('#navMenu a')[3].click()"><div class="icon">📄</div><h3>Моите болнични</h3><p>Болнични листове</p></div>
            <div class="welcome-card" style="border: 2px solid var(--danger); background: var(--danger-bg);" onclick="testSecurity()">
                <div class="icon">🔒</div><h3 style="color:var(--danger)">Тест Защита</h3><p style="color:var(--text-secondary)">Опит за достъп до чужди данни</p>
            </div>
        `;
    }
    mainContent.innerHTML = `<h2 style="font-size:1.5rem;margin-bottom:0.5rem">Добре дошли, ${currentName}!</h2><p style="color:var(--text-secondary);margin-bottom:1.5rem">Изберете секция от менюто или от картите по-долу.</p><div class="welcome-grid">${cards}</div>`;
}
