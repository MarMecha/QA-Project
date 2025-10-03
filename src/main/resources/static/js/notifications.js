function notifStorageKey() {
  const name = localStorage.getItem('employeeName') || 'anon';
  return `readNotifKeys_${name}`;
}

function getNotifReadMap() {
  try {
    return JSON.parse(localStorage.getItem(notifStorageKey()) || '{}');
  } catch {
    return {};
  }
}

function markNotifRead(key) {
  const map = getNotifReadMap();
  map[key] = true;
  localStorage.setItem(notifStorageKey(), JSON.stringify(map));
}

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
    const badge = document.getElementById('notifBadge');
    const readMap = getNotifReadMap();
    const unread = data.filter(n => {
      const key = `${n.message}|${n.diagram}`;
      return !readMap[key];
    });
    if (unread.length === 0) {
      panel.innerHTML = "<p class='text-muted'>Δεν υπάρχουν ειδοποιήσεις.</p>";
      if (badge) badge.style.display = 'none';
    } else {
      panel.innerHTML = '';
      unread.forEach(n => {
        const key = `${n.message}|${n.diagram}`;
        const link = document.createElement('a');
        const target = position === 'qa'
          ? `edit-dashboard.html#modeler&name=${encodeURIComponent(n.diagram)}`
          : `modeler.html?name=${encodeURIComponent(n.diagram)}`;
        link.href = target;
        link.className = 'list-group-item list-group-item-action';
        link.textContent = n.message;
        link.addEventListener('click', () => {
          markNotifRead(key);
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

document.addEventListener('DOMContentLoaded', () => {
  loadNotifications();
});