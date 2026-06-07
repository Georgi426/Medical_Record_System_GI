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
