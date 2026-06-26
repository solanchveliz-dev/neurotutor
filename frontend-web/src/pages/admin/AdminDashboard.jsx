import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  ArrowRight,
  BookOpen,
  ChartNoAxesColumnIncreasing,
  RefreshCw,
  TrendingUp,
  UserCheck,
  Users,
} from "lucide-react";
import AdminLayout from "@/components/admin/AdminLayout";
import AdminStatusBadge from "@/components/admin/AdminStatusBadge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getAdminStudents, getAdminSummary } from "@/services/adminService";

const levelOrder = ["BASICO", "INTERMEDIO", "AVANZADO"];
const levelLabels = { BASICO: "Básico", INTERMEDIO: "Intermedio", AVANZADO: "Avanzado" };

function DataNotice() {
  return (
    <div className="flex items-center gap-3 rounded-2xl border border-[#7C3AED]/15 bg-white/85 px-4 py-3 text-sm text-[#52617C] shadow-[0_14px_34px_rgba(37,99,255,0.06)] backdrop-blur">
      <span className="size-2 shrink-0 rounded-full bg-[#7C3AED]" />
      El backend no está disponible. Se muestran datos temporales de demostración.
    </div>
  );
}

function ErrorState({ onRetry }) {
  return (
    <Card className="border-[#D8E5F8] bg-white/90 shadow-[0_18px_48px_rgba(37,99,255,0.08)]">
      <CardContent className="flex flex-col items-start justify-between gap-4 p-6 sm:flex-row sm:items-center">
        <div>
          <h2 className="font-semibold text-[#1E2A4A]">No pudimos cargar el panel</h2>
          <p className="mt-1 text-sm text-[#52617C]">Ocurrió un error inesperado al consultar la información administrativa.</p>
        </div>
        <Button onClick={onRetry} variant="outline" className="rounded-xl">
          <RefreshCw className="size-4" /> Reintentar
        </Button>
      </CardContent>
    </Card>
  );
}

function StatCard({ label, value, helper, icon: Icon, tone = "blue" }) {
  const toneClasses = tone === "violet"
    ? "bg-white/72 text-[#7C3AED] ring-[#7C3AED]/12"
    : "bg-white/72 text-[#2563FF] ring-[#2563FF]/12";
  const gradientClasses = tone === "violet"
    ? "from-white via-[#F7F3FF] to-[#DFF4FF]/45"
    : "from-white via-[#F4F8FF] to-[#DFF4FF]/75";

  return (
    <Card className={`group overflow-hidden border-[#D8E5F8]/80 bg-gradient-to-br ${gradientClasses} shadow-[0_18px_46px_rgba(37,99,255,0.08)] transition-all duration-200 hover:-translate-y-0.5 hover:shadow-[0_24px_60px_rgba(37,99,255,0.14)]`}>
      <CardContent className="p-5">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p className="text-sm font-semibold text-[#52617C]">{label}</p>
            <p className="mt-3 text-3xl font-semibold tracking-[-0.04em] text-[#1E2A4A]">{value}</p>
            <p className="mt-2 text-xs text-[#7C8CAB]">{helper}</p>
          </div>
          <div className={`grid size-11 place-items-center rounded-2xl shadow-[0_12px_28px_rgba(37,99,255,0.10)] ring-1 ${toneClasses}`}>
            <Icon className="size-[18px]" strokeWidth={1.8} aria-hidden="true" />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

function LoadingDashboard() {
  return (
    <div className="space-y-6" aria-label="Cargando dashboard">
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[1, 2, 3, 4].map((item) => <div key={item} className="h-36 animate-pulse rounded-2xl bg-white/80" />)}
      </div>
      <div className="grid gap-6 xl:grid-cols-[1.65fr_1fr]">
        <div className="h-80 animate-pulse rounded-2xl bg-white/80" />
        <div className="h-80 animate-pulse rounded-2xl bg-white/80" />
      </div>
    </div>
  );
}

function AdminDashboard() {
  const [summary, setSummary] = useState(null);
  const [students, setStudents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const [isFallback, setIsFallback] = useState(false);

  const loadDashboard = useCallback(async () => {
    setIsLoading(true);
    setHasError(false);
    try {
      const [summaryResult, studentsResult] = await Promise.all([getAdminSummary(), getAdminStudents()]);
      setSummary(summaryResult.data);
      setStudents(studentsResult.data);
      setIsFallback(summaryResult.isFallback || studentsResult.isFallback);
    } catch {
      setHasError(true);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  const distribution = useMemo(() => {
    const total = students.length || 1;
    return levelOrder.map((level) => {
      const count = students.filter((student) => student.level === level).length;
      return { level, count, percentage: Math.round((count / total) * 100) };
    });
  }, [students]);

  const averageProgress = summary?.average_progress;

  return (
    <AdminLayout
      title="Dashboard"
      description="Indicadores generales y seguimiento académico de los estudiantes de NeuroTutor."
      actions={
        <Button onClick={loadDashboard} variant="outline" className="rounded-xl border-[#D8E5F8] bg-white text-[#52617C] shadow-sm hover:bg-[#F4F8FF]">
          <RefreshCw className="size-4" /> Actualizar
        </Button>
      }
    >
      {isLoading && <LoadingDashboard />}
      {!isLoading && hasError && <ErrorState onRetry={loadDashboard} />}
      {!isLoading && !hasError && summary && (
        <div className="space-y-6">
          {isFallback && <DataNotice />}

          <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4" aria-label="Resumen administrativo">
            <StatCard label="Total estudiantes" value={summary.total_students ?? 0} helper="Registrados en la plataforma" icon={Users} />
            <StatCard label="Estudiantes activos" value={summary.active_students ?? 0} helper="Con acceso disponible" icon={UserCheck} tone="violet" />
            <StatCard label="Total módulos" value={summary.total_modules ?? 0} helper="Contenido publicado" icon={BookOpen} />
            <StatCard
              label="Progreso promedio"
              value={Number.isFinite(averageProgress) ? `${averageProgress}%` : "N/D"}
              helper={Number.isFinite(averageProgress) ? "Promedio general" : "Sin datos de progreso"}
              icon={TrendingUp}
              tone="violet"
            />
          </section>

          <div className="grid gap-6 xl:grid-cols-[1.65fr_1fr]">
            <Card className="overflow-hidden border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
              <CardHeader className="flex-row items-center justify-between border-b border-[#E5EEFC] px-5 py-4">
                <div>
                  <CardTitle className="text-base font-semibold text-[#1E2A4A]">Vista general de estudiantes</CardTitle>
                  <p className="mt-1 text-xs text-[#7C8CAB]">Actividad y rendimiento reciente</p>
                </div>
                <Button asChild variant="ghost" className="rounded-lg text-[#2563FF] hover:bg-[#DFF4FF]/55 hover:text-[#1E2A4A]">
                  <Link to="/admin/students">Ver todos <ArrowRight className="size-4" /></Link>
                </Button>
              </CardHeader>
              <CardContent className="p-0">
                <div className="hidden grid-cols-[1.5fr_.6fr_.6fr_.7fr_.6fr_.7fr] gap-3 border-b border-[#E5EEFC] bg-[#F4F8FF]/70 px-5 py-3 text-[11px] font-semibold uppercase tracking-wide text-[#7C8CAB] md:grid">
                  <span>Nombre</span><span>Grado</span><span>Sección</span><span>Nivel</span><span>Puntos</span><span>Estado</span>
                </div>
                <div className="divide-y divide-[#E5EEFC]">
                  {students.slice(0, 5).map((student) => (
                    <Link
                      key={student.id}
                      to={`/admin/students/${student.id}`}
                      className="grid gap-2 px-5 py-4 transition-colors hover:bg-[#F4F8FF]/70 md:grid-cols-[1.5fr_.6fr_.6fr_.7fr_.6fr_.7fr] md:items-center md:gap-3"
                    >
                      <div className="min-w-0">
                        <p className="truncate text-sm font-medium text-[#1E2A4A]">{student.name}</p>
                        <p className="mt-0.5 truncate text-xs text-[#7C8CAB] md:hidden">{student.email}</p>
                      </div>
                      <p className="text-sm text-slate-600">{student.grade || "—"}</p>
                      <p className="text-sm text-slate-600">{student.section || "—"}</p>
                      <p className="text-xs font-semibold text-[#7C3AED]">{levelLabels[student.level] || student.level || "—"}</p>
                      <p className="text-sm font-medium text-[#52617C]">{student.points ?? 0}</p>
                      <AdminStatusBadge status={student.status} />
                    </Link>
                  ))}
                  {students.length === 0 && <p className="px-5 py-10 text-center text-sm text-slate-400">No hay estudiantes registrados.</p>}
                </div>
              </CardContent>
            </Card>

            <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
              <CardHeader className="border-b border-[#E5EEFC] px-5 py-4">
                <div className="flex items-center gap-3">
                  <div className="grid size-10 place-items-center rounded-2xl bg-[#DFF4FF] text-[#7C3AED] shadow-[0_12px_28px_rgba(124,58,237,0.10)]">
                    <ChartNoAxesColumnIncreasing className="size-[18px]" aria-hidden="true" />
                  </div>
                  <div>
                    <CardTitle className="text-base font-semibold text-[#1E2A4A]">Distribución por nivel</CardTitle>
                    <p className="mt-1 text-xs text-[#7C8CAB]">Resultado diagnóstico actual</p>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-6 p-5">
                {distribution.map(({ level, count, percentage }) => (
                  <div key={level}>
                    <div className="mb-2 flex items-center justify-between text-sm">
                      <span className="font-medium text-[#52617C]">{levelLabels[level]}</span>
                      <span className="text-[#7C8CAB]">{count} · {percentage}%</span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-[#E5EEFC]">
                      <div
                        className="h-full rounded-full bg-gradient-to-r from-[#2563FF] to-[#7C3AED]"
                        style={{ width: `${percentage}%` }}
                      />
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          </div>
        </div>
      )}
    </AdminLayout>
  );
}

export { DataNotice, ErrorState };
export default AdminDashboard;
