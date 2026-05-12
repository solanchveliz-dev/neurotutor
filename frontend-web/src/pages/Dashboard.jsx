import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (!token) {
      navigate('/login');
      return;
    }
    
    if (userData) {
      setUser(JSON.parse(userData));
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  if (!user) return <div>Cargando...</div>;

  return (
    <div style={{ padding: '40px', textAlign: 'center' }}>
      <h1>Panel NeuroTutor</h1>
      <h2>Bienvenido, {user.nombre || user.email}</h2>
      <p>Rol: {user.rol || 'ESTUDIANTE'}</p>
      
      <br /><br />
      <button 
        onClick={handleLogout} 
        style={{ 
          background: '#e74c3c', 
          color: 'white', 
          padding: '10px 20px', 
          border: 'none', 
          borderRadius: '8px', 
          cursor: 'pointer',
          fontSize: '16px'
        }}
      >
        🚪 Cerrar sesión
      </button>
    </div>
  );
}

export default Dashboard;