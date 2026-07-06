import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ArrowLeft, Mail, Sparkles } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
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

    const normalizedEmail = email.trim().toLowerCase();

    try {
      const result = await forgotPassword(normalizedEmail);
      if (!result.success) throw new Error(result.message);

      setMessage(result.data?.message || 'Te enviamos un código a tu correo');
      setEmail('');

      setTimeout(() => {
        navigate('/reset-password', {
          state: { email: normalizedEmail, token: result.data?.debugToken || result.data?.devCode || '' }
        });
      }, 900);
    } catch (requestError) {
      setError(requestError.message || 'No se pudo solicitar la recuperación. Intenta nuevamente.');
    }

    setIsLoading(false);
  };

  return (
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_neo4.png')] bg-cover bg-center bg-no-repeat px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute left-6 top-8 hidden h-36 w-36 rounded-full bg-nt-blue-light/30 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-8 hidden h-40 w-40 rounded-full bg-nt-purple-light/25 blur-3xl md:block" />
      <div className="pointer-events-none absolute inset-0 bg-white/5" />

      <section className="relative mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
        <Card className="w-full max-w-md rounded-[36px] border border-white/85 bg-white/78 p-0 shadow-[0_28px_90px_rgba(30,58,138,0.22)] backdrop-blur-2xl">
          <CardHeader className="px-6 pb-4 pt-6 text-center sm:px-8">
            <img
              src="/assets/neo_forgot.png"
              alt="NEO"
              className="mx-auto mb-2 block w-32 object-contain drop-shadow-[0_22px_28px_rgba(30,58,138,0.22)] sm:w-40 lg:w-44"
            />
            <Badge className="mx-auto mb-3 h-7 rounded-full bg-white/70 px-4 text-sm font-black uppercase tracking-wide shadow-sm ring-1 ring-nt-blue/10 hover:bg-white/70">
              <span className="bg-gradient-to-r from-blue-600 to-violet-600 bg-clip-text text-transparent">
                NEUROTUTOR
              </span>
            </Badge>
            <CardTitle className="text-3xl font-black tracking-normal text-nt-text-primary">
              ¿Olvidaste tu contraseña?
            </CardTitle>
            <CardDescription className="mt-2 text-sm font-semibold leading-6 text-nt-text-secondary">
              Ingresa tu correo electrónico y te enviaremos un código para restablecer tu contraseña.
            </CardDescription>
          </CardHeader>

          <CardContent className="px-6 pb-7 sm:px-8">
            {message && (
              <div className="mb-4 rounded-[18px] border border-green-200 bg-green-50 px-4 py-3 text-center text-sm font-bold text-green-700">
                {message}
              </div>
            )}

            {error && (
              <div className="mb-4 rounded-[18px] border border-red-200 bg-red-50 px-4 py-3 text-center text-sm font-bold text-red-600">
                {error}
              </div>
            )}

            <form className="space-y-4" onSubmit={handleSubmit}>
              <div className="space-y-2">
                <label htmlFor="forgot-email" className="text-sm font-black text-nt-text-primary">
                  Correo electrónico
                </label>
                <div className="relative">
                  <Mail
                    className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
                    aria-hidden="true"
                  />
                  <input
                    id="forgot-email"
                    type="email"
                    placeholder="tu@email.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    className="h-12 w-full rounded-[18px] border border-nt-border bg-white pl-11 pr-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary/70 focus:border-nt-blue focus:ring-4 focus:ring-nt-blue/15"
                  />
                </div>
              </div>

              <Button
                type="submit"
                disabled={isLoading || !email}
                className="h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
              >
                <Sparkles className="size-4" aria-hidden="true" />
                {isLoading ? 'Enviando...' : 'Enviar código de recuperación'}
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

export default ForgotPassword;
