import api from './api';

const API_URL = '';

/**
 * Registra un nuevo estudiante
 * @param {Object} userData - Datos del usuario
 * @returns {Promise<Object>} - Respuesta del servidor
 */
export const register = async (userData) => {
  try {
    const response = await api.post('/api/register', userData);
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    let errorMessage = 'Error al registrar usuario';
    
    if (error.response) {
      // El servidor respondiÃ³ con un error
      if (error.response.status === 400) {
        errorMessage = error.response.data?.message || 'Datos invÃ¡lidos';
      } else if (error.response.status === 409) {
        errorMessage = 'Este correo ya estÃ¡ registrado';
      } else if (error.response.status === 500) {
        errorMessage = 'Error interno del servidor';
      } else {
        errorMessage = error.response.data?.message || errorMessage;
      }
      errorMessage = error.response.data?.error || error.response.data?.message || errorMessage;
      if (error.response.status === 400 && !error.response.data?.error && !error.response.data?.message) {
        errorMessage = 'Datos inválidos';
      }
    } else if (error.request) {
      errorMessage = 'No se pudo conectar con el servidor. Â¿Spring Boot estÃ¡ corriendo?';
    }
    
    return {
      success: false,
      message: errorMessage,
      error: error.response?.data
    };
  }
};
/**
 * Inicia sesiÃ³n de usuario (para HU-02)
 * @param {Object} credentials - Credenciales del usuario
 * @returns {Promise<Object>} - Respuesta del servidor
 */
export const login = async (credentials) => {
  try {
    const response = await api.post('/api/login', credentials);
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    let errorMessage = 'Error al iniciar sesiÃ³n';
    
    if (error.response) {
      if (error.response.status === 401) {
        errorMessage = 'Usuario o contraseÃ±a incorrectos';
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
export const forgotPassword = async (email) => {
  try {
    const response = await api.post('/api/forgot-password', { email });
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    const responseData = error.response?.data;
    const serverMessage = typeof responseData === 'string'
      ? responseData
      : responseData?.message || responseData?.error || responseData?.detail;

    return {
      success: false,
      message: serverMessage || 'No se pudo solicitar la recuperacion',
      error: responseData
    };
  }
};

export const resetPassword = async (payload) => {
  try {
    const response = await api.post('/api/reset-password', payload);
    return {
      success: true,
      data: response.data,
      status: response.status
    };
  } catch (error) {
    return {
      success: false,
      message: error.response?.data?.message || error.response?.data?.error || 'No se pudo restablecer la contraseña',
      error: error.response?.data
    };
  }
};

/**
 * Cierra sesion de usuario.
 * @returns {Promise<Object>} - Respuesta del servidor
 */
export const logout = async () => {
  try {
    const response = await api.post(`${API_URL}/logout`);
    return {
      success: true,
      data: response.data
    };
  } catch {
    return {
      success: false,
      message: 'Error al cerrar sesiÃ³n'
    };
  }
};
