function msgStorageKey() {
  const name = localStorage.getItem('employeeName') || 'anon';
  return `readMsgTimes_${name}`;
}

function getReadMap() {
  try {
    return JSON.parse(localStorage.getItem(msgStorageKey()) || '{}');
  } catch {
    return {};
  }
}

function markDiagramRead(diagram, time) {
  const map = getReadMap();
  map[diagram] = time;
  localStorage.setItem(msgStorageKey(), JSON.stringify(map));
}

async function loadMessages() {
  const name = localStorage.getItem('employeeName');
  const position = localStorage.getItem('employeePosition') || '';
  const panel = document.getElementById('message-list');
  if (!panel) return;

  panel.innerHTML = "<p class='text-muted'>⏳ Φόρτωση...</p>";
  try {
    const res = await fetch(`/api/message-notifications?user=${encodeURIComponent(name)}`);
    if (!res.ok) throw new Error(res.statusText);
    const data = await res.json();
    const badge = document.getElementById('msgBadge');
    const readMap = getReadMap();
    const unread = data.filter(n => {
      const t = readMap[n.diagram];
      return !t || new Date(n.time) > new Date(t);
    });
    if (unread.length === 0) {
      panel.innerHTML = "<p class='text-muted'>Δεν υπάρχουν νέα μηνύματα.</p>";
      if (badge) badge.style.display = 'none';
    } else {
      panel.innerHTML = "";
      unread.forEach(n => {
        const link = document.createElement('a');
         const target = position === 'QA'
          ? `edit-dashboard.html#modeler&name=${encodeURIComponent(n.diagram)}`
          : `modeler.html?name=${encodeURIComponent(n.diagram)}`;
        link.href = target;
        link.className = 'list-group-item list-group-item-action';
        link.textContent = n.message;
        link.addEventListener('click', () => {
          markDiagramRead(n.diagram, n.time);
          if (badge) {
            const count = parseInt(badge.textContent, 10) - 1;
            if (count > 0) badge.textContent = count;
            else badge.style.display = 'none';
          }
        });
        panel.appendChild(link);
      });
      if (badge) {
        badge.textContent = unread.length;
        badge.style.display = 'inline-block';
      }
    }
  } catch (e) {
    console.error('Failed to load messages', e);
    panel.innerHTML = "<p class='text-danger'>⚠️ Σφάλμα φόρτωσης μηνυμάτων.</p>";
  }
}

function toggleMessages() {
  const panel = document.getElementById('msgPanel');
  if (!panel) return;
  panel.classList.toggle('open');
  if (panel.classList.contains('open')) {
    loadMessages();
  }
}

document.addEventListener('click', (e) => {
  const panel = document.getElementById('msgPanel');
  const btn = document.querySelector('.navbar-msg');
  if (!panel || !btn) return;
  if (panel.classList.contains('open')) {
    if (!panel.contains(e.target) && !btn.contains(e.target)) {
      panel.classList.remove('open');
    }
  }
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    const panel = document.getElementById('msgPanel');
    if (panel && panel.classList.contains('open')) {
      panel.classList.remove('open');
    }
  }
});

document.addEventListener('DOMContentLoaded', () => {
  loadMessages();
});