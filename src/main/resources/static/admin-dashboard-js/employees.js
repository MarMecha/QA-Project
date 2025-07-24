// --- Διαχείριση Υπαλλήλων (προβολή) --- //
async function fetchEmployeesOnly() {
  const res = await fetch('/api/employees');
  const employees = await res.json();
  const table = document.getElementById('employeeOnlyTable');
  if (!table) return;
  table.innerHTML = '';
  employees.forEach(emp => {
    const row = `<tr>
      <td>${emp.fullName ?? ''}</td>
      <td>${emp.position ?? ''}</td>
      <td>${emp.username ?? ''}</td>
    </tr>`;
    table.innerHTML += row;
  });
}
