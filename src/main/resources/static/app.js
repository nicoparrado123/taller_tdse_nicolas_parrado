const result = document.getElementById('result');
const errorBox = document.getElementById('error');
const loading = document.getElementById('loading');

function showResult(text) {
    errorBox.textContent = '';
    result.textContent = text;
    loading.hidden = true;
}

function showError(text) {
    result.textContent = '';
    errorBox.textContent = text;
    loading.hidden = true;
}

function startLoad() {
    result.textContent = '';
    errorBox.textContent = '';
    loading.hidden = false;
}

async function callService(url) {
    startLoad();
    try {
        const res = await fetch(url);
        const data = await res.json();
        if (!res.ok) {
            showError('Error ' + res.status + ': ' + (data.error || res.statusText));
        } else {
            showResult(JSON.stringify(data, null, 2));
        }
    } catch (e) {
        showError('Network failure: ' + e.message);
    }
}

document.getElementById('greetingForm').addEventListener('submit', e => {
    e.preventDefault();
    const name = document.getElementById('greetingName').value.trim();
    if (!name) { showError('Please enter a name.'); return; }
    callService('/api/greeting?name=' + encodeURIComponent(name));
});

document.getElementById('squareForm').addEventListener('submit', e => {
    e.preventDefault();
    const val = document.getElementById('squareValue').value.trim();
    if (val === '' || isNaN(val)) { showError('Please enter a valid number.'); return; }
    callService('/api/square?value=' + encodeURIComponent(val));
});

document.getElementById('timeBtn').addEventListener('click', () => {
    callService('/api/time');
});

document.getElementById('healthBtn').addEventListener('click', () => {
    callService('/api/health');
});
