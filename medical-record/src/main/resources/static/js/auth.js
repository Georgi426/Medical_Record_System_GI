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
                currentName = data.name || username;

                document.getElementById('currentUser').textContent = currentName;
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
                        currentName = loginData.name || username;
                        document.getElementById('currentUser').textContent = currentName;
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
