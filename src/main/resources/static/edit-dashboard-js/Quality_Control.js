// --- Διαχείριση Ποιοτικών Στόχων --- //
const BASE_URL = '/api/objectives';
const FORM_URL = '/api/forms';
const AVG_URL = '/api/responses/averages';

let formsCache = [];
let selectedFormId = null;
let selectedQuestion = null;
let currentAverages = {};

async function loadForms() {
  const res = await fetch(FORM_URL);
  if (!res.ok) return;
  formsCache = await res.json();
  const list = document.getElementById('formOptions');
  list.innerHTML = '';
  formsCache.forEach(f => {
    const opt = document.createElement('option');
    opt.value = f.title;
    list.appendChild(opt);
  });
}

function populateQuestions(form) {
  const qList = document.getElementById('questionOptions');
  qList.innerHTML = '';
  form.questions.forEach(q => {
    const opt = document.createElement('option');
    opt.value = q;
    qList.appendChild(opt);
  });
}

async function fetchAverages(formId) {
  const res = await fetch(`${AVG_URL}/${formId}`);
  if (res.ok) currentAverages = await res.json();
  else currentAverages = {};
  updateMeasurement();
}

function updateMeasurement() {
  const avg = currentAverages[selectedQuestion];
  const target = parseFloat(document.getElementById('objTarget')?.value);
  const el = document.getElementById('currentMeasurement');
  if (avg !== undefined) {
    const diff = !isNaN(target) ? (avg - target).toFixed(2) : '';
    el.textContent = `Τρέχουσα: ${avg}${diff ? ` (Διαφορά ${diff})` : ''}`;
  } else {
    el.textContent = '';
  }
}

async function loadObjectives() {
  const res = await fetch(BASE_URL);
  if (!res.ok) return;
  const data = await res.json();
  const list = document.getElementById('objectiveList');
  list.innerHTML = '';
  if (data.length === 0) {
    list.innerHTML = '<li class="list-group-item text-muted">Δεν υπάρχουν στόχοι.</li>';
    return;
  }

  for (const obj of data) {
    const item = document.createElement('li');
    item.className = 'list-group-item d-flex justify-content-between align-items-center';

    const statusRes = await fetch(`${BASE_URL}/${obj.id}/status`);
    let status = null;
    if (statusRes.ok) status = await statusRes.json();

    let statusBadge = '';
    if (status && status.latestValue !== undefined) {
      const diff = status.difference;
      const color = diff >= 0 ? 'success' : 'danger';
      const sign = diff > 0 ? '+' : '';
      statusBadge = `<span class="badge text-bg-${color} me-2">${status.latestValue} (${sign}${diff.toFixed(2)})</span>`;
    }

    item.innerHTML = `
      <div><strong>${obj.name}</strong> - ${obj.description || ''}</div>
      <div>
        <span class="badge text-bg-secondary me-2">🎯 ${obj.targetValue}</span>
        ${statusBadge}
        <button class="btn btn-sm btn-outline-danger" onclick="deleteObjective(${obj.id})">🗑️</button>
      </div>`;
    list.appendChild(item);
  }
}

async function deleteObjective(id) {
  if (!confirm('Διαγραφή στόχου;')) return;
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
  if (res.ok) loadObjectives();
  else alert('Αποτυχία διαγραφής');
}

document.getElementById('objectiveForm')?.addEventListener('submit', async e => {
  e.preventDefault();
  const name = document.getElementById('objName').value.trim();
  const description = document.getElementById('objDesc').value.trim();
  const target = parseFloat(document.getElementById('objTarget').value);
  const payload = {
    name,
    description,
    targetValue: target,
    formId: selectedFormId,
    questionText: selectedQuestion
  };

  const res = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    document.getElementById('objectiveForm').reset();
    selectedFormId = null;
    selectedQuestion = null;
    currentAverages = {};
    loadObjectives();
  } else {
    alert('Σφάλμα δημιουργίας στόχου');
  }
});

document.getElementById('objName')?.addEventListener('focus', loadForms);
document.getElementById('objName')?.addEventListener('change', e => {
  const form = formsCache.find(f => f.title === e.target.value);
  selectedQuestion = null;
  currentAverages = {};
  if (form) {
    selectedFormId = form.id;
    populateQuestions(form);
    fetchAverages(form.id);
  } else {
    selectedFormId = null;
    document.getElementById('questionOptions').innerHTML = '';
    updateMeasurement();
  }
});

document.getElementById('objDesc')?.addEventListener('change', e => {
  selectedQuestion = e.target.value;
  updateMeasurement();
});

document.getElementById('objTarget')?.addEventListener('input', updateMeasurement);