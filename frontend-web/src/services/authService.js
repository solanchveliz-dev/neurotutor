import api from './api';

const API_URL = '/auth';

/**
 * Registra un nuevo estudiante
 * @param {Object} userData - Datos del usuario
 * @returns {Promise<Object>} - Respuesta del servidor
 */
export const register = async (userData) => {
  try {
    const response = await api.post(`${API_URL}/register`, userData);
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    let errorMessage = 'Error al registrar usuario';
    
    if (error.response) {
      // El servidor respondió con un error
      if (error.response.status === 400) {
        errorMessage = error.response.data?.message || 'Datos inválidos';
      } else if (error.response.status === 409) {
        errorMessage = 'Este correo ya está registrado';
      } else if (error.response.status === 500) {
        errorMessage = 'Error interno del servidor';
      } else {
        errorMessage = error.response.data?.message || errorMessage;
      }
    } else if (error.request) {
      errorMessage = 'No se pudo conectar con el servidor. ¿Spring Boot está corriendo?';
    }
    
    return {
      success: false,
      message: errorMessage,
      error: error.response?.data
    };
  }
};

/**
 * Inicia sesión de usuario (para HU-02)
 * @param {Object} credentials - Credenciales del usuario
 * @returns {Promise<Object>} - Respuesta del servidor
 */
export const login = async (credentials) => {
  try {
    const response = await api.post(`${API_URL}/login`, credentials);
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    let errorMessage = 'Error al iniciar sesión';
    
    if (error.response) {
      if (error.response.status === 401) {
        errorMessage = 'Usuario o contraseña incorrectos';
      } else if (error.response.status === 423) {
        errorMessage = 'Cuenta bloqueada por 15 minutos';
      } else {
        errorMessage = error.response.data?.message || errorMessage;
      }
    } else if (error.request) {
      errorMessage = 'No se pudo conectar con el servidor';
    }
    
    return {
      success: false,
      message: errorMessage,
      error: error.response?.data
    };
  }
};

/**
 * Cierra sesión de usuario (para HU-04)
 * @returns {Promise<Object>} - Respuesta del servidor
 */
export const logout = async () => {
  try {
    const response = await api.post(`${API_URL}/logout`);
    return {
      success: true,
      data: response.data
    };
  } catch (error) {
    return {
      success: false,
      message: 'Error al cerrar sesión'
    };
  }
};