const BASE = 'http://localhost:8080/api/employees';

function authHeaders() {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function handleResponse(res) {
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json();
}

/** GET /api/employees */
export const fetchAllEmployees = () =>
  fetch(BASE, { headers: authHeaders() }).then(handleResponse);

/** GET /api/employees/paged */
export const fetchEmployeesPaged = (params = {}) => {
  const qs = new URLSearchParams({
    page: params.page ?? 0,
    size: params.size ?? 20,
    sort: params.sort ?? 'empId',
    dir:  params.dir  ?? 'asc',
    ...(params.q      ? { q:      params.q      } : {}),
    ...(params.deptId ? { deptId: params.deptId } : {}),
    ...(params.status ? { status: params.status } : {}),
  });
  return fetch(`${BASE}/paged?${qs}`, { headers: authHeaders() }).then(handleResponse);
};

/** POST /api/employees */
export const createEmployee = (data) =>
  fetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(data),
  }).then(handleResponse);

/** PUT /api/employees/:id */
export const updateEmployee = (id, data) =>
  fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(data),
  }).then(handleResponse);

/** DELETE /api/employees/:id */
export const deleteEmployee = (id) =>
  fetch(`${BASE}/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  }).then(res => { if (!res.ok) throw new Error(`HTTP ${res.status}`); });
