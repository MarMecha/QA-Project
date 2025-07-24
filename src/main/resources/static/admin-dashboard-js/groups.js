// --- Διαχείριση Ομάδων (προβολή) --- //
async function fetchGroupsOnly() {
  const res = await fetch('/api/groups');
  const groups = await res.json();
  const table = document.getElementById('groupListTable');
  if (!table) return;
  table.innerHTML = '';
  groups.forEach(g => {
    const members = g.members.map(m => m.fullName).join(', ');
    const row = `<tr>
      <td>${g.name}</td>
      <td>${members}</td>
    </tr>`;
    table.innerHTML += row;
  });
}
