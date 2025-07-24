// ----Modeler--- //

let bpmnModeler;

async function initModelerTab() {
  if (bpmnModeler) return; // μην το ξαναφορτώνεις

  bpmnModeler = new BpmnJS({
    container: '#canvas',
    moddleExtensions: { qa: qaModdle, camunda: camundaModdle }
  });

  const emptyDiagram = `<?xml version="1.0" encoding="UTF-8"?>
    <bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                      xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                      xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                      id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
      <bpmn:process id="Process_1" isExecutable="true">
        <bpmn:startEvent id="StartEvent_1"/>
      </bpmn:process>
      <bpmndi:BPMNDiagram id="BPMNDiagram_1">
        <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
          <bpmndi:BPMNShape id="StartEvent_1_di" bpmnElement="StartEvent_1">
            <dc:Bounds x="100" y="100" width="36" height="36"/>
          </bpmndi:BPMNShape>
        </bpmndi:BPMNPlane>
      </bpmndi:BPMNDiagram>
    </bpmn:definitions>`;

  await bpmnModeler.importXML(emptyDiagram);
  renderAssigneeOverlays();
  renderCompletionOverlays(); 

  let currentZoom = 1;
  const canvas = bpmnModeler.get('canvas');
  const commandStack = bpmnModeler.get('commandStack');

  const updateZoomDisplay = () => {
    document.getElementById('zoomDisplay').textContent = Math.round(currentZoom * 100) + '%';
  };

  const setZoom = (z) => {
    currentZoom = Math.max(0.2, Math.min(4, z));
    canvas.zoom(currentZoom);
    updateZoomDisplay();
  };

  // Zoom & History
  document.getElementById('zoomInBtn').addEventListener('click', () => setZoom(currentZoom + 0.1));
  document.getElementById('zoomOutBtn').addEventListener('click', () => setZoom(currentZoom - 0.1));
  document.getElementById('undoBtn').addEventListener('click', () => commandStack.undo());
  document.getElementById('redoBtn').addEventListener('click', () => commandStack.redo());

  updateZoomDisplay();

  // Palette toggle
  const paletteEl = document.querySelector('.djs-palette');
  if (paletteEl) paletteEl.classList.add('hidden');

  document.getElementById('paletteToggle').addEventListener('click', () => {
    if (paletteEl) paletteEl.classList.toggle('hidden');
  });

  const eventBus = bpmnModeler.get('eventBus');
  eventBus.on('commandStack.changed', scheduleAutoSave);
  eventBus.on('element.click', e => {
    const element = e.element;
    if (element.type === 'bpmn:UserTask') {
      showEmployeeSelect(element, e.originalEvent);
    } else {
      hideEmployeeSelect();
    }
  });
  eventBus.on('element.changed', e => {
    if (e.element.type === 'bpmn:UserTask') {
      updateAssigneeOverlay(e.element);
      updateCompletionOverlay(e.element);
    }
  });
}

function showEmployeeSelect(element, evt) {
  const select = document.getElementById('employeeSelect');
  select.innerHTML = '<option value="">-- Εκχώρηση --</option>';
  employeeList.forEach(emp => {
    const opt = document.createElement('option');
    opt.value = emp.fullName;
    opt.textContent = emp.fullName;
    select.appendChild(opt);
  });
  select.value = element.businessObject.get('camunda:assignee') || '';
  select.style.left = evt.pageX + 'px';
  select.style.top  = evt.pageY + 'px';
  select.classList.remove('d-none');

  const completeBtn = document.getElementById('completeBtn');
  completeBtn.textContent = element.businessObject.get('qa:completed')
    ? 'Μη ολοκληρωμένο'
    : 'Ολοκληρώθηκε';
  completeBtn.style.left = (evt.pageX + 210) + 'px';
  completeBtn.style.top  = evt.pageY + 'px';
  completeBtn.classList.remove('d-none');
  completeBtn.onclick = async () => {
    const modeling = bpmnModeler.get('modeling');
    const currently = element.businessObject.get('qa:completed');
    modeling.updateProperties(element, { 'qa:completed': !currently });

    updateCompletionOverlay(element);

    completeBtn.classList.add('d-none');
    select.classList.add('d-none');

  // Αποθήκευση
    const assignee = element.businessObject.get('camunda:assignee') || '';
    try {
      await fetch('/api/bpmn/assign-status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ diagramName, taskId: element.id, assignee, completed: !currently })
      });
    } catch (err) {
      console.error('❌ Σφάλμα αποθήκευσης κατάστασης', err);
    }

    await autoSave();
  };


  select.onchange = async () => {
    if (select.value) {
      const modeling = bpmnModeler.get('modeling');
      modeling.updateProperties(element, { 'camunda:assignee': select.value });
      updateAssigneeOverlay(element);

      try {
        await fetch('/api/bpmn/assign-status', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ diagramName, taskId: element.id, assignee: select.value, completed: element.businessObject.get('qa:completed') || false })
        });
      } catch (err) {
        console.error('❌ Σφάλμα αποθήκευσης εκχώρησης', err);
      }

      await autoSave();
    }
    select.classList.add('d-none');
    document.getElementById('completeBtn').classList.add('d-none');
  };
}

function hideEmployeeSelect() {
  document.getElementById('employeeSelect').classList.add('d-none');
  document.getElementById('completeBtn').classList.add('d-none');
}

function updateAssigneeOverlay(element) {
  const overlays = bpmnModeler.get('overlays');
  const existing = assigneeOverlays[element.id];
  if (existing) overlays.remove(existing);

  const assignee = element.businessObject.get('camunda:assignee');
  if (assignee) {
    const div = document.createElement('div');
    div.className = 'assignee-label';
    div.textContent = assignee;
    const id = overlays.add(element, {
      position: { bottom: 2, right: 2 },
      html: div
    });
    assigneeOverlays[element.id] = id;
  }
}

function renderAssigneeOverlays() {
  const registry = bpmnModeler.get('elementRegistry');
  registry.filter(e => e.type === 'bpmn:UserTask').forEach(updateAssigneeOverlay);
}

function updateCompletionOverlay(element) {
  const overlays = bpmnModeler.get('overlays');
  const existing = completionOverlays[element.id];
  if (existing) overlays.remove(existing);

  const completed = element.businessObject.get('qa:completed');
  if (completed) {
    const div = document.createElement('div');
    div.className = 'completed-label';
    div.textContent = '✓ Ολοκληρώθηκε';
    const id = overlays.add(element, {
      position: { top: -10, right: 2 },
      html: div
    });
    completionOverlays[element.id] = id;
  }
}

function renderCompletionOverlays() {
  const registry = bpmnModeler.get('elementRegistry');
  registry.filter(e => e.type === 'bpmn:UserTask').forEach(updateCompletionOverlay);
}
