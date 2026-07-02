(function () {
  const originalFetch = window.fetch.bind(window);

  function storeEmployee(employee) {
    localStorage.setItem('employeeId', employee.id);
    localStorage.setItem('employeeName', employee.fullName);
    localStorage.setItem('employeePosition', employee.position ?? '');
  }

  function clearAuth() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('employeeId');
    localStorage.removeItem('employeeName');
    localStorage.removeItem('employeePosition');
  }

  function parseUrl(resource) {
    const url = typeof resource === 'string' ? resource : resource.url;
    return new URL(url, window.location.origin);
  }

  function isApiRequest(resource) {
    const parsed = parseUrl(resource);
    return parsed.origin === window.location.origin && parsed.pathname.startsWith('/api/');
  }

  function isLoginRequest(resource) {
    const parsed = parseUrl(resource);
    return parsed.origin === window.location.origin && parsed.pathname === '/api/login';
  }

  function isCurrentUserRequest(resource) {
    const parsed = parseUrl(resource);
    return parsed.origin === window.location.origin && parsed.pathname === '/api/me';
  }

  async function refreshCurrentUser() {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return null;
    }

    const response = await originalFetch('/api/me', {
      headers: { Authorization: `Bearer ${token}` }
    });

    if (!response.ok) {
      return null;
    }

    const employee = await response.json();
    storeEmployee(employee);
    return employee;
  }

  window.fetch = async function (resource, options = {}) {
    const token = localStorage.getItem('authToken');

    if (token && isApiRequest(resource) && !isLoginRequest(resource)) {
      const headers = new Headers(options.headers || {});
      headers.set('Authorization', `Bearer ${token}`);
      options = { ...options, headers };
    }

    const response = await originalFetch(resource, options);

    if (response.status === 401 && !isLoginRequest(resource) && !isCurrentUserRequest(resource)) {
      clearAuth();
      window.location.href = 'login.html';
    }

    return response;
  };

  window.authReady = refreshCurrentUser().catch(() => null);
})();
