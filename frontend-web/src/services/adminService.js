const localDjangoAdminUrl = `${window.location.protocol}//${window.location.hostname}:8000/api/admin`;
const ADMIN_API_BASE_URL = (
  import.meta.env.VITE_DJANGO_ADMIN_API_URL || localDjangoAdminUrl
).replace(/\/$/, "");

const requestAdminResource = async (path, options = {}) => {
  const response = await fetch(`${ADMIN_API_BASE_URL}${path}`, {
    ...options,
    // Django admin authentication is session-based. Every request must carry sessionid.
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    const payload = await response.json().catch(() => null);
    const error = new Error(payload?.detail || "No se pudo completar la solicitud administrativa.");
    error.status = response.status;
    throw error;
  }

  return response.status === 204 ? null : response.json();
};

export const loginAdmin = (credentials) => requestAdminResource("/login/", {
  method: "POST",
  body: JSON.stringify({
    username: credentials.username || credentials.email,
    password: credentials.password,
  }),
});

export const logoutAdmin = () => requestAdminResource("/logout/", { method: "POST" });

export const getCurrentAdmin = () => requestAdminResource("/me/");

export const getAdminSummary = () => requestAdminResource("/summary/");

export const getAdminStudents = () => requestAdminResource("/students/");

export const getAdminStudentById = (id) => requestAdminResource(`/students/${id}/`);
