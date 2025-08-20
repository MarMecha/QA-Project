// --- Διαχείριση Φορμών --- //
const BASE_URL = 'http://localhost:8080/api/forms';
let questionCounter = 0;
let editingFormId = null;
const CHART_COLORS = [
  '#e6194B', '#3cb44b', '#ffe119', '#4363d8', '#f58231', '#911eb4',
  '#46f0f0', '#f032e6', '#bcf60c', '#fabebe', '#008080', '#e6beff',
  '#9A6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1',
  '#000075', '#808080'
];

function addQuestion(text = '') {
  const container = document.getElementById('questionList');
  if (!container) return;
  const div = document.createElement('div');
  div.className = 'mb-4';
  div.id = `question_${questionCounter}`;
  div.innerHTML = `
    <label class="form-label">Ερώτηση #${questionCounter + 1}</label>
    <input type="text" class="form-control mb-2" name="question_${questionCounter}" value="${text}" required>
    <div class="mb-2">
      <small>Δείγμα: 1 – 5</small>
    </div>
    <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeQuestion('${div.id}')">❌ Αφαίρεση</button>
  `;
  container.appendChild(div);
  questionCounter++;
}

function removeQuestion(id) {
  const element = document.getElementById(id);
  if (element) element.remove();
}

const formBuilder = document.getElementById('formBuilder');
if (formBuilder) {
  formBuilder.addEventListener('submit', async (e) => {
    e.preventDefault();
    const title = document.getElementById('formTitleInput').value.trim();
    if (!title) return alert('❗ Η φόρμα πρέπει να έχει τίτλο.');
    const questions = Array.from(document.querySelectorAll('#questionList input[type="text"]'))
      .map(input => input.value.trim()).filter(text => text.length > 0);
    if (questions.length === 0) return alert('❗ Πρέπει να εισαχθεί τουλάχιστον μία ερώτηση.');
    const url = editingFormId ? `${BASE_URL}/${editingFormId}` : BASE_URL;
    const method = editingFormId ? 'PUT' : 'POST';
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, questions, active: false })
    });
    if (response.ok) {
      alert('✅ Η Φόρμα αποθηκεύθηκε επιτυχώς!');
      editingFormId = null;
      questionCounter = 0;
      formBuilder.reset();
      document.getElementById('questionList').innerHTML = '';
      addQuestion();
      loadSavedForms();
    } else {
      alert('❌ Σφάλμα κατά την αποθήκευση.');
    }
  });
}

let formModal;
let editingFormActive = false;
let modalQuestionCounter = 0;

function addModalQuestion(text = '') {
  const container = document.getElementById('modalQuestionList');
  if (!container) return;
  const div = document.createElement('div');
  div.className = 'mb-3';
  div.id = `modal_q${modalQuestionCounter}`;
  div.innerHTML = `
    <label class="form-label">Ερώτηση #${modalQuestionCounter + 1}</label>
    <input type="text" class="form-control mb-2" value="${text}">
    <button type="button" class="btn btn-sm btn-outline-danger">❌ Αφαίρεση</button>
  `;
  const input = div.querySelector('input');
  input.addEventListener('input', scheduleFormSave);
  div.querySelector('button').onclick = () => {
    div.remove();
    renumberModalQuestions();
    scheduleFormSave();
  };
  container.appendChild(div);
  modalQuestionCounter++;
}

function renumberModalQuestions() {
  const items = document.querySelectorAll('#modalQuestionList > div');
  items.forEach((el, idx) => {
    const label = el.querySelector('label');
    if (label) label.textContent = `Ερώτηση #${idx + 1}`;
  });
}

let formSaveTimeout;
async function autoSaveForm() {
  if (!editingFormId) return;
  const title = document.getElementById('modalFormTitle').value.trim();
  const questions = Array.from(document.querySelectorAll('#modalQuestionList input'))
    .map(i => i.value.trim()).filter(t => t.length > 0);
  await fetch(`${BASE_URL}/${editingFormId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, questions, active: editingFormActive })
  });
  loadSavedForms();
}

function scheduleFormSave() {
  clearTimeout(formSaveTimeout);
  formSaveTimeout = setTimeout(autoSaveForm, 1000);
}

function openEditForm(form) {
  editingFormId = form.id;
  editingFormActive = form.active;
  modalQuestionCounter = 0;
  document.getElementById('modalFormTitle').value = form.title || '';
  const container = document.getElementById('modalQuestionList');
  container.innerHTML = '';
  form.questions.forEach(q => addModalQuestion(q));
  formModal.show();
}

async function toggleActivation(id) {
  const res = await fetch(`${BASE_URL}/${id}/toggle`, { method: 'PUT' });
  if (res.ok) loadSavedForms();
  else alert('Αποτυχία ενεργοποίησης/απενεργοποίησης');
}

async function deleteForm(id) {
  if (!confirm('Είσαι σίγουρος ότι θέλεις να διαγράψεις αυτή τη φόρμα;')) return;
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
  if (res.ok) loadSavedForms();
  else alert('Αποτυχία διαγραφής');
}

async function fetchProcessesOnly() {
  await fetchIndividualProcesses();
  await fetchGroupProcesses();
}

async function fetchIndividualProcesses() {
  const res = await fetch('/api/qa/all');
  const data = await res.json();
  const table = document.getElementById('readonlyIndividualTable');
  if (!table) return;
  table.innerHTML = '';
  data.forEach(p => {
    const row = `<tr>
      <td>${p.processName || ''}</td>
      <td>${p.description || ''}</td>
      <td>${p.fullName || ''}</td>
      <td>${p.position || ''}</td>
      <td>${p.bpmnFileName ? `<a href="edit-dashboard.html#modeler&name=${encodeURIComponent(p.bpmnFileName)}">${p.bpmnFileName}</a>` : '–'}</td>
    </tr>`;
    table.innerHTML += row;
  });
}

async function fetchGroupProcesses() {
  const res = await fetch('/api/qa/group-all');
  const data = await res.json();
  const table = document.getElementById('readonlyGroupTable');
  if (!table) return;
  table.innerHTML = '';
  data.forEach(p => {
    const row = `<tr>
      <td>${p.processName || ''}</td>
      <td>${p.description || ''}</td>
      <td>${p.groupName || ''}</td>
      <td>${p.members || ''}</td>
      <td>${p.bpmnFileName ? `<a href="edit-dashboard.html#modeler&name=${encodeURIComponent(p.bpmnFileName)}">${p.bpmnFileName}</a>` : '–'}</td>
    </tr>`;
    table.innerHTML += row;
  });
}

async function loadSavedForms() {
  const res = await fetch(BASE_URL);
  if (!res.ok) return;
  const forms = await res.json();
  const list = document.getElementById('savedFormsList');
  list.innerHTML = '';
  if (!Array.isArray(forms) || forms.length === 0) {
    list.innerHTML = '<li class="list-group-item text-muted">Δεν υπάρχουν αποθηκευμένες φόρμες.</li>';
    return;
  }
  const thisYear = new Date().getFullYear();
  const lastYear = thisYear - 1;
  const currentForms = [];
  const lastForms = [];
  const olderForms = [];
  forms.forEach(f => {
    const year = f.createdAt ? new Date(f.createdAt).getFullYear() : null;
    if (year === thisYear) currentForms.push(f);
    else if (year === lastYear) lastForms.push(f);
    else olderForms.push(f);
  });

  function createItem(form, isPast = false) {
    const item = document.createElement('li');
    item.className = 'list-group-item';
    item.style.border = '1px solid #dee2e6';

    const header = document.createElement('div');
    header.className = 'd-flex justify-content-between align-items-center';

    const label = document.createElement('span');
    label.textContent = `📄 ${form.title || 'Χωρίς τίτλο'}`;
    label.style.cursor = 'pointer';

    if (isPast) {
      label.onclick = async () => {
        const existingList = item.querySelector('ul');
        if (existingList) {
          existingList.parentElement.remove();
          item.classList.remove('border-primary');
          item.style.border = '1px solid #dee2e6';
          return;
        }
        const res = await fetch(`/api/responses/averages/${form.id}`);
        const averages = res.ok ? await res.json() : {};
        const questionsHtml = (form.questions || []).map(q => {
          const score = averages[q];
          return `
            <li class="list-group-item d-flex justify-content-between align-items-center">
              ${q}
              <span class="badge ${typeof score === 'number' ? 'bg-primary' : 'bg-secondary'}">
                ${typeof score === 'number' ? score : '–'}
              </span>
            </li>`;
        }).join('');
        const wrapper = document.createElement('div');
        wrapper.className = 'mt-3';
        wrapper.innerHTML = `<ul class="list-group list-group-flush mt-2">${questionsHtml}</ul>`;
        item.appendChild(wrapper);
        item.classList.add('border-primary');
        item.style.border = '2px solid #0d6efd';
      };
    } else {
      label.onclick = () => openEditForm(form);
    }

    const buttons = document.createElement('div');
    if (!isPast) {
      const toggleBtn = document.createElement('button');
      toggleBtn.className = 'btn btn-sm me-2 ' + (form.active ? 'btn-success' : 'btn-secondary');
      toggleBtn.textContent = form.active ? 'Ενεργή' : 'Ανενεργή';
      toggleBtn.onclick = () => toggleActivation(form.id);
      buttons.appendChild(toggleBtn);
    }
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn btn-sm btn-outline-danger';
    deleteBtn.textContent = 'Διαγραφή';
    deleteBtn.onclick = () => deleteForm(form.id);
    buttons.appendChild(deleteBtn);

    header.appendChild(label);
    header.appendChild(buttons);
    item.appendChild(header);
    list.appendChild(item);
  }

  function appendGroup(title, arr) {
    if (arr.length === 0) return;
    const headerItem = document.createElement('li');
    headerItem.className = 'list-group-item list-group-item-secondary';
    headerItem.textContent = title;
    list.appendChild(headerItem);
    arr.forEach(f => createItem(f, f._isPast));
  }

  appendGroup('Φετινές', currentForms.map(f => ({ ...f, _isPast: false })));
  appendGroup('Πέρσινες', lastForms.map(f => ({ ...f, _isPast: true })));
  appendGroup('Παλαιότερες', olderForms.map(f => ({ ...f, _isPast: true })));
}

async function loadHistoryForms() {
  const container = document.getElementById('historyForms');
  container.innerHTML = '';
  const [histRes, currentRes, allRes] = await Promise.all([
    fetch(`${BASE_URL}/history`),
    fetch(`${BASE_URL}/current`),
    fetch(BASE_URL)
  ]);
  if (!histRes.ok) return;
  const forms = await histRes.json();
  const currentForms = currentRes.ok ? await currentRes.json() : [];
  const allForms = allRes.ok ? await allRes.json() : [];
  const currentMap = {};
  currentForms.forEach(f => { currentMap[f.title] = f; });
  if (!Array.isArray(forms) || forms.length === 0) {
    container.innerHTML = '<div class="text-muted">Δεν βρέθηκαν περσινά ερωτηματολόγια.</div>';
    return;
  }
  for (const form of forms) {
    const avgRes = await fetch(`/api/responses/averages/${form.id}`);
    const averages = avgRes.ok ? await avgRes.json() : {};
    let currentAverages = {};
    const current = currentMap[form.title];
    if (current) {
      const curRes = await fetch(`/api/responses/averages/${current.id}`);
      currentAverages = curRes.ok ? await curRes.json() : {};
    }
    const questionsRows = (form.questions || []).map(q => {
      const last = averages[q];
      const curr = currentAverages[q];
      let diffMarkup = '<span class="badge bg-secondary">-</span>';
      if (typeof last === 'number' && typeof curr === 'number') {
        const diff = +(curr - last).toFixed(2);
        const color = diff > 0 ? 'bg-success' : diff < 0 ? 'bg-danger' : 'bg-secondary';
        const sign = diff > 0 ? '+' : '';
        diffMarkup = `<span class="badge ${color}">${sign}${diff}</span>`;
      }
      const currMarkup = typeof curr === 'number'
          ? `<span class="badge bg-warning text-dark">${curr}</span>`
          : '-';
      const lastMarkup = typeof last === 'number'
          ? `<span class="badge bg-primary">${last}</span>`
          : '-';
      return `
      <tr>
        <td>${q}</td>
        <td class="text-center">${currMarkup}</td>
        <td class="text-center">${lastMarkup}</td>
        <td class="text-center">${diffMarkup}</td>
      </tr>`;
    }).join('');
    const chartId = `historyChart${form.id}`;
    const html = `
      <div class="accordion-item">
        <h2 class="accordion-header" id="historyHeading${form.id}">
          <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#historyCollapse${form.id}" aria-expanded="false" aria-controls="historyCollapse${form.id}">
            ${form.title}
          </button>
        </h2>
        <div id="historyCollapse${form.id}" class="accordion-collapse collapse" aria-labelledby="historyHeading${form.id}" data-bs-parent="#historyForms">
          <div class="accordion-body p-0">
            <table class="table table-bordered mb-0">
              <thead>
                <tr>
                  <th>Ερώτηση</th>
                  <th class="text-center">Φετινά</th>
                  <th class="text-center">Περσινά</th>
                  <th class="text-center">Διαφορά</th>
                </tr>
              </thead>
              <tbody>
                ${questionsRows}
              </tbody>
            </table>
            <div class="p-3"><canvas id="${chartId}"></canvas></div>
          </div>
        </div>
      </div>`;
    container.insertAdjacentHTML('beforeend', html);

    const sameTitleForms = allForms
      .filter(f => f.title === form.title)
      .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
    renderHistoryChart(chartId, sameTitleForms);
  }
}

function renderHistoryChart(canvasId, forms) {
  if (!forms.length) return;
  const labels = forms.map(f => new Date(f.createdAt).getFullYear());
  Promise.all(forms.map(f => fetch(`/api/responses/averages/${f.id}`).then(r => r.ok ? r.json() : {})))
    .then(avgList => {
      const questions = forms[0].questions || [];
      const datasets = questions.map((q, idx) => ({
        label: q,
        data: avgList.map(avgs => avgs[q] ?? null),
        borderColor: CHART_COLORS[idx % CHART_COLORS.length],
        fill: false
      }));
      const ctx = document.getElementById(canvasId);
      if (ctx) {
        new Chart(ctx, {
          type: 'line',
          data: { labels, datasets },
          options: {
            responsive: true,
            scales: { y: { beginAtZero: true, suggestedMax: 5 } }
          }
        });
      }
    });
}

async function loadActiveFormsWithAverages() {
  const container = document.getElementById('activeForms');
  container.innerHTML = '';
  const res = await fetch('/api/forms/current');
  const forms = res.ok ? await res.json() : [];
  const activeForms = forms.filter(f => f.active === true || f.active === 'true');
  if (activeForms.length === 0) {
    container.innerHTML = '<div class="text-muted">Δεν υπάρχουν ενεργές φόρμες.</div>';
    return;
  }
  for (const form of activeForms) {
    const avgRes = await fetch(`/api/responses/averages/${form.id}`);
    const averages = await avgRes.json();
    const questionsHtml = Object.entries(averages).map(([question, avg]) => `
      <li class="list-group-item d-flex justify-content-between align-items-center">
        ${question}
        <span class="badge bg-primary rounded-pill">${avg}</span>
      </li>
    `).join('');
    container.innerHTML += `
      <div class="accordion-item">
        <h2 class="accordion-header" id="heading${form.id}">
          <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${form.id}" aria-expanded="false" aria-controls="collapse${form.id}">
            ${form.title}
          </button>
        </h2>
        <div id="collapse${form.id}" class="accordion-collapse collapse" aria-labelledby="heading${form.id}" data-bs-parent="#activeForms">
          <div class="accordion-body p-0">
            <ul class="list-group list-group-flush">
              ${questionsHtml}
            </ul>
          </div>
        </div>
      </div>
    `;
  }
}
