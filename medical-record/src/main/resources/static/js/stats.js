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
                    <select id="searchDocId" class="styled-select" style="flex:1; min-width:200px;">
                        <option value="">-- Изберете лекар --</option>
                    </select>
                    <input type="date" id="searchStart" style="flex:1;" title="Начална дата">
                    <input type="date" id="searchEnd" style="flex:1;" title="Крайна дата">
                    <select id="searchDiagId" class="styled-select" style="flex:1; min-width:150px;">
                        <option value="">-- Изберете диагноза --</option>
                    </select>
                    <select id="searchPatId" class="styled-select" style="flex:1; min-width:150px;">
                        <option value="">-- Изберете пациент --</option>
                    </select>
                    <button onclick="window.doStatsSearch()" class="btn-primary" style="width: auto;">Търси</button>
                </div>
                <div id="searchRes"></div>
            </div>
        </div>
    `;

    try {
        const [diagRes, totalRes, mSickRes, dSickRes, docsInfo, patCountGp, appCountDoc, doctorsList, diagnosesList, patientsList] = await Promise.all([
            fetchApi('/api/stats/most-common-diagnosis'),
            fetchApi('/api/stats/total-paid-by-patients'),
            fetchApi('/api/stats/month-most-sick-leaves'),
            fetchApi('/api/stats/doctor-most-sick-leaves'),
            fetchApi('/api/stats/paid-by-patients-grouped-by-doctor'),
            fetchApi('/api/stats/patients-count-by-gp'),
            fetchApi('/api/stats/appointments-count-by-doctor'),
            fetchApi('/api/doctors'),
            fetchApi('/api/diagnoses'),
            fetchApi('/api/patients')
        ]);

        const docSelect = document.getElementById('searchDocId');
        if (doctorsList) doctorsList.forEach(d => docSelect.insertAdjacentHTML('beforeend', `<option value="${d.id}">${d.name} (${d.specialty})</option>`));

        const diagSelect = document.getElementById('searchDiagId');
        if (diagnosesList) diagnosesList.forEach(d => diagSelect.insertAdjacentHTML('beforeend', `<option value="${d.id}" data-desc="${d.description || ''}">${d.name}</option>`));

        const patSelect = document.getElementById('searchPatId');
        if (patientsList) patientsList.forEach(p => patSelect.insertAdjacentHTML('beforeend', `<option value="${p.id}">${p.name} (ЕГН: ${p.egn})</option>`));


        document.getElementById('stat1').innerHTML = `<h3>Най-честа диагноза</h3><p style="color:var(--primary); font-size:1.5rem">${diagRes ? diagRes.name : 'Няма данни'}</p>`;
        document.getElementById('stat2').innerHTML = `<h3>Общо платено от пациенти</h3><p style="color:var(--primary); font-size:1.5rem">${totalRes || 0} лв.</p>`;
        document.getElementById('stat3').innerHTML = `<h3>Месец с най-много болнични</h3><p style="color:var(--primary); font-size:1.5rem">${mSickRes || 'Няма данни'}</p>`;
        document.getElementById('stat4').innerHTML = `<h3>Лекар с най-много болнични</h3><p style="color:var(--primary); font-size:1.5rem">${dSickRes ? dSickRes.name : 'Няма данни'}</p>`;

        let docsHtml = `<h3>Приходи по лекар</h3><ul>`;
        if (docsInfo && docsInfo.length) {
            docsInfo.forEach(d => docsHtml += `<li>${d.doctor}: ${d.sum} лв.</li>`);
        } else docsHtml += '<li>Няма данни</li>';
        document.getElementById('stat5').innerHTML = docsHtml + '</ul>';

        let patGpHtml = `<h3>Брой пациенти при личен лекар</h3><ul>`;
        if (patCountGp && patCountGp.length) {
            patCountGp.forEach(p => patGpHtml += `<li>${p.doctor}: ${p.count} пациенти</li>`);
        } else patGpHtml += '<li>Няма данни</li>';
        document.getElementById('stat6').innerHTML = patGpHtml + '</ul>';

        let appDocHtml = `<h3>Брой посещения при лекар</h3><ul>`;
        if (appCountDoc && appCountDoc.length) {
            appCountDoc.forEach(a => appDocHtml += `<li>${a.doctor}: ${a.count} посещения</li>`);
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
            html += '<h4 style="margin-top:1rem;color:var(--primary);">Намерени прегледи (по лекар/период):</h4><ul>';
            if (data.length === 0) html += '<li>Не са открити прегледи за тези критерии.</li>';
            else data.forEach(a => {
                const payStatus = a.paidByNzok ? '<span style="color:var(--success);font-weight:600;">(Безплатно / НЗОК)</span>' : `<span style="color:var(--danger);font-weight:600;">(Платено от пациента: ${a.price} лв.)</span>`;
                html += `<li>На дата <strong>${a.date}</strong> пациентът <strong>${a.patient.name}</strong> е прегледан от лекар <strong>${a.doctor.name}</strong> с поставена диагноза: <strong>${a.diagnosis.name}</strong>. ${payStatus}</li>`;
            });
            html += '</ul>';

            if (docId && !start && !end) {
                const gpData = await fetchApi(`/api/stats/patients-by-gp/${docId}`);
                html += '<h4 style="margin-top:1rem;color:var(--primary);">Пациенти, записани при този личен лекар:</h4><ul>';
                if (gpData.length === 0) html += '<li>Този лекар няма записани пациенти.</li>';
                else gpData.forEach(p => html += `<li>Пациент: <strong>${p.name}</strong> (ЕГН: ${p.egn})</li>`);
                html += '</ul>';
            }
        }

        if (diagId) {
            const data = await fetchApi(`/api/stats/patients-by-diagnosis/${diagId}`);
            
            const diagSelect = document.getElementById('searchDiagId');
            const selectedOption = diagSelect.options[diagSelect.selectedIndex];
            const diagName = selectedOption.text;
            const diagDesc = selectedOption.getAttribute('data-desc');
            
            html += '<h4 style="margin-top:1rem;color:var(--primary);">Списък с пациенти, на които е поставена избраната диагноза:</h4><ul>';
            if (data.length === 0) html += '<li>Няма пациенти с тази диагноза.</li>';
            else data.forEach(p => html += `<li>Пациент: <strong>${p.name}</strong> (ЕГН: ${p.egn}) - Диагноза: <strong>${diagName}</strong> <em>(${diagDesc})</em></li>`);
            html += '</ul>';
        }

        if (patId) {
            const data = await fetchApi(`/api/stats/patient-history/${patId}`);
            html += '<h4 style="margin-top:1rem;color:var(--primary);">Медицинска история (прегледи) на избрания пациент:</h4><ul>';
            if (data.length === 0) html += '<li>Този пациент все още няма предишни прегледи.</li>';
            else data.forEach(a => {
                const payStatus = a.paidByNzok ? '<span style="color:var(--success);font-weight:600;">(Безплатно / НЗОК)</span>' : `<span style="color:var(--danger);font-weight:600;">(Платено от пациента: ${a.price} лв.)</span>`;
                html += `<li>На дата <strong>${a.date}</strong> пациентът <strong>${a.patient.name}</strong> е прегледан от лекар <strong>${a.doctor.name}</strong> с поставена диагноза: <strong>${a.diagnosis.name}</strong>. ${payStatus}</li>`;
            });
            html += '</ul>';
        }

        searchRes.innerHTML = html;
    } catch (e) {
        searchRes.innerHTML = '<p class="error-msg">Грешка при търсенето.</p>';
    }
};
