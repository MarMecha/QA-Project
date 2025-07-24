// --- Διαχείριση Διαδικασιών --- //
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
