import axios from 'axios';

// Configuración base de la API (Spring Boot de Naomi)
const api = axios.create({
  baseURL: 'http://10.200.168.93:8085/',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 segundos máximo
});

// Interceptor para manejar errores globalmente
api.interceptors.response.use(
  (response) => {
    console.log('✅ Respuesta exitosa:', response.status);
    return response;
  },
  (error) => {
    if (error.code === 'ECONNABORTED') {
      console.error('❌ Tiempo de espera agotado');
    } else if (error.response) {
      console.error('❌ Error del servidor:', error.response.status, error.response.data);
    } else if (error.request) {
      console.error('❌ No hay respuesta del servidor. ¿Spring Boot está corriendo?');
    } else {
      console.error('❌ Error:', error.message);
    }
    return Promise.reject(error);
  }
);

export default api;