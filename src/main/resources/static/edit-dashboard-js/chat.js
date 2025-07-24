// --- Chat Functions --- //
async function loadChatMessages(name) {
  if (!name) return;
  try {
    const res = await fetch(`/api/chat/${encodeURIComponent(name)}`);
    if (!res.ok) throw new Error(await res.text());
    const messages = await res.json();
    const chatBox = document.getElementById('chatMessages');
    chatBox.innerHTML = '';
    messages.forEach(msg => {
      const div = document.createElement('div');
      div.innerHTML = `<strong>${msg.sender || 'Χρήστης'}:</strong> ${msg.message}<br><small class="text-muted">${new Date(msg.sentAt).toLocaleString('el-GR')}</small>`;
      chatBox.appendChild(div);
    });
  } catch (err) {
    console.error('❌ Σφάλμα φόρτωσης chat:', err);
  }
}

document.getElementById('sendChatBtn').addEventListener('click', async () => {
  const messageInput = document.getElementById('chatInput');
  const messageText = messageInput.value.trim();
  const senderName = localStorage.getItem('employeeName');
  const processName = diagramName;
  if (!messageText || !senderName || !processName) return;
  const messageData = {
    name: processName,
    message: messageText,
    sender: senderName
  };
  try {
    const res = await fetch('/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(messageData)
    });
    if (res.ok) {
      messageInput.value = '';
      await loadChatMessages(processName);
    } else {
      alert('❌ Σφάλμα αποστολής μηνύματος');
    }
  } catch (err) {
    console.error('❌ Σφάλμα API:', err);
  }
});

document.getElementById('chatModal').addEventListener('shown.bs.modal', () => {
  loadChatMessages(diagramName);
});

const paletteToggle = document.getElementById('paletteToggle');
const chatModal = document.getElementById('chatModal');
chatModal.addEventListener('show.bs.modal', () => {
  paletteToggle.style.display = 'none';
});
chatModal.addEventListener('hidden.bs.modal', () => {
  paletteToggle.style.display = 'inline-block';
});