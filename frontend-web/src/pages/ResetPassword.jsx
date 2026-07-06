import { useMemo, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, Clock3, KeyRound, Lock, Mail, ShieldCheck, Sparkles } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { resetPassword } from '../services/authService';

function ResetPassword() {
  const navigate = useNavigate();
  const location = useLocation();
  const initialEmail = useMemo(() => location.state?.email || '', [location.state?.email]);
  const hasEmailFromState = Boolean(initialEmail);

  const [formData, setFormData] = useState({
    email: initialEmail,
    token: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState('');
  const [serverError, setServerError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const inputClass = (hasError) =>
    `h-12 w-full rounded-[18px] border bg-white pr-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary/70 focus:border-nt-blue focus:ring-4 focus:ring-nt-blue/15 ${
      hasError ? 'border-red-300 focus:ring-red-100' : 'border-nt-border'
    }`;

  const handleChange = (event) => {
    const { name, value } = event.target;
    const nextValue = name === 'token' ? value.replace(/\D/g, '').slice(0, 6) : value;

    setFormData((current) => ({
      ...current,
      [name]: nextValue
    }));
    setErrors((current) => ({
      ...current,
      [name]: ''
    }));
    setServerError('');
  };

  const validate = () => {
    const nextErrors = {};

    if (!formData.email.trim()) {
      nextErrors.email = 'El correo es obligatorio';
    }

    if (!formData.token.trim()) {
      nextErrors.token = 'El código es obligatorio';
    } else if (formData.token.trim().length !== 6) {
      nextErrors.token = 'El código debe tener 6 dígitos';
    }

    if (!formData.newPassword) {
      nextErrors.newPassword = 'La nueva contraseña es obligatoria';
    }

    if (!formData.confirmPassword) {
      nextErrors.confirmPassword = 'Confirma tu nueva contraseña';
    } else if (formData.newPassword !== formData.confirmPassword) {
      nextErrors.confirmPassword = 'Las contraseñas no coinciden';
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage('');
    setServerError('');

    if (!validate()) return;

    setIsLoading(true);

    const payload = {
      email: formData.email.trim().toLowerCase(),
      token: formData.token.trim(),
      newPassword: formData.newPassword,
      confirmPassword: formData.confirmPassword
    };

    const result = await resetPassword(payload);
    setIsLoading(false);

    if (result.success) {
      setMessage('Contraseña restablecida con éxito. Redirigiendo al inicio de sesión...');
      setTimeout(() => {
        navigate('/login');
      }, 1800);
      return;
    }

    setServerError(result.message || 'No se pudo restablecer la contraseña');
  };

  return (
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_neo4.png')] bg-cover bg-center bg-no-repeat px-4 py-6 text-nt-text-primary sm:py-8">
      <div className="pointer-events-none absolute left-6 top-8 hidden h-36 w-36 rounded-full bg-nt-blue-light/30 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-8 hidden h-40 w-40 rounded-full bg-nt-purple-light/25 blur-3xl md:block" />
      <div className="pointer-events-none absolute inset-0 bg-white/5" />

      <section className="relative mx-auto flex min-h-[calc(100vh-3rem)] w-full max-w-5xl items-center justify-center">
        <Card className="w-full max-w-lg rounded-[36px] border border-white/85 bg-white/78 p-0 shadow-[0_28px_90px_rgba(30,58,138,0.22)] backdrop-blur-2xl">
          <CardHeader className="px-6 pb-3 pt-5 text-center sm:px-8 sm:pt-6">
            <img
              src="/assets/neo_reset.png"
              alt="NEO"
              className="mx-auto mb-1 block w-28 object-contain drop-shadow-[0_20px_26px_rgba(30,58,138,0.22)] sm:w-36 lg:w-40"
            />
            <Badge className="mx-auto mb-2 h-7 rounded-full bg-white/70 px-4 text-sm font-black uppercase tracking-wide shadow-sm ring-1 ring-nt-blue/10 hover:bg-white/70">
              <span className="bg-gradient-to-r from-blue-600 to-violet-600 bg-clip-text text-transparent">
                NEUROTUTOR
              </span>
            </Badge>
            <CardTitle className="text-3xl font-black tracking-normal text-nt-text-primary">
              Restablecer contraseña
            </CardTitle>
            <CardDescription className="mt-1 text-sm font-semibold leading-6 text-nt-text-secondary">
              Usa el código recibido y crea una contraseña nueva para tu cuenta.
            </CardDescription>
          </CardHeader>

          <CardContent className="px-6 pb-7 sm:px-8">
            {hasEmailFromState && (
              <p className="mb-3 flex items-center justify-center gap-2 text-xs font-black text-nt-text-secondary">
                <Mail className="size-4 text-nt-blue" aria-hidden="true" />
                <span>Código enviado a:</span>
                <span className="max-w-[230px] truncate text-nt-text-primary">{formData.email}</span>
              </p>
            )}

            {message && (
              <div className="mb-4 rounded-[18px] border border-green-200 bg-green-50 px-4 py-3 text-center text-sm font-bold text-green-700">
                {message}
              </div>
            )}

            {serverError && (
              <div className="mb-4 rounded-[18px] border border-red-200 bg-red-50 px-4 py-3 text-center text-sm font-bold text-red-600">
                {serverError}
              </div>
            )}

            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="space-y-3">
                <div className="flex items-center gap-2">
                  <KeyRound className="size-4 shrink-0 text-nt-blue" aria-hidden="true" />
                  <p className="text-xs font-bold text-nt-text-secondary">Escribe los 6 dígitos que recibiste por correo.</p>
                </div>

                <div className="space-y-2">
                  <label htmlFor="reset-token" className="text-sm font-black text-nt-text-primary">
                    Código de verificación
                  </label>
                  <div className="relative">
                    <KeyRound className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary" />
                    <input
                      id="reset-token"
                      type="text"
                      inputMode="numeric"
                      name="token"
                      placeholder="123456"
                      value={formData.token}
                      onChange={handleChange}
                      className={`${inputClass(errors.token)} pl-11 tracking-[0.34em]`}
                    />
                  </div>
                  {errors.token && <p className="text-xs font-bold text-red-500">{errors.token}</p>}
                </div>

                <div className="inline-flex max-w-full items-center gap-2 rounded-full border border-amber-200 bg-amber-50 px-3 py-1.5 text-xs font-black text-amber-700">
                  <Clock3 className="size-3.5 shrink-0" aria-hidden="true" />
                  <span>Código válido por 60 minutos. Si expira, solicita uno nuevo.</span>
                </div>

                {!hasEmailFromState && (
                  <div className="space-y-2 rounded-[18px] border border-nt-border/80 bg-white/55 p-3">
                    <label htmlFor="reset-email" className="text-xs font-black uppercase tracking-wide text-nt-text-secondary">
                      Correo electrónico
                    </label>
                    <div className="relative">
                      <Mail className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary" />
                      <input
                        id="reset-email"
                        type="email"
                        name="email"
                        placeholder="tu@email.com"
                        value={formData.email}
                        onChange={handleChange}
                        className={`${inputClass(errors.email)} pl-11`}
                      />
                    </div>
                    {errors.email && <p className="text-xs font-bold text-red-500">{errors.email}</p>}
                  </div>
                )}
              </div>

              <div className="border-t border-nt-border/80 pt-4">
                <div className="mb-3 flex items-center gap-2">
                  <Lock className="size-4 shrink-0 text-nt-purple" aria-hidden="true" />
                  <p className="text-xs font-bold text-nt-text-secondary">Confirma tu nueva clave antes de continuar.</p>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <label htmlFor="reset-new-password" className="text-sm font-black text-nt-text-primary">
                      Nueva contraseña
                    </label>
                    <div className="relative">
                      <Lock className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary" />
                      <input
                        id="reset-new-password"
                        type="password"
                        name="newPassword"
                        placeholder="Nueva contraseña"
                        value={formData.newPassword}
                        onChange={handleChange}
                        className={`${inputClass(errors.newPassword)} pl-11`}
                      />
                    </div>
                    {errors.newPassword && <p className="text-xs font-bold text-red-500">{errors.newPassword}</p>}
                  </div>

                  <div className="space-y-2">
                    <label htmlFor="reset-confirm-password" className="text-sm font-black text-nt-text-primary">
                      Confirmar contraseña
                    </label>
                    <div className="relative">
                      <ShieldCheck className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary" />
                      <input
                        id="reset-confirm-password"
                        type="password"
                        name="confirmPassword"
                        placeholder="Repite la contraseña"
                        value={formData.confirmPassword}
                        onChange={handleChange}
                        className={`${inputClass(errors.confirmPassword)} pl-11`}
                      />
                    </div>
                    {errors.confirmPassword && <p className="text-xs font-bold text-red-500">{errors.confirmPassword}</p>}
                  </div>
                </div>
              </div>

              <Button
                type="submit"
                disabled={isLoading}
                className="h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
              >
                <Sparkles className="size-4" aria-hidden="true" />
                {isLoading ? 'Cambiando...' : 'Cambiar contraseña'}
              </Button>
            </form>

            <div className="mt-5 text-center">
              <Link
                to="/login"
                className="inline-flex items-center gap-2 text-sm font-black text-nt-blue hover:text-nt-purple"
              >
                <ArrowLeft className="size-4" aria-hidden="true" />
                Volver al inicio de sesión
              </Link>
            </div>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

export default ResetPassword;
