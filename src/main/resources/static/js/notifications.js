  async function loadNotifications() {
  const name = localStorage.getItem('employeeName');
  const position = localStorage.getItem('employeePosition') || '';
  const panel = document.getElementById('notification-list');
  if (!panel) return;
  
  panel.innerHTML = "<p class='text-muted'>⏳ Φόρτωση...</p>";
  try {
    const url = `/api/notifications?user=${encodeURIComponent(name)}&position=${encodeURIComponent(position)}`;
    const res = await fetch(url);
    if (!res.ok) throw new Error(res.statusText);
    const data = await res.json();
    if (data.length === 0) {
      panel.innerHTML = "<p class='text-muted'>Δεν υπάρχουν ειδοποιήσεις.</p>";
    } else {
      panel.innerHTML = "";
      data.forEach(n => {
        const link = document.createElement('a');
        link.href = `modeler.html?name=${encodeURIComponent(n.diagram)}`;
        link.className = 'list-group-item list-group-item-action';
        link.textContent = n.message;
        panel.appendChild(link);
      });
    }
  } catch (e) {
    console.error('Failed to load notifications', e);
    panel.innerHTML = "<p class='text-danger'>⚠️ Σφάλμα φόρτωσης ειδοποιήσεων.</p>";
  }
}

function toggleNotifications() {
  const panel = document.getElementById('notifPanel');
  if (!panel) return;
  panel.classList.toggle('open');
  if (panel.classList.contains('open')) {
    loadNotifications();
  }
}

// Close notifications panel when clicking outside of it
document.addEventListener('click', (e) => {
  const panel = document.getElementById('notifPanel');
  const bell = document.querySelector('.navbar-bell');
  if (!panel || !bell) return;
  if (panel.classList.contains('open')) {
    if (!panel.contains(e.target) && !bell.contains(e.target)) {
      panel.classList.remove('open');
    }
  }
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    const panel = document.getElementById('notifPanel');
    if (panel && panel.classList.contains('open')) {
      panel.classList.remove('open');
    }
  }
});