import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { GraduationCap, UserCog } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { login } from '../services/authService';
import { saveAuthData } from '../utils/auth';

const fixVisibleEncoding = (text = '') =>
  text
    .replaceAll('CÃ³digo', 'Código')
    .replaceAll('electrÃ³nico', 'electrónico')
    .replaceAll('contraseÃ±a', 'contraseña')
    .replaceAll('ContraseÃ±a', 'Contraseña')
    .replaceAll('sesiÃ³n', 'sesión')
    .replaceAll('Â¿', '¿');

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
    ADMIN: { border: '#7C3AED', button: '#7C3AED' }
  };

  const roleOptions = [
    { id: 'ESTUDIANTE', label: 'Estudiante', icon: GraduationCap },
    { id: 'ADMIN', label: 'Administrador', icon: UserCog }
  ];

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
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_neo3.png')] bg-cover bg-center px-4 py-8 text-nt-text-primary">
      <div className="absolute inset-0 bg-white/10" aria-hidden="true" />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(255,255,255,0.3)_0,rgba(37,99,235,0.08)_42%,rgba(30,58,138,0.16)_100%)]" />
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
        <Card className="w-full max-w-lg rounded-[36px] border border-white/80 bg-white/72 p-0 shadow-[0_28px_90px_rgba(30,58,138,0.24)] backdrop-blur-2xl">
          <CardHeader className="px-7 pb-4 pt-6 text-center sm:px-10">
            <img
              src="/assets/neo_login.png"
              alt="NEO"
              className="mx-auto mb-2 block w-32 object-contain drop-shadow-[0_22px_28px_rgba(30,58,138,0.22)] sm:w-40 lg:w-44"
            />
            <Badge className="mx-auto mb-3 h-7 rounded-full bg-white/70 px-4 text-sm font-black uppercase tracking-wide shadow-sm ring-1 ring-nt-blue/10 hover:bg-white/70">
              <span className="bg-gradient-to-r from-blue-600 to-violet-600 bg-clip-text text-transparent">
                NEUROTUTOR
              </span>
            </Badge>
            <CardTitle className="text-3xl font-black tracking-normal text-nt-text-primary sm:text-3xl">
              Iniciar sesión
            </CardTitle>
            <CardDescription className="mx-auto mt-2 max-w-sm text-sm font-semibold leading-6 text-nt-text-secondary">
              Entra a tu aventura matemática y continúa aprendiendo.
            </CardDescription>
          </CardHeader>

          <CardContent className="px-7 pb-8 sm:px-10">
            <div className="mb-6 grid grid-cols-2 gap-2 rounded-[26px] border border-white/75 bg-white/45 p-1.5 shadow-inner shadow-white/60 backdrop-blur">
              {roleOptions.map(({ id, label, icon: Icon }) => {
                const isActive = rol === id;

                return (
                  <button
                    key={id}
                    type="button"
                    className={`flex min-h-[74px] flex-col items-center justify-center gap-1.5 rounded-[22px] px-4 text-sm font-black transition ${
                      isActive
                        ? 'bg-white text-nt-blue shadow-[0_14px_30px_rgba(37,99,235,0.18)] ring-1 ring-nt-blue/10'
                        : 'text-nt-text-secondary hover:bg-white/75 hover:text-nt-blue'
                    }`}
                    onClick={() => handleRolChange(id)}
                    aria-pressed={isActive}
                  >
                    <Icon className="size-5" aria-hidden="true" />
                    <span className="leading-tight">{label}</span>
                  </button>
                );
              })}
            </div>

            {error && (
              <div className="mb-5 rounded-[20px] border border-red-200 bg-red-50/90 px-4 py-3 text-center text-sm font-bold text-red-600 shadow-sm">
                {fixVisibleEncoding(error)}
              </div>
            )}

            <form className="space-y-3" onSubmit={handleSubmit}>
              <div className="space-y-2">
                <label htmlFor="login-email" className="text-sm font-black text-nt-text-primary">
                  Usuario o correo
                </label>
                <input
                  id="login-email"
                  type="text"
                  name="email"
                  placeholder={getPlaceholder()}
                  value={formData.email}
                  onChange={handleChange}
                  required
                  className="h-13 w-full rounded-[20px] border border-white/80 bg-white/82 px-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary/65 focus:border-nt-blue focus:bg-white focus:ring-4 focus:ring-nt-blue/15"
                />
              </div>

              <div className="space-y-2">
                <label htmlFor="login-password" className="text-sm font-black text-nt-text-primary">
                  Contraseña
                </label>
                <input
                  id="login-password"
                  type="password"
                  name="password"
                  placeholder="Contraseña"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  className="h-13 w-full rounded-[20px] border border-white/80 bg-white/82 px-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary/65 focus:border-nt-blue focus:bg-white focus:ring-4 focus:ring-nt-blue/15"
                />
              </div>

              <div className="flex justify-end">
                <Link to="/forgot-password" className="text-sm font-black text-nt-blue hover:text-nt-purple">
                  ¿Olvidaste tu contraseña?
                </Link>
              </div>

              <Button
                type="submit"
                disabled={isLoading}
                className="h-13 w-full rounded-[20px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_18px_34px_rgba(37,99,235,0.28)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                style={{ '--role-color': colors[rol].button }}
              >
                {isLoading ? 'Ingresando...' : 'Ingresar'}
              </Button>
            </form>

            {rol === 'ESTUDIANTE' && (
              <p className="mt-6 text-center text-sm font-semibold text-nt-text-secondary">
                ¿No tienes cuenta?{' '}
                <Link to="/register" className="font-black text-nt-purple hover:text-nt-blue">
                  Crea una nueva
                </Link>
              </p>
            )}
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

export default Login;
