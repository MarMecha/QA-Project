// --- Διαχείρηση Ομάδων --- //
async function loadGroupEmployees() {
  const res = await fetch('/api/employees');
  const employees = await res.json();
  const select = document.getElementById('memberSelect');
  if (!select) return;
  select.innerHTML = '';
  employees.forEach(emp => {
    const opt = document.createElement('option');
    opt.value = emp.id;
    opt.textContent = `${emp.fullName} - ${emp.position}`;
    select.appendChild(opt);
  });
}

async function fetchGroups() {
  const res = await fetch('/api/groups');
  const groups = await res.json();
  const table = document.getElementById('groupTable');
  if (!table) return;
  table.innerHTML = '';
  groups.forEach(g => {
    const members = g.members.map(m => m.fullName).join(', ');
    const row = `<tr>
      <td>${g.name}</td>
      <td>${members}</td>
      <td>
        <a href="assign-qa-process.html?groupId=${g.id}" class="btn btn-sm btn-primary me-2">Ανάθεση</a>
        <button class="btn btn-sm btn-danger" onclick="deleteGroup(${g.id})">🗑️ Διαγραφή</button>
      </td>
    </tr>`;
    table.innerHTML += row;
  });
}

async function deleteGroup(id) {
  if (!confirm('Σίγουρα θέλετε να διαγράψετε το group;')) return;
  await fetch(`/api/groups/${id}`, { method: 'DELETE' });
  fetchGroups();
}

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('groupForm');
  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = document.getElementById('groupName').value;
      const memberSelect = document.getElementById('memberSelect');
      const memberIds = Array.from(memberSelect.selectedOptions).map(o => parseInt(o.value));
      const res = await fetch('/api/groups', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, memberIds })
      });
      const msg = document.getElementById('formMsg');
      msg.classList.remove('d-none', 'alert-success', 'alert-danger');
      msg.classList.add(res.ok ? 'alert-success' : 'alert-danger');
      msg.textContent = res.ok ? '✅ Προστέθηκε το group' : '❌ Σφάλμα προσθήκης';
      if (res.ok) form.reset();
      fetchGroups();
    });
  }
});