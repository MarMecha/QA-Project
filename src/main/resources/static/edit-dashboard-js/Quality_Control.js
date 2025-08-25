// --- Διαχείριση Ποιοτικών Στόχων --- //
const BASE_URL = '/api/objectives';
const FORM_URL = '/api/forms/current';
const FORMS_ALL_URL = '/api/forms'; 
const AVG_URL = '/api/responses/averages';

const CHART_COLORS = [
  '#e6194B', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4',
  '#46f0f0', '#f032e6', '#bcf60c', '#fabebe', '#008080', '#e6beff',
  '#9A6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1',
  '#000075', '#808080'
];

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
    list.innerHTML = '<div class="text-muted">Δεν υπάρχουν στόχοι.</div>';
    return;
  }

  for (const obj of data) {
    const item = document.createElement('div');
    item.className = 'accordion-item';

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

    const chartId = `objChart${obj.id}`;
    item.innerHTML = `
      <h2 class="accordion-header" id="heading${obj.id}">
        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${obj.id}" aria-expanded="false" aria-controls="collapse${obj.id}">
          <strong>${obj.name}</strong> - ${obj.questionText || obj.description || ''}
        </button>
      </h2>
      <div id="collapse${obj.id}" class="accordion-collapse collapse" aria-labelledby="heading${obj.id}" data-bs-parent="#objectiveList">
        <div class="accordion-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
              <span class="badge text-bg-secondary me-2">🎯 ${obj.targetValue}</span>
              ${statusBadge}
            </div>
            <button class="btn btn-sm btn-outline-danger" onclick="deleteObjective(${obj.id})">🗑️</button>
          </div>
          <canvas id="${chartId}"></canvas>
        </div>
      </div>`;
    list.appendChild(item);

    const collapseEl = item.querySelector(`#collapse${obj.id}`);
    collapseEl.addEventListener('shown.bs.collapse', () => renderObjectiveChart(obj, chartId), { once: true });
  }
}

async function renderObjectiveChart(obj, canvasId) {
  const formsRes = await fetch(FORMS_ALL_URL);
  if (!formsRes.ok) return;
  const allForms = await formsRes.json();
  const sameForms = allForms
    .filter(f => f.title === obj.name)
    .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
  if (!sameForms.length) return;
  const labels = sameForms.map(f => new Date(f.createdAt).getFullYear());
  const avgList = await Promise.all(
    sameForms.map(f => fetch(`${AVG_URL}/${f.id}`).then(r => r.ok ? r.json() : {}))
  );
  const question = obj.questionText || obj.description;
  const data = avgList.map(avgs => avgs[question] ?? null);
  const currentYear = new Date().getFullYear();
  const targetData = labels.map(y => y === currentYear ? obj.targetValue : null);

  const ctx = document.getElementById(canvasId);
  if (ctx) {
    new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: question,
            data,
            borderColor: CHART_COLORS[0],
            fill: false
          },
          {
            label: 'Στόχος',
            data: targetData,
            borderColor: 'red',
            backgroundColor: 'red',
            showLine: false,
            pointRadius: 5
          }
        ]
      },
      options: {
        responsive: true,
        scales: { y: { beginAtZero: true, suggestedMax: 5 } }
      }
    });
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