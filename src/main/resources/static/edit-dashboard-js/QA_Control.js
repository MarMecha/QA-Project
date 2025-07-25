// --- Διαχείρηση QA Διαδικασιών---//
let indData = [], groupData = [];

async function loadIndividual() {
  const res = await fetch('/api/qa/all');
  indData = await res.json();
  const table = document.getElementById('individualTable');
  table.innerHTML = '';
  indData.forEach((p, idx) => {
    const row = `<tr>
      <td>${p.processName || ''}</td>
      <td>${p.description || ''}</td>
      <td>${p.fullName || ''}</td>
      <td>${p.position || ''}</td>
      <td>${p.bpmnFileName ? `<a href="edit-dashboard.html#modeler&name=${encodeURIComponent(p.bpmnFileName)}">${p.bpmnFileName}</a>` : '–'}</td>
      <td>
        <button class="btn btn-sm btn-primary me-2" onclick="editInd(${idx})">✏️</button>
        <button class="btn btn-sm btn-danger" onclick="deleteInd(${p.id})">🗑️</button>
      </td>
    </tr>`;
    table.innerHTML += row;
  });
}

async function loadQaGroups() {
  const res = await fetch('/api/qa/group-all');
  groupData = await res.json();
  const table = document.getElementById('qaGroupTable');
  table.innerHTML = '';
  groupData.forEach((p, idx) => {
    const row = `<tr>
      <td>${p.processName || ''}</td>
      <td>${p.description || ''}</td>
      <td>${p.groupName || ''}</td>
      <td>${p.members || ''}</td>
      <td>${p.bpmnFileName ? `<a href="edit-dashboard.html#modeler&name=${encodeURIComponent(p.bpmnFileName)}">${p.bpmnFileName}</a>` : '–'}</td>
      <td>
        <button class="btn btn-sm btn-primary me-2" onclick="editGroup(${idx})">✏️</button>
        <button class="btn btn-sm btn-danger" onclick="deleteQaGroup(${p.id})">🗑️</button>
      </td>
    </tr>`;
    table.innerHTML += row;
  });
}

function editInd(idx) {
  const p = indData[idx];
  const processName = prompt('Όνομα Διαδικασίας', p.processName);
  if (processName === null) return;
  const description = prompt('Περιγραφή', p.description);
  if (description === null) return;
  const bpmnFileName = prompt('Αρχείο BPMN', p.bpmnFileName || '');
  if (bpmnFileName === null) return;
  const fullName = prompt('Υπάλληλος', p.fullName);
  if (fullName === null) return;
  const position = prompt('Θέση', p.position);
  if (position === null) return;
  fetch('/api/qa/' + p.id, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ processName, description, bpmnFileName, fullName, position })
  }).then(loadAll);
}

async function deleteInd(id) {
  if (!confirm('Σίγουρα θέλετε να διαγράψετε αυτή τη διαδικασία;')) return;
  const res = await fetch('/api/qa/' + id, { method: 'DELETE' });
  if (res.ok) {
    loadAll();
  } else {
    alert('❌ Σφάλμα διαγραφής');
  }
}

function editGroup(idx) {
  const p = groupData[idx];
  const processName = prompt('Όνομα Διαδικασίας', p.processName);
  if (processName === null) return;
  const description = prompt('Περιγραφή', p.description);
  if (description === null) return;
  const bpmnFileName = prompt('Αρχείο BPMN', p.bpmnFileName || '');
  if (bpmnFileName === null) return;
  const groupName = prompt('Όνομα Group', p.groupName);
  if (groupName === null) return;
  const members = prompt('Μέλη', p.members);
  if (members === null) return;
  fetch('/api/qa/group/' + p.id, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ processName, description, bpmnFileName, groupName, members })
  }).then(loadAll);
}

async function deleteQaGroup(id) {
  if (!confirm('Σίγουρα θέλετε να διαγράψετε αυτή τη διαδικασία;')) return;
  const res = await fetch('/api/qa/group/' + id, { method: 'DELETE' });
  if (res.ok) {
    loadAll();
  } else {
    alert('❌ Σφάλμα διαγραφής');
  }
}

function loadAll() {
  loadIndividual();
  loadQaGroups();
}