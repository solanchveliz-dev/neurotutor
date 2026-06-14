import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from '../styles/Register.module.css';
import { register, login } from '../services/authService';

function Register({ initialTab = 'register' }) {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState(initialTab);
  const [role, setRole] = useState('ESTUDIANTE');
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    grado: '',
    seccion: '',
    password: '',
    confirmPassword: ''
  });

  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [serverError, setServerError] = useState('');

  useEffect(() => {
    setActiveTab(initialTab);
  }, [initialTab]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Limpiar error del campo cuando el usuario escribe
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
    // Limpiar errores generales
    if (serverError) setServerError('');
    if (successMessage) setSuccessMessage('');
  };

  const validateForm = () => {
    const newErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (activeTab === 'login') {
      if (!formData.email.trim()) {
        newErrors.email = 'El correo electrónico es obligatorio';
      } else if (!emailRegex.test(formData.email)) {
        newErrors.email = 'Correo inválido (ej: usuario@dominio.com)';
      }

      if (!formData.password) {
        newErrors.password = 'La contraseña es obligatoria';
      } else if (formData.password.length < 8) {
        newErrors.password = 'La contraseña debe tener al menos 8 caracteres';
      }
    } else {
      // Nombre
      if (!formData.nombre.trim()) {
        newErrors.nombre = 'El nombre completo es obligatorio';
      }

      // Email
      if (!formData.email.trim()) {
        newErrors.email = 'El correo electrónico es obligatorio';
      } else if (!emailRegex.test(formData.email)) {
        newErrors.email = 'Correo inválido (ej: usuario@dominio.com)';
      }

      // Grado
      if (!formData.grado) {
        newErrors.grado = 'Seleccione un grado';
      }

      // Sección
      if (!formData.seccion) {
        newErrors.seccion = 'Seleccione una sección';
      }

      // Contraseña
      if (!formData.password) {
        newErrors.password = 'La contraseña es obligatoria';
      } else if (formData.password.length < 8) {
        newErrors.password = 'La contraseña debe tener al menos 8 caracteres';
      }

      // Confirmar contraseña
      if (formData.password !== formData.confirmPassword) {
        newErrors.confirmPassword = 'Las contraseñas no coinciden';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setIsLoading(true);
    setServerError('');
    setSuccessMessage('');

    if (activeTab === 'login') {
      const result = await login({
        email: formData.email.trim().toLowerCase(),
        password: formData.password
      });

      if (result.success) {
        localStorage.setItem('token', result.data.token);
        localStorage.setItem('user', JSON.stringify(result.data.user || result.data));

        const userRol = result.data.rol || result.data.user?.rol;
        if (userRol === 'ESTUDIANTE') {
          navigate('/dashboard');
        } else if (userRol === 'DOCENTE') {
          navigate('/teacher-dashboard');
        } else if (userRol === 'ADMIN') {
          navigate('/admin-dashboard');
        } else {
          navigate('/dashboard');
        }
      } else {
        setServerError(result.message || 'Usuario o contraseña incorrectos');
      }

      setIsLoading(false);
      return;
    }

    // Datos para enviar al backend
    const dataToSend = {
      email: formData.email.trim().toLowerCase(),
      nombreCompleto: formData.nombre.trim(),
      grado: formData.grado,
      seccion: formData.seccion,
      password: formData.password,
      password2: formData.confirmPassword
    };

    console.log('📤 Enviando registro a Spring Boot:', dataToSend);

    const result = await register(dataToSend);

    if (result.success) {
      console.log('✅ Registro exitoso:', result.data);
      setSuccessMessage('✅ ¡Registro exitoso! Redirigiendo al examen diagnóstico...');
      
      // Limpiar formulario
      setFormData({
        nombre: '',
        email: '',
        grado: '',
        seccion: '',
        password: '',
        confirmPassword: ''
      });
      
      // Redirigir al diagnóstico después de 2 segundos
      setTimeout(() => {
        window.location.href = '/diagnostic-exam';
      }, 2000);
    } else {
      console.error('❌ Error en registro:', result.message);
      setServerError(result.message);
    }
    
    setIsLoading(false);
  };

  // Verificar si el formulario es válido para habilitar el botón
  const isFormValid = () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (activeTab === 'login') {
      return (
        formData.email.trim() !== '' &&
        emailRegex.test(formData.email) &&
        formData.password.length >= 8
      );
    }

    return (
      formData.nombre.trim() !== '' &&
      formData.email.trim() !== '' &&
      emailRegex.test(formData.email) &&
      formData.grado !== '' &&
      formData.seccion !== '' &&
      formData.password.length >= 8 &&
      formData.confirmPassword !== '' &&
      formData.password === formData.confirmPassword
    );
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>{activeTab === 'login' ? 'Iniciar Sesión' : 'Crear Cuenta'}</h1>
        <p className={styles.subtitle}>{activeTab === 'login' ? 'Ingresa con tu correo y contraseña' : 'Regístrate para comenzar tu aprendizaje'}</p>

        <div className={styles.toggleWrapper}>
          <button
            type="button"
            className={`${styles.toggleButton} ${activeTab === 'login' ? styles.activeToggle : ''}`}
            onClick={() => navigate('/login')}
          >
            Iniciar Sesión
          </button>
          <button
            type="button"
            className={`${styles.toggleButton} ${activeTab === 'register' ? styles.activeToggle : ''}`}
            onClick={() => navigate('/register')}
          >
            Registrarse
          </button>
        </div>

        {/* Mensaje de éxito */}
        {successMessage && (
          <div className={styles.successMessage}>{successMessage}</div>
        )}

        {/* Mensaje de error del servidor */}
        {serverError && (
          <div className={styles.errorMessage}>{serverError}</div>
        )}

        {activeTab === 'login' ? (
          <form onSubmit={handleSubmit}>
            <div className={styles.rolesContainer}>
              <button
                type="button"
                className={`${styles.roleButton} ${role === 'ESTUDIANTE' ? styles.activeRole : ''}`}
                onClick={() => setRole('ESTUDIANTE')}
              >
                👨‍🎓 Estudiante
              </button>
              <button
                type="button"
                className={`${styles.roleButton} ${role === 'DOCENTE' ? styles.activeRole : ''}`}
                onClick={() => setRole('DOCENTE')}
              >
                👩‍🏫 Docente
              </button>
              <button
                type="button"
                className={`${styles.roleButton} ${role === 'ADMIN' ? styles.activeRole : ''}`}
                onClick={() => setRole('ADMIN')}
              >
                👑 Administrador
              </button>
            </div>

            <div className={styles.field}>
              <label>Correo electrónico</label>
              <input
                type="email"
                name="email"
                placeholder="Código de estudiante o email"
                value={formData.email}
                onChange={handleChange}
                className={errors.email ? styles.errorInput : ''}
              />
              {errors.email && <span className={styles.errorText}>{errors.email}</span>}
            </div>

            <div className={styles.field}>
              <label>Contraseña</label>
              <input
                type="password"
                name="password"
                placeholder="Contraseña"
                value={formData.password}
                onChange={handleChange}
                className={errors.password ? styles.errorInput : ''}
              />
              {errors.password && <span className={styles.errorText}>{errors.password}</span>}
            </div>

            <div className={styles.forgotLink}>
              <Link to="/forgot-password">¿Olvidaste tu contraseña?</Link>
            </div>

            <button
              type="submit"
              disabled={!isFormValid() || isLoading}
              className={styles.button}
            >
              {isLoading ? 'Ingresando...' : 'INGRESAR'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleSubmit}>
            {/* Campo: Nombre */}
            <div className={styles.field}>
              <label>Nombre completo</label>
              <input
                type="text"
                name="nombre"
                placeholder="Ej: María González"
                value={formData.nombre}
                onChange={handleChange}
                className={errors.nombre ? styles.errorInput : ''}
              />
              {errors.nombre && <span className={styles.errorText}>{errors.nombre}</span>}
            </div>

            {/* Campo: Email */}
            <div className={styles.field}>
              <label>Correo electrónico</label>
              <input
                type="email"
                name="email"
                placeholder="usuario@ejemplo.com"
                value={formData.email}
                onChange={handleChange}
                className={errors.email ? styles.errorInput : ''}
              />
              {errors.email && <span className={styles.errorText}>{errors.email}</span>}
            </div>

            {/* Fila: Grado y Sección */}
            <div className={styles.row}>
              <div className={styles.field}>
                <label>Grado</label>
                <select
                  name="grado"
                  value={formData.grado}
                  onChange={handleChange}
                  className={errors.grado ? styles.errorInput : ''}
                >
                  <option value="">Selecciona</option>
                  <option value="4to">4to grado</option>
                  <option value="5to">5to grado</option>
                  <option value="6to">6to grado</option>
                  <option value="7mo">7mo grado</option>
                </select>
                {errors.grado && <span className={styles.errorText}>{errors.grado}</span>}
              </div>

              <div className={styles.field}>
                <label>Sección</label>
                <select
                  name="seccion"
                  value={formData.seccion}
                  onChange={handleChange}
                  className={errors.seccion ? styles.errorInput : ''}
                >
                  <option value="">Selecciona</option>
                  <option value="A">A</option>
                  <option value="B">B</option>
                  <option value="C">C</option>
                  <option value="D">D</option>
                </select>
                {errors.seccion && <span className={styles.errorText}>{errors.seccion}</span>}
              </div>
            </div>

            {/* Campo: Contraseña */}
            <div className={styles.field}>
              <label>Contraseña</label>
              <input
                type="password"
                name="password"
                placeholder="Mínimo 8 caracteres"
                value={formData.password}
                onChange={handleChange}
                className={errors.password ? styles.errorInput : ''}
              />
              {errors.password && <span className={styles.errorText}>{errors.password}</span>}
            </div>

            {/* Campo: Confirmar Contraseña */}
            <div className={styles.field}>
              <label>Confirmar contraseña</label>
              <input
                type="password"
                name="confirmPassword"
                placeholder="Repite tu contraseña"
                value={formData.confirmPassword}
                onChange={handleChange}
                className={errors.confirmPassword ? styles.errorInput : ''}
              />
              {errors.confirmPassword && <span className={styles.errorText}>{errors.confirmPassword}</span>}
            </div>

            {/* Botón de registro */}
            <button 
              type="submit" 
              disabled={!isFormValid() || isLoading}
              className={styles.button}
            >
              {isLoading ? 'Registrando...' : 'REGISTRARSE'}
            </button>
          </form>
        )}

        <p className={styles.loginLink}>
          {activeTab === 'login' ? (
            <>¿No tienes cuenta? <Link to="/register">Crea una nueva</Link></>
          ) : (
            <>¿Ya tienes cuenta? <Link to="/login">Inicia sesión aquí</Link></>
          )}
        </p>
      </div>
    </div>
  );
}

export default Register;