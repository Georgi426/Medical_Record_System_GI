window.reloadAppointments = () => {
    window.currentAppFilter = document.getElementById('appFilter').value;
    window.currentAppSort = document.getElementById('appSort').value;
    document.querySelector('#navMenu a.active').click();
};

async function renderTable(entityName, url, columns, canEdit = true, doctorIdFilter = null) {
    pageTitle.textContent = entityLabels[entityName] || entityName;
    currentEntity = entityName;
    mainContent.innerHTML = '<p>Зареждане...</p>';

    try {
        let finalUrl = url;
        if (entityName === 'appointments') {
            let params = [];
            if (window.currentAppFilter && window.currentAppFilter !== 'all') params.push('filter=' + window.currentAppFilter);
            if (window.currentAppSort) params.push('sort=' + window.currentAppSort);
            if (params.length > 0) {
                finalUrl += (finalUrl.includes('?') ? '&' : '?') + params.join('&');
            }
        }

        let data = await fetchApi(finalUrl);
        if (doctorIdFilter && currentRole === 'ROLE_DOCTOR') {
            data = data.filter(item => item.doctor && item.doctor.id === doctorIdFilter);
        }

        let html = '';

        if (entityName === 'appointments') {
            html += `
            <div style="margin-bottom: 1.5rem; display: flex; gap: 1rem; align-items: center; background: rgba(255,255,255,0.6); padding: 12px; border-radius: 8px; border: 1px solid #e5e7eb;">
                <label style="font-weight: 600; font-size: 0.9rem; color: #374151;">Филтър по дата:</label>
                <select id="appFilter" class="styled-select" onchange="window.reloadAppointments()" style="min-width: 180px; margin: 0; padding: 6px 12px;">
                    <option value="all" ${window.currentAppFilter === 'all' ? 'selected' : ''}>Всички</option>
                    <option value="future" ${window.currentAppFilter === 'future' ? 'selected' : ''}>Бъдещи</option>
                    <option value="past" ${window.currentAppFilter === 'past' ? 'selected' : ''}>Минали</option>
                    <option value="today" ${window.currentAppFilter === 'today' ? 'selected' : ''}>Днес</option>
                </select>
                <label style="font-weight: 600; font-size: 0.9rem; color: #374151; margin-left: 1rem;">Сортиране:</label>
                <select id="appSort" class="styled-select" onchange="window.reloadAppointments()" style="min-width: 180px; margin: 0; padding: 6px 12px;">
                    <option value="" ${window.currentAppSort === '' ? 'selected' : ''}>Без сортиране</option>
                    <option value="desc" ${window.currentAppSort === 'desc' ? 'selected' : ''}>Най-нови / Последни</option>
                    <option value="asc" ${window.currentAppSort === 'asc' ? 'selected' : ''}>Най-стари / Предстоящи</option>
                </select>
            </div>
            `;
        }

        if (canEdit && currentRole !== 'ROLE_PATIENT') {
            html += `<button class="btn-primary" onclick="openModal('${entityName}')" style="margin-bottom:1rem;">Добави Нов</button>`;
        } else if (currentRole === 'ROLE_PATIENT' && entityName === 'appointments') {
            html += `<button class="btn-primary" onclick="openModal('bookAppointment')" style="margin-bottom:1rem;">Запази час за преглед</button>`;
        }

        if (data.length === 0) {
            html += '<p>Няма намерени записи.</p>';
        } else {
            html += '<table><tr>';
            columns.forEach(col => html += `<th>${colLabels[col] || col}</th>`);
            const showActions = (canEdit && currentRole !== 'ROLE_PATIENT') || (currentRole === 'ROLE_PATIENT' && entityName === 'appointments');
            if (showActions) html += '<th>Действия</th>';
            html += '</tr>';

            data.forEach(row => {
                html += '<tr>';
                columns.forEach(col => {
                    let val = getNested(row, col);
                    if (typeof val === 'boolean') val = val ? 'Да' : 'Не';
                    html += `<td>${val || ''}</td>`;
                });
                if (showActions) {
                    html += `<td>`;
                    if (canEdit && currentRole !== 'ROLE_PATIENT') {
                        let editBtnText = entityName === 'appointments' ? 'Попълни Диагноза / Цена' : 'Редактиране';
                        html += `
                            <button onclick='editRecord("${entityName}", ${JSON.stringify(row).replace(/'/g, "&apos;")})'>${editBtnText}</button>
                            <button onclick='deleteRecord("${entityName}", ${row.id})' style="background:rgba(239,68,68,0.15);color:#ef4444">Изтрий</button>
                        `;
                    } else if (currentRole === 'ROLE_PATIENT' && entityName === 'appointments') {
                        if (!row.paidByNzok && row.price > 0 && !row.paid) {
                            html += `<button onclick='payAppointment(${row.id})' style="background:var(--success);color:white;border:none;">Плати (${row.price} лв.)</button>`;
                        } else if (row.paid) {
                            html += `<span style="color:var(--success);font-weight:600;font-size:0.85rem;">Платено</span>`;
                        } else {
                            html += `<span style="color:var(--text-muted);font-size:0.85rem;">Безплатно</span>`;
                        }
                    }
                    html += `</td>`;
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

window.testSecurity = async () => {
    try {
        await fetchApi('/api/appointments', 'GET');
        alert('Грешка: Системата допусна пациента до всички прегледи!');
    } catch (e) {
        alert('УСПЕШНА ЗАЩИТА!\n\nСървърът блокира опита за хакване и върна грешка 403 (Forbidden).\n\nСъобщение от бекенда:\n' + e.message);
    }
}

window.payAppointment = async (id) => {
    if (!confirm('Потвърждавате ли плащането?')) return;
    try {
        await fetchApi(`/api/appointments/${id}/pay`, 'POST');
        alert('Успешно плащане!');
        document.querySelector('#navMenu a.active').click();
    } catch (e) {
        alert('Грешка при плащане: ' + e.message);
    }
}

window.openModal = async (entity, data = null) => {
    currentEditId = data ? data.id : null;
    modalTitle.textContent = data ? 'Редактиране' : 'Добавяне';
    modalFormFields.innerHTML = 'Зареждане...';
    formModal.classList.remove('hidden');

    const today = new Date().toISOString().split('T')[0];
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
            <div class="input-group"><label>Дата</label><input type="date" id="appDate" min="${today}" value="${data ? data.date : ''}" required></div>
            <div class="input-group"><label>Лечение</label><input type="text" id="appTreatment" value="${data && data.treatment ? data.treatment : ''}"></div>
            <div class="input-group"><label>Специфична информация (Доп. Инфо)</label><input type="text" id="appAdditionalInfo" value="${data && data.additionalInfo ? data.additionalInfo : ''}"></div>
            <div class="input-group"><label>Цена (лв)</label><input type="number" step="1" min="0" id="appPrice" value="${data ? data.price : ''}" required></div>
            <div class="input-group"><label>Пациент</label><select id="appPatient">
                ${patients.map(p => `<option value="${p.id}" ${data && data.patient.id === p.id ? 'selected' : ''}>${p.name} (${p.insured ? 'НЗОК' : 'Плаща кеш'})</option>`).join('')}
            </select></div>
            <div class="input-group"><label>Лекар</label><select id="appDoctor" ${currentRole === 'ROLE_DOCTOR' ? 'disabled' : ''}>
                ${doctors.map(d => `<option value="${d.id}" ${(data && data.doctor && data.doctor.id === d.id) || (currentRole === 'ROLE_DOCTOR' && currentId === d.id) ? 'selected' : ''}>${d.name}</option>`).join('')}
            </select></div>
            <div class="input-group"><label>Диагноза</label><select id="appDiagnosis">
                <option value="">-- Изберете диагноза (Опционално) --</option>
                ${diagnoses.map(d => `<option value="${d.id}" ${data && data.diagnosis && data.diagnosis.id === d.id ? 'selected' : ''}>${d.name}</option>`).join('')}
            </select></div>
            <div class="input-group checkbox-group"><input type="checkbox" id="appHealthy" ${data && data.healthy ? 'checked' : ''}><label for="appHealthy">Пациентът е клинично здрав</label></div>
        `;
    } else if (entity === 'sickLeaves') {
        const [patients, doctors] = await Promise.all([fetchApi('/api/patients'), fetchApi('/api/doctors')]);
        html += `
            <div class="input-group"><label>Начална дата</label><input type="date" id="slDate" min="${today}" value="${data ? data.startDate : ''}" required></div>
            <div class="input-group"><label>Продължителност (дни)</label><input type="number" min="1" id="slDays" value="${data ? data.durationDays : ''}" required></div>
            <div class="input-group"><label>Пациент</label><select id="slPatient">
                ${patients.map(p => `<option value="${p.id}" ${data && data.patient.id === p.id ? 'selected' : ''}>${p.name}</option>`).join('')}
            </select></div>
            <div class="input-group"><label>Лекар</label><select id="slDoctor" ${currentRole === 'ROLE_DOCTOR' ? 'disabled' : ''}>
                ${doctors.map(d => `<option value="${d.id}" ${(data && data.doctor.id === d.id) || (currentRole === 'ROLE_DOCTOR' && currentId === d.id) ? 'selected' : ''}>${d.name}</option>`).join('')}
            </select></div>
        `;
    } else if (entity === 'bookAppointment') {
        const doctors = await fetchApi('/api/doctors');
        html += `
            <div class="input-group"><label>Дата</label><input type="date" id="bookDate" min="${today}" required></div>
            <div class="input-group"><label>Лекар</label><select id="bookDoctor" required>
                <option value="">-- Изберете лекар --</option>
                ${doctors.map(d => `<option value="${d.id}">${d.name} (${d.specialty})</option>`).join('')}
            </select></div>
        `;
        currentEntity = 'bookAppointment';
    }
    modalFormFields.innerHTML = html;
}

window.editRecord = (entity, data) => openModal(entity, data);

closeModalBtn.addEventListener('click', () => {
    formModal.classList.add('hidden');
});

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
            additionalInfo: document.getElementById('appAdditionalInfo').value,
            price: parseFloat(document.getElementById('appPrice').value),
            patient: { id: parseInt(document.getElementById('appPatient').value) },
            doctor: { id: parseInt(document.getElementById('appDoctor').value) },
            healthy: document.getElementById('appHealthy').checked
        };
        const diagVal = document.getElementById('appDiagnosis').value;
        if (diagVal) payload.diagnosis = { id: parseInt(diagVal) };
    } else if (currentEntity === 'sickLeaves') {
        payload = {
            startDate: document.getElementById('slDate').value,
            durationDays: parseInt(document.getElementById('slDays').value),
            patient: { id: parseInt(document.getElementById('slPatient').value) },
            doctor: { id: parseInt(document.getElementById('slDoctor').value) }
        };
    } else if (currentEntity === 'bookAppointment') {
        payload = {
            date: document.getElementById('bookDate').value,
            doctorId: parseInt(document.getElementById('bookDoctor').value)
        };
        url = '/api/appointments/book';
        method = 'POST';
    }

    try {
        await fetchApi(url, method, payload);
        formModal.classList.add('hidden');
        document.querySelector('#navMenu a.active').click();
    } catch (err) {
        alert(err.message || 'Възникна грешка при запазване!');
    }
});
