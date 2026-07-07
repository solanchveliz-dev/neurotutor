const localDjangoAdminUrl = `${window.location.protocol}//${window.location.hostname}:8000/api/admin`;
const ADMIN_API_BASE_URL = (
  import.meta.env.VITE_DJANGO_ADMIN_API_URL || localDjangoAdminUrl
).replace(/\/$/, "");

const ADMIN_ACCESS_TOKEN_KEY = "admin_access_token";
const ADMIN_REFRESH_TOKEN_KEY = "admin_refresh_token";
const ADMIN_USER_KEY = "admin_user";

const getAdminAccessToken = () => localStorage.getItem(ADMIN_ACCESS_TOKEN_KEY);

const saveAdminAuthData = (payload) => {
  const accessToken = payload?.access || payload?.access_token || payload?.token;
  const refreshToken = payload?.refresh || payload?.refresh_token;

  if (accessToken) {
    localStorage.setItem(ADMIN_ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem("access_token", accessToken);
  }

  if (refreshToken) {
    localStorage.setItem(ADMIN_REFRESH_TOKEN_KEY, refreshToken);
    localStorage.setItem("refresh_token", refreshToken);
  }

  if (payload) {
    localStorage.setItem(ADMIN_USER_KEY, JSON.stringify(payload));
  }

  console.info("[admin auth] access token after login:", accessToken ? "present" : "missing");
};

const clearAdminAuthData = () => {
  localStorage.removeItem(ADMIN_ACCESS_TOKEN_KEY);
  localStorage.removeItem(ADMIN_REFRESH_TOKEN_KEY);
  localStorage.removeItem(ADMIN_USER_KEY);
  localStorage.removeItem("access_token");
  localStorage.removeItem("refresh_token");
  localStorage.removeItem("adminToken");
};

const requestAdminResource = async (path, options = {}) => {
  const accessToken = getAdminAccessToken();
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  if (accessToken) {
    headers.Authorization = `Bearer ${accessToken}`;
  }

  if (path === "/me/") {
    console.info("[admin auth] /me URL:", `${ADMIN_API_BASE_URL}${path}`);
    console.info("[admin auth] Authorization sent:", Boolean(headers.Authorization));
  }

  if (path === "/summary/" || path === "/students/" || path.startsWith("/students/") || path.startsWith("/chat/")) {
    console.info("[admin api] URL:", `${ADMIN_API_BASE_URL}${path}`);
    console.info("[admin api] Authorization sent:", Boolean(headers.Authorization));
  }

  const response = await fetch(`${ADMIN_API_BASE_URL}${path}`, {
    ...options,
    // Supports admin Bearer tokens and keeps Django session cookies as a fallback.
    credentials: "include",
    headers,
  });

  if (!response.ok) {
    const payload = await response.json().catch(() => null);
    console.error("[admin api] Backend error:", response.status, payload);
    const message = response.status === 401
      ? "Sesión de administrador expirada. Vuelve a iniciar sesión."
      : payload?.detail || "No se pudo completar la solicitud administrativa.";
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  return response.status === 204 ? null : response.json();
};

export const loginAdmin = async (credentials) => {
  clearAdminAuthData();
  const payload = await requestAdminResource("/login/", {
    method: "POST",
    body: JSON.stringify({
      username: credentials.username || credentials.email,
      password: credentials.password,
    }),
  });
  saveAdminAuthData(payload);
  return payload;
};

export const logoutAdmin = async () => {
  try {
    return await requestAdminResource("/logout/", { method: "POST" });
  } finally {
    clearAdminAuthData();
  }
};

export const getCurrentAdmin = () => requestAdminResource("/me/");

export const getAdminSummary = () => requestAdminResource("/summary/");

export const getAdminStudents = () => requestAdminResource("/students/");

export const getAdminStudentById = (id) => requestAdminResource(`/students/${id}/`);

export const getAdminChatConversations = () => requestAdminResource("/chat/conversations/");

export const getAdminChatStudent = (id) => requestAdminResource(`/chat/student/${id}/`);

export const getAdminChatStatistics = () => requestAdminResource("/chat/statistics/");

export const deleteAdminChatConversation = (conversationId) => (
  requestAdminResource(`/chat/conversations/${encodeURIComponent(conversationId)}/`, {
    method: "DELETE",
  })
);
