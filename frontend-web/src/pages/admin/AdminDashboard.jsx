import { useEffect, useState } from "react";
import { Link, NavLink } from "react-router-dom";
import {
  Activity,
  AlertCircle,
  BarChart3,
  BookOpen,
  LayoutDashboard,
  RefreshCw,
  Users,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { getAdminSummary } from "@/services/adminService";

const adminNavItems = [
  { label: "Dashboard", href: "/admin/dashboard", icon: LayoutDashboard },
  { label: "Estudiantes", href: "/admin/students", icon: Users },
];

function AdminLayout({ title, description, children }) {
  return (
    <main className="min-h-screen bg-slate-50 text-slate-950">
      <div className="grid min-h-screen lg:grid-cols-[280px_1fr]">
        <aside className="border-b border-slate-200 bg-white px-5 py-5 lg:border-b-0 lg:border-r">
          <div className="flex items-center gap-3">
            <div className="flex size-11 items-center justify-center rounded-2xl bg-gradient-to-br from-blue-600 to-violet-600 text-white shadow-lg shadow-blue-500/20">
              <BarChart3 className="size-5" aria-hidden="true" />
            </div>
            <div>
              <p className="text-sm font-black uppercase tracking-wide text-slate-400">NeuroTutor</p>
              <h1 className="text-lg font-black text-slate-900">Administrador</h1>
            </div>
          </div>

          <nav className="mt-7 flex gap-2 overflow-x-auto lg:flex-col lg:overflow-visible">
            {adminNavItems.map(({ label, href, icon: Icon }) => (
              <NavLink
                key={href}
                to={href}
                className={({ isActive }) =>
                  `flex min-h-11 shrink-0 items-center gap-3 rounded-2xl px-4 text-sm font-bold transition ${
                    isActive
                      ? "bg-blue-50 text-blue-700 ring-1 ring-blue-100"
                      : "text-slate-500 hover:bg-slate-50 hover:text-slate-900"
                  }`
                }
              >
                <Icon className="size-4" aria-hidden="true" />
                {label}
              </NavLink>
            ))}
          </nav>
        </aside>

        <section className="min-w-0 px-4 py-6 sm:px-6 lg:px-8">
          <header className="mb-6 flex flex-col justify-between gap-4 border-b border-slate-200 pb-5 md:flex-row md:items-end">
            <div>
              <Badge className="mb-3 bg-white text-blue-700 ring-1 ring-blue-100 hover:bg-white">
                Panel administrativo
              </Badge>
              <h2 className="text-2xl font-black tracking-normal text-slate-950 sm:text-3xl">{title}</h2>
              <p className="mt-2 max-w-2xl text-sm font-semibold leading-6 text-slate-500">{description}</p>
            </div>
            <Button asChild variant="outline" className="h-10 rounded-2xl border-slate-200 bg-white font-bold">
              <Link to="/admin/students">
                <Users className="size-4" aria-hidden="true" />
                Ver estudiantes
              </Link>
            </Button>
          </header>

          {children}
        </section>
      </div>
    </main>
  );
}

function ErrorState({ onRetry }) {
  return (
    <Card className="rounded-3xl border border-red-100 bg-white shadow-sm">
      <CardContent className="flex flex-col items-start gap-4 p-6 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex gap-3">
          <div className="flex size-11 shrink-0 items-center justify-center rounded-2xl bg-red-50 text-red-600">
            <AlertCircle className="size-5" aria-hidden="true" />
          </div>
          <div>
            <h3 className="font-black text-slate-950">No se pudo conectar con Django</h3>
            <p className="mt-1 text-sm font-semibold text-slate-500">
              Verifica que el backend este activo en http://127.0.0.1:8000.
            </p>
          </div>
        </div>
        <Button onClick={onRetry} className="h-10 rounded-2xl bg-blue-600 font-bold text-white hover:bg-blue-700">
          <RefreshCw className="size-4" aria-hidden="true" />
          Reintentar
        </Button>
      </CardContent>
    </Card>
  );
}

function LoadingCards() {
  return (
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      {[1, 2, 3, 4].map((item) => (
        <Card key={item} className="rounded-3xl border border-slate-200 bg-white shadow-sm">
          <CardContent className="p-5">
            <div className="h-4 w-24 animate-pulse rounded-full bg-slate-100" />
            <div className="mt-5 h-8 w-16 animate-pulse rounded-xl bg-slate-100" />
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

function StatCard({ label, value, icon: Icon, tone }) {
  const tones = {
    blue: "bg-blue-50 text-blue-700",
    green: "bg-emerald-50 text-emerald-700",
    amber: "bg-amber-50 text-amber-700",
    purple: "bg-violet-50 text-violet-700",
  };

  return (
    <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
      <CardContent className="flex items-center justify-between gap-4 p-5">
        <div>
          <p className="text-sm font-bold text-slate-500">{label}</p>
          <p className="mt-2 text-3xl font-black text-slate-950">{value}</p>
        </div>
        <div className={`flex size-12 items-center justify-center rounded-2xl ${tones[tone]}`}>
          <Icon className="size-5" aria-hidden="true" />
        </div>
      </CardContent>
    </Card>
  );
}

function AdminDashboard() {
  const [summary, setSummary] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);

  const loadSummary = async () => {
    setIsLoading(true);
    setHasError(false);

    try {
      const data = await getAdminSummary();
      setSummary(data);
    } catch {
      setHasError(true);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadSummary();
  }, []);

  return (
    <AdminLayout
      title="Resumen general"
      description="Vista inicial para supervisar estudiantes, actividad y modulos disponibles desde Django."
    >
      {isLoading && <LoadingCards />}
      {!isLoading && hasError && <ErrorState onRetry={loadSummary} />}
      {!isLoading && !hasError && summary && (
        <div className="space-y-5">
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <StatCard label="Estudiantes" value={summary.total_students} icon={Users} tone="blue" />
            <StatCard label="Activos" value={summary.active_students} icon={Activity} tone="green" />
            <StatCard label="Inactivos" value={summary.inactive_students} icon={AlertCircle} tone="amber" />
            <StatCard label="Modulos" value={summary.total_modules} icon={BookOpen} tone="purple" />
          </div>

          <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
            <CardHeader>
              <CardTitle className="text-lg font-black text-slate-950">Siguiente paso</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-4 p-5 pt-0 md:flex-row md:items-center md:justify-between">
              <p className="max-w-2xl text-sm font-semibold leading-6 text-slate-500">
                Revisa la lista de estudiantes para validar que React pueda leer informacion administrativa desde el backend Django.
              </p>
              <Button asChild className="h-10 rounded-2xl bg-blue-600 font-bold text-white hover:bg-blue-700">
                <Link to="/admin/students">
                  <Users className="size-4" aria-hidden="true" />
                  Abrir estudiantes
                </Link>
              </Button>
            </CardContent>
          </Card>
        </div>
      )}
    </AdminLayout>
  );
}

export { AdminLayout, ErrorState };
export default AdminDashboard;
