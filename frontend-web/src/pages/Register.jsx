import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { register, login } from '../services/authService';
import { saveAuthData } from '../utils/auth';

function Register({ initialTab = 'register' }) {
  const navigate = useNavigate();
  const activeTab = initialTab;
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

  const inputClass = (hasError) =>
    `h-12 w-full rounded-[18px] border bg-white px-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary/70 focus:ring-4 ${
      hasError
        ? 'border-red-300 focus:border-red-400 focus:ring-red-100'
        : 'border-nt-border focus:border-nt-blue focus:ring-nt-blue/15'
    }`;

  const selectClass = (hasError) =>
    `h-12 w-full rounded-[18px] border bg-white px-4 text-sm font-semibold text-nt-text-primary outline-none transition focus:ring-4 ${
      hasError
        ? 'border-red-300 focus:border-red-400 focus:ring-red-100'
        : 'border-nt-border focus:border-nt-blue focus:ring-nt-blue/15'
    }`;

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
        saveAuthData(result.data);

        if (result.data.examenCompletado === true) {
          navigate('/student-dashboard');
        } else {
          navigate('/diagnostic-exam');
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

    console.log('Enviando registro a Spring Boot:', dataToSend);

    const result = await register(dataToSend);

    if (result.success) {
      console.log('Registro exitoso:', result.data);
      saveAuthData(result.data);
      setSuccessMessage('Registro exitoso. Redirigiendo al examen diagnóstico...');
      navigate('/diagnostic-exam');
    } else {
      console.error('Error en registro:', result.message);
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
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_neo3.png')] bg-cover bg-center px-4 py-8 text-nt-text-primary">
      <div className="absolute inset-0 bg-white/10" aria-hidden="true" />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(255,255,255,0.24)_0,rgba(37,99,235,0.08)_42%,rgba(30,58,138,0.14)_100%)]" />
      <div className="pointer-events-none absolute right-8 top-8 hidden h-28 w-28 rounded-full bg-nt-green/25 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 left-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
        <Card className="w-full max-w-xl rounded-[36px] border border-white/80 bg-white/74 p-0 shadow-[0_28px_90px_rgba(30,58,138,0.24)] backdrop-blur-2xl">
          <CardHeader className="px-6 pb-4 pt-6 text-center sm:px-8">
            <img
              src="/assets/neo_register.png"
              alt="NEO"
              className="mx-auto mb-2 block w-32 object-contain drop-shadow-[0_22px_28px_rgba(30,58,138,0.22)] sm:w-40 lg:w-44"
            />
            <Badge className="mx-auto mb-3 h-7 rounded-full bg-white/70 px-4 text-sm font-black uppercase tracking-wide shadow-sm ring-1 ring-nt-blue/10 hover:bg-white/70">
              <span className="bg-gradient-to-r from-blue-600 to-violet-600 bg-clip-text text-transparent">
                NEUROTUTOR
              </span>
            </Badge>
            <CardTitle className="text-3xl font-black tracking-normal text-nt-text-primary">
              {activeTab === 'login' ? 'Iniciar sesión' : 'Crear cuenta'}
            </CardTitle>
            <CardDescription className="mt-2 text-sm font-semibold text-nt-text-secondary">
              {activeTab === 'login'
                ? 'Ingresa con tu correo y contraseña.'
                : 'Regístrate para comenzar tu aprendizaje personalizado.'}
            </CardDescription>
          </CardHeader>

          <CardContent className="px-6 pb-7 sm:px-8">
            <div className="hidden">
              <button
                type="button"
                className={`h-11 rounded-[18px] text-sm font-black transition ${
                  activeTab === 'login'
                    ? 'bg-white text-nt-blue shadow-[0_10px_24px_rgba(37,99,235,0.16)]'
                    : 'text-nt-text-secondary hover:bg-white/70 hover:text-nt-blue'
                }`}
                onClick={() => navigate('/login')}
              >
                Iniciar sesión
              </button>
              <button
                type="button"
                className={`h-11 rounded-[18px] text-sm font-black transition ${
                  activeTab === 'register'
                    ? 'bg-white text-nt-purple shadow-[0_10px_24px_rgba(124,58,237,0.16)]'
                    : 'text-nt-text-secondary hover:bg-white/70 hover:text-nt-purple'
                }`}
                onClick={() => navigate('/register')}
              >
                Registrarse
              </button>
            </div>

            {successMessage && (
              <div className="mb-4 rounded-[18px] border border-green-200 bg-green-50 px-4 py-3 text-center text-sm font-bold text-green-700">
                {successMessage}
              </div>
            )}

            {serverError && (
              <div className="mb-4 flex min-h-12 items-center justify-center rounded-[18px] border border-red-200 bg-red-50 px-4 py-3 text-center text-sm font-bold leading-5 text-red-600" role="alert">
                <span className="max-w-full break-words">{serverError}</span>
              </div>
            )}

            {activeTab === 'login' ? (
              <form className="space-y-4" onSubmit={handleSubmit}>
                <div className="space-y-2">
                  <label htmlFor="register-login-email" className="text-sm font-black text-nt-text-primary">
                    Correo electrónico
                  </label>
                  <input
                    id="register-login-email"
                    type="email"
                    name="email"
                    placeholder="Código de estudiante o email"
                    value={formData.email}
                    onChange={handleChange}
                    className={inputClass(errors.email)}
                  />
                  {errors.email && <span className="text-xs font-bold text-red-500">{errors.email}</span>}
                </div>

                <div className="space-y-2">
                  <label htmlFor="register-login-password" className="text-sm font-black text-nt-text-primary">
                    Contraseña
                  </label>
                  <input
                    id="register-login-password"
                    type="password"
                    name="password"
                    placeholder="Contraseña"
                    value={formData.password}
                    onChange={handleChange}
                    className={inputClass(errors.password)}
                  />
                  {errors.password && <span className="text-xs font-bold text-red-500">{errors.password}</span>}
                </div>

                <div className="flex justify-end">
                  <Link to="/forgot-password" className="text-sm font-black text-nt-blue hover:text-nt-purple">
                    ¿Olvidaste tu contraseña?
                  </Link>
                </div>

                <Button
                  type="submit"
                  disabled={!isFormValid() || isLoading}
                  className="h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                >
                  {isLoading ? 'Ingresando...' : 'Ingresar'}
                </Button>
              </form>
            ) : (
              <form className="space-y-4" onSubmit={handleSubmit}>
                <div className="space-y-2">
                  <label htmlFor="register-name" className="text-sm font-black text-nt-text-primary">
                    Nombre completo
                  </label>
                  <input
                    id="register-name"
                    type="text"
                    name="nombre"
                    placeholder="Ej: María González"
                    value={formData.nombre}
                    onChange={handleChange}
                    className={inputClass(errors.nombre)}
                  />
                  {errors.nombre && <span className="text-xs font-bold text-red-500">{errors.nombre}</span>}
                </div>

                <div className="space-y-2">
                  <label htmlFor="register-email" className="text-sm font-black text-nt-text-primary">
                    Correo electrónico
                  </label>
                  <input
                    id="register-email"
                    type="email"
                    name="email"
                    placeholder="usuario@ejemplo.com"
                    value={formData.email}
                    onChange={handleChange}
                    className={inputClass(errors.email)}
                  />
                  {errors.email && <span className="text-xs font-bold text-red-500">{errors.email}</span>}
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <label htmlFor="register-grade" className="text-sm font-black text-nt-text-primary">
                      Grado
                    </label>
                    <select
                      id="register-grade"
                      name="grado"
                      value={formData.grado}
                      onChange={handleChange}
                      className={selectClass(errors.grado)}
                    >
                      <option value="">Selecciona</option>
                      <option value="4to">4to grado</option>
                      <option value="5to">5to grado</option>
                      <option value="6to">6to grado</option>
                      <option value="7mo">7mo grado</option>
                    </select>
                    {errors.grado && <span className="text-xs font-bold text-red-500">{errors.grado}</span>}
                  </div>

                  <div className="space-y-2">
                    <label htmlFor="register-section" className="text-sm font-black text-nt-text-primary">
                      Sección
                    </label>
                    <select
                      id="register-section"
                      name="seccion"
                      value={formData.seccion}
                      onChange={handleChange}
                      className={selectClass(errors.seccion)}
                    >
                      <option value="">Selecciona</option>
                      <option value="A">A</option>
                      <option value="B">B</option>
                      <option value="C">C</option>
                      <option value="D">D</option>
                    </select>
                    {errors.seccion && <span className="text-xs font-bold text-red-500">{errors.seccion}</span>}
                  </div>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <label htmlFor="register-password" className="text-sm font-black text-nt-text-primary">
                      Contraseña
                    </label>
                    <input
                      id="register-password"
                      type="password"
                      name="password"
                      placeholder="Mínimo 8 caracteres"
                      value={formData.password}
                      onChange={handleChange}
                      className={inputClass(errors.password)}
                    />
                    {errors.password && <span className="text-xs font-bold text-red-500">{errors.password}</span>}
                  </div>

                  <div className="space-y-2">
                    <label htmlFor="register-confirm-password" className="text-sm font-black text-nt-text-primary">
                      Confirmar contraseña
                    </label>
                    <input
                      id="register-confirm-password"
                      type="password"
                      name="confirmPassword"
                      placeholder="Repite tu contraseña"
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      className={inputClass(errors.confirmPassword)}
                    />
                    {errors.confirmPassword && <span className="text-xs font-bold text-red-500">{errors.confirmPassword}</span>}
                  </div>
                </div>

                <Button
                  type="submit"
                  disabled={!isFormValid() || isLoading}
                  className="h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                >
                  {isLoading ? 'Registrando...' : 'Registrarse'}
                </Button>
              </form>
            )}

            <p className="mt-5 text-center text-sm font-semibold text-nt-text-secondary">
              {activeTab === 'login' ? (
                <>
                  ¿No tienes cuenta?{' '}
                  <Link to="/register" className="font-black text-nt-purple hover:text-nt-blue">
                    Crea una nueva
                  </Link>
                </>
              ) : (
                <>
                  ¿Ya tienes cuenta?{' '}
                  <Link to="/login" className="font-black text-nt-purple hover:text-nt-blue">
                    Inicia sesión aquí
                  </Link>
                </>
              )}
            </p>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

export default Register;
