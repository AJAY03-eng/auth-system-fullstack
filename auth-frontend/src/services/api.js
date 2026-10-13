
const API_BASE_URL = "http://localhost:8080/api";

export async function registerUser(formData) {
  return sendRequest("/auth/register", {
    method: "POST",
    body: JSON.stringify(formData),
  });
}

export async function loginUser(formData) {
  return sendRequest("/auth/login", {
    method: "POST",
    body: JSON.stringify(formData),
  });
}

export async function getCurrentUser(token) {
  return sendRequest("/auth/me", {
    method: "GET",
    token,
  });
}

async function sendRequest(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
  };

  if (options.token) {
    headers.Authorization = `Bearer ${options.token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method || "GET",
    headers,
    ...(options.body ? { body: options.body } : {}),
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      data.message || `Request failed (${response.status})`
    );
  }

  return data;
}

