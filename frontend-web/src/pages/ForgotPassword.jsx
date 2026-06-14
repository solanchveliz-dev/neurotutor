import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from '../styles/ForgotPassword.module.css';
import { forgotPassword } from '../services/authService';

function ForgotPassword() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage('');
    setError('');

    try {
      await forgotPassword(email.trim().toLowerCase());
      
      // Mostrar mismo mensaje siempre (por seguridad)
      setMessage('Te enviamos un enlace a tu correo');
      setEmail('');
      
      // Opcional: redirigir al login después de 3 segundos
      setTimeout(() => {
        navigate('/login');
      }, 3000);
      
    } catch (err) {
      // Por seguridad, mostramos el mismo mensaje aunque haya error
      setMessage('Te enviamos un enlace a tu correo si está registrado');
      setEmail('');
      
      // Redirigir igualmente
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    }
    
    setIsLoading(false);
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>¿Olvidaste tu contraseña?</h1>
        <p className={styles.subtitle}>
          Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.
        </p>

        {message && (
          <div className={styles.successMessage}>{message}</div>
        )}

        {error && (
          <div className={styles.errorMessage}>{error}</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className={styles.field}>
            <label>Correo electrónico</label>
            <input
              type="email"
              placeholder="tu@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <button 
            type="submit" 
            disabled={isLoading || !email}
            className={styles.button}
          >
            {isLoading ? 'Enviando...' : 'Enviar enlace de recuperación'}
          </button>
        </form>

        <div className={styles.links}>
          <Link to="/login">← Volver al inicio de sesión</Link>
        </div>
      </div>
    </div>
  );
}

export default ForgotPassword;
