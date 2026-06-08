import axios from "axios";

// API pública temporal del backend de Naomi por LocalTunnel
const api = axios.create({
  baseURL: "https://neurotutor.loca.lt/api",
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
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
        "❌ No hay respuesta del servidor. Verifica que LocalTunnel y Spring Boot estén activos."
      );
    } else {
      console.error("❌ Error:", error.message);
    }

    return Promise.reject(error);
  }
);

export default api;