import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { GraduationCap, ShieldCheck, Sparkles, UserCog } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
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

  const roleOptions = [
    { id: 'ESTUDIANTE', label: 'Estudiante', icon: GraduationCap },
    { id: 'DOCENTE', label: 'Docente', icon: ShieldCheck },
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
    <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
        <Card className="w-full max-w-md rounded-[32px] border border-white/80 bg-white/88 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
          <CardHeader className="px-6 pb-4 pt-7 text-center sm:px-8">
            <div className="mx-auto mb-4 grid size-16 place-items-center rounded-[24px] bg-gradient-to-br from-nt-blue to-nt-purple text-white shadow-[0_16px_34px_rgba(37,99,235,0.28)]">
              <Sparkles className="size-8" aria-hidden="true" />
            </div>
            <Badge className="mx-auto mb-3 h-6 rounded-full bg-nt-blue/10 px-3 text-[11px] font-black uppercase tracking-wide text-nt-blue hover:bg-nt-blue/10">
              NeuroTutor
            </Badge>
            <CardTitle className="text-3xl font-black tracking-normal text-nt-text-primary">
              Iniciar sesión
            </CardTitle>
            <CardDescription className="mt-2 text-sm font-semibold text-nt-text-secondary">
              Entra a tu aventura matemática y continúa aprendiendo.
            </CardDescription>
          </CardHeader>

          <CardContent className="px-6 pb-7 sm:px-8">
            <div className="mb-5 grid grid-cols-3 gap-2 rounded-[22px] bg-nt-sky/70 p-1.5">
              {roleOptions.map(({ id, label, icon: Icon }) => {
                const isActive = rol === id;

                return (
                  <button
                    key={id}
                    type="button"
                    className={`flex min-h-16 flex-col items-center justify-center gap-1 rounded-[18px] px-2 text-[11px] font-black transition ${
                      isActive
                        ? 'bg-white text-nt-blue shadow-[0_10px_24px_rgba(37,99,235,0.16)]'
                        : 'text-nt-text-secondary hover:bg-white/70 hover:text-nt-blue'
                    }`}
                    onClick={() => handleRolChange(id)}
                    aria-pressed={isActive}
                  >
                    <Icon className="size-4" aria-hidden="true" />
                    <span className="leading-tight">{label}</span>
                  </button>
                );
              })}
            </div>

            {error && (
              <div className="mb-4 rounded-[18px] border border-red-200 bg-red-50 px-4 py-3 text-center text-sm font-bold text-red-600">
                {error}
              </div>
            )}

            <form className="space-y-4" onSubmit={handleSubmit}>
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
                  className="h-12 w-full rounded-[18px] border border-nt-border bg-white px-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary/70 focus:border-nt-blue focus:ring-4 focus:ring-nt-blue/15"
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
                  className="h-12 w-full rounded-[18px] border border-nt-border bg-white px-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary/70 focus:border-nt-blue focus:ring-4 focus:ring-nt-blue/15"
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
                className="h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                style={{ '--role-color': colors[rol].button }}
              >
                {isLoading ? 'Ingresando...' : 'Ingresar'}
              </Button>
            </form>

            {rol === 'ESTUDIANTE' && (
              <p className="mt-5 text-center text-sm font-semibold text-nt-text-secondary">
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
