const BASE = import.meta.env.VITE_API_URL || '/api';
async function request(path, options = {}) {
  const response = await fetch(BASE + path, options);
  if (!response.ok) {
    let message = 'Request failed (' + response.status + ')';
    try { const body = await response.json(); message = body.message || body.error || message; }
    catch { /* non-JSON response */ }
    throw new Error(message);
  }
  return response.json();
}
export function fetchMeta() { return request('/meta'); }
export function fetchJobs(filters = {}) {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) params.set(key, value);
  });
  return request('/jobs?' + params.toString());
}

export function triggerCollection(city, token) {
  const params = new URLSearchParams();
  if (city) params.set('city', city);
  return request('/admin/collect?' + params.toString(), {
    method: 'POST',
    headers: { 'X-JobRadar-Admin-Token': token }
  });
}
