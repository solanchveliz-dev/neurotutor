import axios from "axios";

const API_BASE_URL = (
  import.meta.env.VITE_API_URL || "https://neurotutor-production.up.railway.app"
).replace(/\/$/, "");

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 30000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const publicAuthPaths = ["/api/register", "/api/login", "/api/forgot-password", "/api/reset-password"];
  const isPublicAuthRequest = publicAuthPaths.some((path) => config.url?.endsWith(path));
  if (token && !isPublicAuthRequest) config.headers.Authorization = `Bearer ${token}`;
  if (isPublicAuthRequest) delete config.headers.Authorization;
  return config;
});

api.interceptors.response.use(
  (response) => {
    console.log("✅ Respuesta exitosa:", response.status);
    return response;
  },
  (error) => {
    if (error.code === "ECONNABORTED") {
      console.error("❌ Tiempo de espera agotado");
    } else if (error.response) {
      console.error(
        "❌ Error del servidor:",
        error.response.status,
        error.response.data
      );
    } else if (error.request) {
      console.error(
        "❌ No hay respuesta del servidor. Verifica que el backend Railway esté activo y CORS configurado."
      );
    } else {
      console.error("❌ Error:", error.message);
    }

    return Promise.reject(error);
  }
);

export default api;

export const logApiError = (error) => {
  console.error("ERROR API:", error);
  console.error("STATUS:", error?.response?.status);
  console.error("DATA:", error?.response?.data);
  console.error("URL:", error?.config?.url);
  console.error("BASE URL:", error?.config?.baseURL);
};
