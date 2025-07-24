// --- Διαχείρηση Υπαλλήλων --- //
async function fetchEmployees() {
  const res = await fetch('/api/employees');
  const employees = await res.json();
  const table = document.getElementById('employeeTable');
  if (!table) return;
  table.innerHTML = '';
  employees.forEach(emp => {
    const row = `<tr>
      <td>${emp.fullName ?? ''}</td>
      <td>${emp.position ?? ''}</td>
      <td>${emp.username ?? ''}</td>
      <td><button class="btn btn-sm btn-danger" onclick="deleteEmployee(${emp.id})">🗑️ Διαγραφή</button></td>
    </tr>`;
    table.innerHTML += row;
  });
}

async function deleteEmployee(id) {
  if (!confirm('Σίγουρα θέλετε να διαγράψετε τον υπάλληλο;')) return;
  await fetch(`/api/employees/${id}`, { method: 'DELETE' });
  fetchEmployees();
}

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('employeeForm');
  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const fullName = document.getElementById('fullName').value;
      const position = document.getElementById('position').value;
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;
      const msg = document.getElementById('formMsg');
      const res = await fetch('/api/employees', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, position, username, password })
      });
      msg.classList.remove('d-none', 'alert-success', 'alert-danger');
      msg.classList.add(res.ok ? 'alert-success' : 'alert-danger');
      msg.textContent = res.ok ? '✅ Προστέθηκε ο υπάλληλος' : '❌ Σφάλμα προσθήκης';
      if (res.ok) form.reset();
      fetchEmployees();
    });
  }
});