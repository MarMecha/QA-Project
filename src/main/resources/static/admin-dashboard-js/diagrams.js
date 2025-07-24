// --- Διαχείριση Διαγραμμάτων --- //
async function loadDiagrams() {
  const list = document.getElementById('diagramList');
  list.innerHTML = '';
  const res = await fetch('/api/bpmn');
  const diagrams = await res.json();
  if (diagrams.length === 0) {
    list.innerHTML = '<tr><td colspan="4" class="text-muted">Δεν υπάρχουν διαγράμματα.</td></tr>';
    return;
  }
  diagrams.slice().reverse().forEach(d => {
    const row = document.createElement('tr');
    const nameCell = document.createElement('td');
    nameCell.innerHTML = `<a href="edit-dashboard.html#modeler&name=${encodeURIComponent(d.name)}">${d.name}</a>`;
    const progressCell = document.createElement('td');
    const pct = d.userTaskCount ? Math.round((d.completedUserTaskCount / d.userTaskCount) * 100) : 0;
    progressCell.innerHTML = `
      <div class="progress" style="height:20px">
        <div class="progress-bar" role="progressbar" style="width: ${pct}%" aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100">${pct}%</div>
      </div>`;
    const actionCell = document.createElement('td');
    const btn = document.createElement('button');
    btn.className = 'btn btn-sm btn-outline-success';
    btn.textContent = d.published ? 'Publish' : 'Un-Publish';
    btn.onclick = () => toggleDiagramPublish(d.name);
    actionCell.appendChild(btn);
    const deleteCell = document.createElement('td');
    const delBtn = document.createElement('button');
    delBtn.className = 'btn btn-sm btn-outline-danger';
    delBtn.textContent = 'Διαγραφή';
    delBtn.onclick = () => deleteDiagram(d.name);
    deleteCell.appendChild(delBtn);
    row.appendChild(nameCell);
    row.appendChild(progressCell);
    row.appendChild(actionCell);
    row.appendChild(deleteCell);
    list.appendChild(row);
  });
}

async function toggleDiagramPublish(name) {
  const res = await fetch(`/api/bpmn/${encodeURIComponent(name)}/toggle`, { method: 'PUT' });
  if (res.ok) {
    loadDiagrams();
  } else {
    alert('❌ Σφάλμα δημοσίευσης');
  }
}

async function deleteDiagram(name) {
  if (!confirm('Είσαι σίγουρος ότι θέλεις να διαγράψεις αυτό το διάγραμμα;')) return;
  const res = await fetch(`/api/bpmn/${encodeURIComponent(name)}`, { method: 'DELETE' });
  if (res.ok || res.status === 404) {
    loadDiagrams();
  } else {
    alert('❌ Αποτυχία διαγραφής');
  }
}
