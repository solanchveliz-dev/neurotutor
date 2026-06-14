import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import styles from '../styles/Login.module.css';
import { login } from '../services/authService';
import { saveAuthData } from '../utils/auth';

function Login() {
  const navigate = useNavigate();
  const [rol, setRol] = useState('ESTUDIANTE');
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // Colores por rol
  const colors = {
    ESTUDIANTE: { border: '#22C55E', button: '#22C55E' },
    DOCENTE: { border: '#F59E0B', button: '#F59E0B' },
    ADMIN: { border: '#7C3AED', button: '#7C3AED' }
  };

  const handleRolChange = (selectedRol) => {
    setRol(selectedRol);
    setError('');
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const getPlaceholder = () => {
    switch (rol) {
      case 'ESTUDIANTE': return 'Código de estudiante o email';
      case 'DOCENTE': return 'Email del docente';
      case 'ADMIN': return 'Usuario administrador';
      default: return 'Correo electrónico';
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    // Enviar solo email y password (el rol lo maneja el backend)
    const result = await login({
      email: formData.email,
      password: formData.password
    });

    if (result.success) {
      saveAuthData(result.data);

      if (result.data.examenCompletado === true) {
        navigate('/student-dashboard');
      } else {
        navigate('/diagnostic-exam');
      }
    } else {
      setError(result.message || 'Usuario o contraseña incorrectos');
    }

    setIsLoading(false);
  };

  return (
    <div className={styles.container}>
      <div className={styles.card} style={{ borderColor: colors[rol].border }}>
        <h1 className={styles.title}>NeuroTutor</h1>
        
        {/* Selector de roles */}
        <div className={styles.rolesContainer}>
          <button
            className={`${styles.rolButton} ${rol === 'ESTUDIANTE' ? styles.activeEstudiante : ''}`}
            onClick={() => handleRolChange('ESTUDIANTE')}
          >
            👨‍🎓 Estudiante
          </button>
          <button
            className={`${styles.rolButton} ${rol === 'DOCENTE' ? styles.activeDocente : ''}`}
            onClick={() => handleRolChange('DOCENTE')}
          >
            👩‍🏫 Docente
          </button>
          <button
            className={`${styles.rolButton} ${rol === 'ADMIN' ? styles.activeAdmin : ''}`}
            onClick={() => handleRolChange('ADMIN')}
          >
            👑 Administrador
          </button>
        </div>

        {error && (
          <div className={styles.errorMessage}>{error}</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className={styles.field}>
            <input
              type="text"
              name="email"
              placeholder={getPlaceholder()}
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className={styles.field}>
            <input
              type="password"
              name="password"
              placeholder="Contraseña"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          {/* Enlace de recuperación (HU-03 - ya avanzada por Naomi) */}
          <div className={styles.forgotLink}>
            <Link to="/forgot-password">¿Olvidaste tu contraseña?</Link>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className={styles.button}
            style={{ backgroundColor: colors[rol].button }}
          >
            {isLoading ? 'Ingresando...' : 'INGRESAR'}
          </button>
        </form>

        {/* Solo Estudiantes ven el enlace para registrarse */}
        {rol === 'ESTUDIANTE' && (
          <p className={styles.registerLink}>
            ¿No tienes cuenta? <Link to="/register">Crea una nueva</Link>
          </p>
        )}
      </div>
    </div>
  );
}

export default Login;
