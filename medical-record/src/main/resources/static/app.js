document.addEventListener('DOMContentLoaded', () => {
    const loginView = document.getElementById('loginView');
    const dashboardView = document.getElementById('dashboardView');
    const authForm = document.getElementById('authForm');
    const logoutBtn = document.getElementById('logoutBtn');
    const navMenu = document.getElementById('navMenu');
    const mainContent = document.getElementById('mainContent');
    const pageTitle = document.getElementById('pageTitle');

    const tabLogin = document.getElementById('tabLogin');
    const tabRegister = document.getElementById('tabRegister');
    const formTitle = document.getElementById('formTitle');
    const authSubmitBtn = document.getElementById('authSubmitBtn');
    const registerFields = document.getElementById('registerFields');
    const roleSelect = document.getElementById('roleSelect');
    const patientFields = document.getElementById('patientFields');
    const doctorFields = document.getElementById('doctorFields');
    const loginError = document.getElementById('loginError');
    const loginSuccess = document.getElementById('loginSuccess');

    const formModal = document.getElementById('formModal');
    const genericForm = document.getElementById('genericForm');
    const modalFormFields = document.getElementById('modalFormFields');
    const modalTitle = document.getElementById('modalTitle');
    const closeModalBtn = document.getElementById('closeModalBtn');

    let isLoginMode = true;
    let currentUser = null;
    let currentRole = null;
    let currentId = null;
    let currentEntity = null;
    let currentEditId = null;


    const roleNames = {'ROLE_ADMIN':'Администратор','ROLE_DOCTOR':'Лекар','ROLE_PATIENT':'Пациент'};

    tabLogin.addEventListener('click', () => {
        isLoginMode = true;
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        formTitle.textContent = 'Медицинско Досие';
        document.querySelector('.subtitle').textContent = 'Влезте в профила си';
        authSubmitBtn.textContent = 'Вход';
        registerFields.classList.add('hidden');
        loginError.textContent = '';
        loginSuccess.textContent = '';
    });

    tabRegister.addEventListener('click', () => {
        isLoginMode = false;
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        formTitle.textContent = 'Създай акаунт';
        document.querySelector('.subtitle').textContent = 'Регистрирайте се в системата';
        authSubmitBtn.textContent = 'Регистрирай се';
        registerFields.classList.remove('hidden');
        loginError.textContent = '';
        loginSuccess.textContent = '';
        updateRoleFields();
    });

    roleSelect.addEventListener('change', updateRoleFields);

    function updateRoleFields() {
        const role = roleSelect.value;
        if (role === 'ROLE_PATIENT') {
            patientFields.classList.remove('hidden');
            doctorFields.classList.add('hidden');
        } else if (role === 'ROLE_DOCTOR') {
            doctorFields.classList.remove('hidden');
            patientFields.classList.add('hidden');
        } else {
            doctorFields.classList.add('hidden');
            patientFields.classList.add('hidden');
        }
    }

    closeModalBtn.addEventListener('click', () => {
        formModal.classList.add('hidden');
    });


    authForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        loginError.textContent = '';
        loginSuccess.textContent = '';

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        if (isLoginMode) {
            try {
                const res = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                const data = await res.json();
                if (res.ok && data.status === 'success') {
                    currentUser = username;
                    currentRole = data.role;
                    currentId = data.id;

                    document.getElementById('currentUser').textContent = currentUser;
                    document.getElementById('currentRole').textContent = roleNames[currentRole] || currentRole;
                    showDashboard();
                } else {
                    loginError.textContent = data.message || 'Грешно потребителско име или парола';
                }
            } catch (err) {
                loginError.textContent = 'Сървърна грешка при вход';
            }
        } else {
            const payload = { username, password, role: roleSelect.value };
            if (payload.role === 'ROLE_PATIENT') {
                payload.name = document.getElementById('patientName').value;
                payload.egn = document.getElementById('patientEgn').value;
                payload.isInsured = document.getElementById('patientInsured').checked;
            } else if (payload.role === 'ROLE_DOCTOR') {
                payload.name = document.getElementById('doctorName').value;
                payload.uin = document.getElementById('doctorUin').value;
                payload.specialty = document.getElementById('doctorSpecialty').value;
                payload.isGeneralPractitioner = document.getElementById('doctorGp').checked;
            }

            try {
                const res = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    try {
                        const loginRes = await fetch('/api/auth/login', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ username, password })
                        });
                        const loginData = await loginRes.json();
                        if (loginRes.ok && loginData.status === 'success') {
                            currentUser = username;
                            currentRole = loginData.role;
                            currentId = loginData.id;
                            document.getElementById('currentUser').textContent = currentUser;
                            document.getElementById('currentRole').textContent = roleNames[currentRole] || currentRole;
                            showDashboard();
                            return;
                        }
                    } catch (e) {}
                    loginSuccess.textContent = 'Регистрацията е успешна!';
                    authForm.reset();
                    tabLogin.click();
                } else {
                    const data = await res.json();
                    if (data.message) {
                        loginError.textContent = data.message;
                    } else {
                        const msgs = Object.values(data).filter(v => typeof v === 'string');
                        loginError.textContent = msgs.length ? msgs.join(', ') : 'Възникна грешка при регистрация';
                    }
                }
            } catch (err) {
                loginError.textContent = 'Сървърна грешка при регистрация';
            }
        }
    });

    logoutBtn.addEventListener('click', async () => {
        await fetch('/api/auth/logout', { method: 'POST' });
        loginView.classList.remove('hidden');
        dashboardView.classList.add('hidden');
        authForm.reset();
        currentUser = null;
        currentRole = null;
    });


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
            items.push({ id: 'appointments', label: 'Прегледи', fn: () => renderTable('appointments', '/api/appointments', ['date', 'treatment', 'price', 'paidByNzok', 'patient.name', 'doctor.name', 'diagnosis.name']) });
            items.push({ id: 'sickLeaves', label: 'Болнични', fn: () => renderTable('sickLeaves', '/api/sick-leaves', ['startDate', 'durationDays', 'patient.name', 'doctor.name']) });
        } else if (currentRole === 'ROLE_DOCTOR') {
            items.push({ id: 'patients', label: 'Пациенти', fn: () => renderTable('patients', '/api/patients', ['name', 'egn', 'insured'], false) });
            items.push({ id: 'diagnoses', label: 'Диагнози', fn: () => renderTable('diagnoses', '/api/diagnoses', ['name', 'description'], false) });
            items.push({ id: 'appointments', label: 'Моите прегледи', fn: () => renderTable('appointments', '/api/appointments', ['date', 'treatment', 'price', 'paidByNzok', 'patient.name', 'diagnosis.name'], true, currentId) });
            items.push({ id: 'sickLeaves', label: 'Болнични', fn: () => renderTable('sickLeaves', '/api/sick-leaves', ['startDate', 'durationDays', 'patient.name'], true, currentId) });
        } else if (currentRole === 'ROLE_PATIENT') {
            items.push({ id: 'myAppointments', label: 'Моите прегледи', fn: () => renderTable('appointments', '/api/appointments/me', ['date', 'treatment', 'price', 'paidByNzok', 'doctor.name', 'diagnosis.name'], false) });
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
            `;
        }
        mainContent.innerHTML = `<h2 style="font-size:1.5rem;margin-bottom:0.5rem">Добре дошли, ${currentUser}!</h2><p style="color:var(--text-secondary);margin-bottom:1.5rem">Изберете секция от менюто или от картите по-долу.</p><div class="welcome-grid">${cards}</div>`;
    }


    const getNested = (obj, path) => path.split('.').reduce((acc, part) => acc && acc[part], obj);

    async function fetchApi(url, method = 'GET', body = null) {
        const options = { method, headers: {} };
        if (body) {
            options.headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(body);
        }
        const res = await fetch(url, options);
        if (!res.ok) {
            let errorMsg = 'Грешка при заявката';
            try {
                const errData = await res.json();
                if (errData.error) errorMsg = errData.error;
                else {
                    const msgs = Object.values(errData).filter(v => typeof v === 'string');
                    if (msgs.length) errorMsg = msgs.join('\n');
                }
            } catch(e) {}
            throw new Error(errorMsg);
        }
        if (method !== 'DELETE') {
            const text = await res.text();
            return text ? JSON.parse(text) : null;
        }
    }

    const colLabels = {'name':'Име','uin':'УИН','specialty':'Специалност','generalPractitioner':'Личен лекар','egn':'ЕГН','insured':'Осигурен','description':'Описание','date':'Дата','treatment':'Лечение','price':'Цена','paidByNzok':'НЗОК','startDate':'Начало','durationDays':'Дни','patient.name':'Пациент','doctor.name':'Лекар','diagnosis.name':'Диагноза'};
    const entityLabels = {'doctors':'Лекари','patients':'Пациенти','diagnoses':'Диагнози','appointments':'Прегледи','sickLeaves':'Болнични'};

    async function renderTable(entityName, url, columns, canEdit = true, doctorIdFilter = null) {
        pageTitle.textContent = entityLabels[entityName] || entityName;
        currentEntity = entityName;
        mainContent.innerHTML = '<p>Зареждане...</p>';

        try {
            let data = await fetchApi(url);
            if (doctorIdFilter && currentRole === 'ROLE_DOCTOR') {
                data = data.filter(item => item.doctor && item.doctor.id === doctorIdFilter);
            }

            let html = '';
            if (canEdit && currentRole !== 'ROLE_PATIENT') {
                html += `<button class="btn-primary" onclick="openModal('${entityName}')" style="margin-bottom:1rem;">Добави Нов</button>`;
            }

            if (data.length === 0) {
                html += '<p>Няма намерени записи.</p>';
            } else {
                html += '<table><tr>';
                columns.forEach(col => html += `<th>${colLabels[col] || col}</th>`);
                if (canEdit && currentRole !== 'ROLE_PATIENT') html += '<th>Действия</th>';
                html += '</tr>';

                data.forEach(row => {
                    html += '<tr>';
                    columns.forEach(col => {
                        let val = getNested(row, col);
                        if (typeof val === 'boolean') val = val ? 'Да' : 'Не';
                        html += `<td>${val || ''}</td>`;
                    });
                    if (canEdit && currentRole !== 'ROLE_PATIENT') {
                        html += `<td>
                            <button onclick='editRecord("${entityName}", ${JSON.stringify(row).replace(/'/g, "&apos;")})'>Редактирай</button>
                            <button onclick='deleteRecord("${entityName}", ${row.id})' style="background:rgba(239,68,68,0.15);color:#ef4444">Изтрий</button>
                        </td>`;
                    }
                    html += '</tr>';
                });
                html += '</table>';
            }
            mainContent.innerHTML = html;
        } catch (e) {
            mainContent.innerHTML = '<p class="error-msg">Грешка при зареждане на данните.</p>';
        }
    }

    window.deleteRecord = async (entity, id) => {
        if (!confirm('Сигурни ли сте?')) return;
        try {
            await fetchApi(`/api/${entity === 'sickLeaves' ? 'sick-leaves' : entity}/${id}`, 'DELETE');
            document.querySelector('#navMenu a.active').click();
        } catch (e) {
            alert('Грешка при изтриване');
        }
    }

    window.openModal = async (entity, data = null) => {
        currentEditId = data ? data.id : null;
        modalTitle.textContent = data ? 'Редактиране' : 'Добавяне';
        modalFormFields.innerHTML = 'Зареждане...';
        formModal.classList.remove('hidden');

        let html = '';
        if (entity === 'doctors') {
            html += `
                <div class="input-group"><label>Име</label><input type="text" id="docName" value="${data ? data.name : ''}" required></div>
                <div class="input-group"><label>УИН</label><input type="text" id="docUin" value="${data ? data.uin : ''}" required></div>
                <div class="input-group"><label>Специалност</label><input type="text" id="docSpecialty" value="${data ? data.specialty : ''}" required></div>
                <div class="input-group checkbox-group"><input type="checkbox" id="docGp" ${data && data.generalPractitioner ? 'checked' : ''}><label for="docGp">Личен лекар</label></div>
            `;
        } else if (entity === 'patients') {
            const doctors = await fetchApi('/api/doctors');
            const gps = doctors.filter(d => d.generalPractitioner);
            html += `
                <div class="input-group"><label>Име</label><input type="text" id="patName" value="${data ? data.name : ''}" required></div>
                <div class="input-group"><label>ЕГН</label><input type="text" id="patEgn" value="${data ? data.egn : ''}" maxlength="10" required></div>
                <div class="input-group checkbox-group"><input type="checkbox" id="patInsured" ${data && data.insured ? 'checked' : ''}><label for="patInsured">Здравно осигурен</label></div>
                <div class="input-group"><label>Личен лекар</label><select id="patGp">
                    <option value="">-- Няма --</option>
                    ${gps.map(d => `<option value="${d.id}" ${data && data.generalPractitioner && data.generalPractitioner.id === d.id ? 'selected' : ''}>${d.name}</option>`).join('')}
                </select></div>
            `;
        } else if (entity === 'diagnoses') {
            html += `
                <div class="input-group"><label>Име</label><input type="text" id="diagName" value="${data ? data.name : ''}" required></div>
                <div class="input-group"><label>Описание</label><input type="text" id="diagDesc" value="${data ? data.description : ''}"></div>
            `;
        } else if (entity === 'appointments') {
            const [patients, doctors, diagnoses] = await Promise.all([
                fetchApi('/api/patients'), fetchApi('/api/doctors'), fetchApi('/api/diagnoses')
            ]);
            html += `
                <div class="input-group"><label>Дата</label><input type="date" id="appDate" value="${data ? data.date : ''}" required></div>
                <div class="input-group"><label>Лечение</label><input type="text" id="appTreatment" value="${data ? data.treatment : ''}"></div>
                <div class="input-group"><label>Цена</label><input type="number" step="0.01" id="appPrice" value="${data ? data.price : ''}" required></div>
                <div class="input-group"><label>Пациент</label><select id="appPatient">
                    ${patients.map(p => `<option value="${p.id}" ${data && data.patient.id === p.id ? 'selected' : ''}>${p.name}</option>`).join('')}
                </select></div>
                <div class="input-group"><label>Лекар</label><select id="appDoctor" ${currentRole === 'ROLE_DOCTOR' ? 'disabled' : ''}>
                    ${doctors.map(d => `<option value="${d.id}" ${(data && data.doctor.id === d.id) || (currentRole === 'ROLE_DOCTOR' && currentId === d.id) ? 'selected' : ''}>${d.name}</option>`).join('')}
                </select></div>
                <div class="input-group"><label>Диагноза</label><select id="appDiagnosis">
                    ${diagnoses.map(d => `<option value="${d.id}" ${data && data.diagnosis.id === d.id ? 'selected' : ''}>${d.name}</option>`).join('')}
                </select></div>
            `;
        } else if (entity === 'sickLeaves') {
            const [patients, doctors] = await Promise.all([fetchApi('/api/patients'), fetchApi('/api/doctors')]);
            html += `
                <div class="input-group"><label>Начална дата</label><input type="date" id="slDate" value="${data ? data.startDate : ''}" required></div>
                <div class="input-group"><label>Продължителност (дни)</label><input type="number" min="1" id="slDays" value="${data ? data.durationDays : ''}" required></div>
                <div class="input-group"><label>Пациент</label><select id="slPatient">
                    ${patients.map(p => `<option value="${p.id}" ${data && data.patient.id === p.id ? 'selected' : ''}>${p.name}</option>`).join('')}
                </select></div>
                <div class="input-group"><label>Лекар</label><select id="slDoctor" ${currentRole === 'ROLE_DOCTOR' ? 'disabled' : ''}>
                    ${doctors.map(d => `<option value="${d.id}" ${(data && data.doctor.id === d.id) || (currentRole === 'ROLE_DOCTOR' && currentId === d.id) ? 'selected' : ''}>${d.name}</option>`).join('')}
                </select></div>
            `;
        }
        modalFormFields.innerHTML = html;
    }

    window.editRecord = (entity, data) => openModal(entity, data);

    genericForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        let payload = {};
        let url = `/api/${currentEntity === 'sickLeaves' ? 'sick-leaves' : currentEntity}`;
        let method = currentEditId ? 'PUT' : 'POST';
        if (currentEditId) url += `/${currentEditId}`;

        if (currentEntity === 'doctors') {
            payload = {
                name: document.getElementById('docName').value,
                uin: document.getElementById('docUin').value,
                specialty: document.getElementById('docSpecialty').value,
                generalPractitioner: document.getElementById('docGp').checked
            };
        } else if (currentEntity === 'patients') {
            payload = {
                name: document.getElementById('patName').value,
                egn: document.getElementById('patEgn').value,
                insured: document.getElementById('patInsured').checked
            };
            const gpVal = document.getElementById('patGp').value;
            if (gpVal) payload.generalPractitioner = { id: parseInt(gpVal) };
        } else if (currentEntity === 'diagnoses') {
            payload = { name: document.getElementById('diagName').value, description: document.getElementById('diagDesc').value };
        } else if (currentEntity === 'appointments') {
            payload = {
                date: document.getElementById('appDate').value,
                treatment: document.getElementById('appTreatment').value,
                price: parseFloat(document.getElementById('appPrice').value),
                patient: { id: parseInt(document.getElementById('appPatient').value) },
                doctor: { id: parseInt(document.getElementById('appDoctor').value) },
                diagnosis: { id: parseInt(document.getElementById('appDiagnosis').value) }
            };
        } else if (currentEntity === 'sickLeaves') {
            payload = {
                startDate: document.getElementById('slDate').value,
                durationDays: parseInt(document.getElementById('slDays').value),
                patient: { id: parseInt(document.getElementById('slPatient').value) },
                doctor: { id: parseInt(document.getElementById('slDoctor').value) }
            };
        }

        try {
            await fetchApi(url, method, payload);
            formModal.classList.add('hidden');
            document.querySelector('#navMenu a.active').click();
        } catch (err) {
            alert(err.message || 'Възникна грешка при запазване!');
        }
    });


    async function loadStats() {
        pageTitle.textContent = 'Справки и Статистики';
        mainContent.innerHTML = `
            <div class="stats-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="glass-panel" id="stat1">Зареждане...</div>
                <div class="glass-panel" id="stat2">Зареждане...</div>
                <div class="glass-panel" id="stat3">Зареждане...</div>
                <div class="glass-panel" id="stat4">Зареждане...</div>
                <div class="glass-panel" id="stat5">Зареждане...</div>
                <div class="glass-panel" id="stat6">Зареждане...</div>
                <div class="glass-panel" id="stat7" style="grid-column: span 2;">Зареждане...</div>
                
                <div class="glass-panel" style="grid-column: span 2;">
                    <h3>Справки с параметри</h3>
                    <div style="display:flex; gap:1rem; margin:1rem 0; flex-wrap: wrap;">
                        <input type="number" id="searchDocId" placeholder="ID на лекар (за прегледи/пациенти)" style="flex:1; min-width:200px;">
                        <input type="date" id="searchStart" style="flex:1;">
                        <input type="date" id="searchEnd" style="flex:1;">
                        <input type="number" id="searchDiagId" placeholder="ID на диагноза" style="flex:1; min-width:150px;">
                        <input type="number" id="searchPatId" placeholder="ID на пациент" style="flex:1; min-width:150px;">
                        <button onclick="window.doStatsSearch()" class="btn-primary" style="width: auto;">Търси</button>
                    </div>
                    <div id="searchRes"></div>
                </div>
            </div>
        `;

        try {
            const [diagRes, totalRes, mSickRes, dSickRes, docsInfo, patCountGp, appCountDoc] = await Promise.all([
                fetchApi('/api/stats/most-common-diagnosis'),
                fetchApi('/api/stats/total-paid-by-patients'),
                fetchApi('/api/stats/month-most-sick-leaves'),
                fetchApi('/api/stats/doctor-most-sick-leaves'),
                fetchApi('/api/stats/paid-by-patients-grouped-by-doctor'),
                fetchApi('/api/stats/patients-count-by-gp'),
                fetchApi('/api/stats/appointments-count-by-doctor')
            ]);

            document.getElementById('stat1').innerHTML = `<h3>Най-честа диагноза</h3><p style="color:var(--primary); font-size:1.5rem">${diagRes ? diagRes.name : 'Няма данни'}</p>`;
            document.getElementById('stat2').innerHTML = `<h3>Общо платено от пациенти</h3><p style="color:var(--primary); font-size:1.5rem">${totalRes || 0} лв.</p>`;
            document.getElementById('stat3').innerHTML = `<h3>Месец с най-много болнични</h3><p style="color:var(--primary); font-size:1.5rem">${mSickRes || 'Няма данни'}</p>`;
            document.getElementById('stat4').innerHTML = `<h3>Лекар с най-много болнични</h3><p style="color:var(--primary); font-size:1.5rem">${dSickRes ? dSickRes.name : 'Няма данни'}</p>`;

            let docsHtml = `<h3>Приходи по лекар</h3><ul>`;
            if (docsInfo && docsInfo.length) {
                docsInfo.forEach(d => docsHtml += `<li>Д-р ${d.doctor}: ${d.sum} лв.</li>`);
            } else docsHtml += '<li>Няма данни</li>';
            document.getElementById('stat5').innerHTML = docsHtml + '</ul>';

            let patGpHtml = `<h3>Брой пациенти при личен лекар</h3><ul>`;
            if (patCountGp && patCountGp.length) {
                patCountGp.forEach(p => patGpHtml += `<li>Д-р ${p.doctor}: ${p.count} пациенти</li>`);
            } else patGpHtml += '<li>Няма данни</li>';
            document.getElementById('stat6').innerHTML = patGpHtml + '</ul>';

            let appDocHtml = `<h3>Брой посещения при лекар</h3><ul>`;
            if (appCountDoc && appCountDoc.length) {
                appCountDoc.forEach(a => appDocHtml += `<li>Д-р ${a.doctor}: ${a.count} посещения</li>`);
            } else appDocHtml += '<li>Няма данни</li>';
            document.getElementById('stat7').innerHTML = appDocHtml + '</ul>';

        } catch (e) {
            console.error(e);
        }
    }

    window.doStatsSearch = async () => {
        const docId = document.getElementById('searchDocId').value;
        const start = document.getElementById('searchStart').value;
        const end = document.getElementById('searchEnd').value;
        const diagId = document.getElementById('searchDiagId').value;
        const patId = document.getElementById('searchPatId').value;

        let html = '';
        const searchRes = document.getElementById('searchRes');
        searchRes.innerHTML = 'Зареждане...';

        try {
            if (docId || (start && end)) {
                let url = '/api/stats/appointments-search?';
                if (docId) url += `doctorId=${docId}&`;
                if (start) url += `startDate=${start}&`;
                if (end) url += `endDate=${end}`;

                const data = await fetchApi(url);
                html += '<h4 style="margin-top:1rem;color:var(--primary);">Прегледи:</h4><ul>';
                if (data.length === 0) html += '<li>Няма намерени</li>';
                else data.forEach(a => html += `<li>${a.date} - ${a.patient.name} (${a.diagnosis.name})</li>`);
                html += '</ul>';

                if (docId && !start && !end) {
                    const gpData = await fetchApi(`/api/stats/patients-by-gp/${docId}`);
                    html += '<h4 style="margin-top:1rem;color:var(--primary);">Пациенти на този личен лекар:</h4><ul>';
                    if (gpData.length === 0) html += '<li>Няма намерени</li>';
                    else gpData.forEach(p => html += `<li>${p.name} (ЕГН: ${p.egn})</li>`);
                    html += '</ul>';
                }
            }

            if (diagId) {
                const data = await fetchApi(`/api/stats/patients-by-diagnosis/${diagId}`);
                html += '<h4 style="margin-top:1rem;color:var(--primary);">Пациенти с тази диагноза:</h4><ul>';
                if (data.length === 0) html += '<li>Няма намерени</li>';
                else data.forEach(p => html += `<li>${p.name} (ЕГН: ${p.egn})</li>`);
                html += '</ul>';
            }

            if (patId) {
                const data = await fetchApi(`/api/stats/patient-history/${patId}`);
                html += '<h4 style="margin-top:1rem;color:var(--primary);">История на посещенията:</h4><ul>';
                if (data.length === 0) html += '<li>Няма намерени</li>';
                else data.forEach(a => html += `<li>${a.date} - Д-р ${a.doctor.name} (${a.diagnosis.name})</li>`);
                html += '</ul>';
            }

            if (!html) html = '<p>Моля въведете параметри за търсене (напр. ID на лекар, диагноза или пациент).</p>';
            searchRes.innerHTML = html;
        } catch (e) {
            searchRes.innerHTML = '<p class="error-msg">Грешка при извличане на данните.</p>';
        }
    }
});
