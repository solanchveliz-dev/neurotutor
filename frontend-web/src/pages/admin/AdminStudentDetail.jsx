import { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, Award, BookCheck, GraduationCap, Mail, RefreshCw, School, UserRound } from "lucide-react";
import AdminLayout from "@/components/admin/AdminLayout";
import AdminStatusBadge from "@/components/admin/AdminStatusBadge";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getAdminStudentById } from "@/services/adminService";
import { ErrorState } from "./AdminDashboard";

const levelLabels = { BASICO: "Básico", INTERMEDIO: "Intermedio", AVANZADO: "Avanzado" };

function ProfileField({ label, value, icon: Icon }) {
  return (
    <div className="flex items-start gap-3 rounded-2xl border border-[#E5EEFC] bg-[#F4F8FF]/70 p-4">
      <div className="grid size-9 shrink-0 place-items-center rounded-xl bg-white text-[#2563FF] shadow-sm">
        <Icon className="size-4" strokeWidth={1.8} aria-hidden="true" />
      </div>
      <div className="min-w-0">
        <p className="text-xs text-[#7C8CAB]">{label}</p>
        <p className="mt-1 break-words text-sm font-medium text-[#1E2A4A]">{value || "Sin datos disponibles"}</p>
      </div>
    </div>
  );
}

function ProgressRow({ label, value }) {
  const hasValue = Number.isFinite(value);
  return (
    <div>
      <div className="mb-2 flex items-center justify-between text-sm">
        <span className="font-medium text-[#52617C]">{label}</span>
        <span className="text-[#7C8CAB]">{hasValue ? `${value}%` : "Sin datos"}</span>
      </div>
      <div className="h-2 overflow-hidden rounded-full bg-[#E5EEFC]">
        <div
          className={`h-full rounded-full ${hasValue ? "bg-gradient-to-r from-[#2563FF] to-[#7C3AED]" : "bg-[#D8E5F8]"}`}
          style={{ width: hasValue ? `${Math.min(100, Math.max(0, value))}%` : "0%" }}
        />
      </div>
    </div>
  );
}

function AdminStudentDetail() {
  const { id } = useParams();
  const [student, setStudent] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const loadStudent = useCallback(async () => {
    setIsLoading(true);
    setHasError(false);
    setErrorMessage("");
    try {
      const result = await getAdminStudentById(id);
      setStudent(result ? {
        ...result,
        progress: result.progress && typeof result.progress === "object" ? result.progress : {},
        completed_modules: Array.isArray(result.completed_modules) ? result.completed_modules : [],
      } : null);
    } catch (error) {
      setHasError(true);
      setStudent(null);
      setErrorMessage(error.message);
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadStudent();
  }, [loadStudent]);

  const averageProgress = useMemo(() => {
    const values = Object.values(student?.progress || {}).filter(Number.isFinite);
    return values.length ? Math.round(values.reduce((total, value) => total + value, 0) / values.length) : null;
  }, [student]);

  return (
    <AdminLayout
      eyebrow="Estudiantes / Perfil"
      title="Detalle del estudiante"
      description="Información académica, diagnóstico y avance individual."
      actions={
        <Button asChild variant="outline" className="rounded-xl border-[#D8E5F8] bg-white text-[#52617C] shadow-sm hover:bg-[#F4F8FF]">
          <Link to="/admin/students"><ArrowLeft className="size-4" /> Volver</Link>
        </Button>
      }
    >
      {isLoading && (
        <div className="space-y-5">
          <div className="h-48 animate-pulse rounded-2xl bg-white/80" />
          <div className="grid gap-5 lg:grid-cols-2"><div className="h-80 animate-pulse rounded-2xl bg-white/80" /><div className="h-80 animate-pulse rounded-2xl bg-white/80" /></div>
        </div>
      )}
      {!isLoading && hasError && <ErrorState onRetry={loadStudent} message={errorMessage} />}
      {!isLoading && !hasError && student && (
        <div className="space-y-6">
          <Card className="overflow-hidden border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
            <div className="h-1 bg-gradient-to-r from-[#2563FF] to-[#7C3AED]" />
            <CardContent className="flex flex-col gap-5 p-6 sm:flex-row sm:items-center sm:justify-between">
              <div className="flex min-w-0 items-center gap-4">
                <div className="grid size-16 shrink-0 place-items-center rounded-2xl bg-gradient-to-br from-[#2563FF] to-[#7C3AED] text-lg font-semibold text-white shadow-[0_18px_38px_rgba(37,99,255,0.22)]">
                  {(student.name || "E").split(" ").slice(0, 2).map((part) => part[0]).join("")}
                </div>
                <div className="min-w-0">
                  <div className="flex flex-wrap items-center gap-2">
                    <h2 className="truncate text-xl font-semibold tracking-[-0.02em] text-[#1E2A4A]">{student.name}</h2>
                    <AdminStatusBadge status={student.status} />
                  </div>
                  <p className="mt-1 truncate text-sm text-[#52617C]">{student.email}</p>
                  <div className="mt-3 flex flex-wrap gap-2">
                    <Badge className="border-0 bg-[#DFF4FF] text-[#2563FF] hover:bg-[#DFF4FF]">{student.grade} · Sección {student.section}</Badge>
                    <Badge className="border-0 bg-[#F2EAFE] text-[#7C3AED] hover:bg-[#F2EAFE]">Nivel {levelLabels[student.level] || student.level}</Badge>
                  </div>
                </div>
              </div>
              <div className="rounded-2xl border border-[#E5EEFC] bg-[#F4F8FF] px-5 py-3 text-left sm:text-right">
                <p className="text-xs text-[#7C8CAB]">Puntos acumulados</p>
                <p className="mt-1 text-2xl font-semibold tracking-tight text-[#1E2A4A]">{student.points ?? 0}</p>
              </div>
            </CardContent>
          </Card>

          <div className="grid gap-6 xl:grid-cols-[1fr_1.1fr]">
            <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
              <CardHeader className="border-b border-[#E5EEFC] px-5 py-4">
                <CardTitle className="text-base font-semibold text-[#1E2A4A]">Perfil del estudiante</CardTitle>
              </CardHeader>
              <CardContent className="grid gap-3 p-5 sm:grid-cols-2">
                <ProfileField label="Nombre completo" value={student.name} icon={UserRound} />
                <ProfileField label="Correo electrónico" value={student.email} icon={Mail} />
                <ProfileField label="Grado y sección" value={`${student.grade || "—"} ${student.section || ""}`} icon={School} />
                <ProfileField label="Puntos totales" value={student.points ?? 0} icon={Award} />
              </CardContent>
            </Card>

            <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
              <CardHeader className="border-b border-[#E5EEFC] px-5 py-4">
                <div className="flex items-center justify-between gap-3">
                  <div>
                    <CardTitle className="text-base font-semibold text-[#1E2A4A]">Progreso académico</CardTitle>
                    <p className="mt-1 text-xs text-[#7C8CAB]">Avance por área de aprendizaje</p>
                  </div>
                  <span className="text-sm font-semibold text-[#7C3AED]">{Number.isFinite(averageProgress) ? `${averageProgress}%` : "N/D"}</span>
                </div>
              </CardHeader>
              <CardContent className="space-y-6 p-5">
                <ProgressRow label="Fracciones" value={student.progress?.fractions} />
                <ProgressRow label="Decimales" value={student.progress?.decimals} />
                <ProgressRow label="Porcentajes" value={student.progress?.percentages} />
              </CardContent>
            </Card>
          </div>

          <div className="grid gap-6 lg:grid-cols-2">
            <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
              <CardHeader className="flex-row items-center gap-3 border-b border-[#E5EEFC] px-5 py-4">
                <div className="grid size-10 place-items-center rounded-2xl bg-[#DFF4FF] text-[#2563FF] shadow-[0_12px_28px_rgba(37,99,255,0.10)]"><GraduationCap className="size-[18px]" /></div>
                <div><CardTitle className="text-base font-semibold text-[#1E2A4A]">Resultado diagnóstico</CardTitle><p className="mt-1 text-xs text-[#7C8CAB]">Nivel asignado al estudiante</p></div>
              </CardHeader>
              <CardContent className="p-5">
                <p className="text-2xl font-semibold tracking-tight text-[#1E2A4A]">{levelLabels[student.level] || student.level || "Sin datos"}</p>
                <p className="mt-2 text-sm leading-6 text-[#52617C]">Este nivel determina la ruta de aprendizaje recomendada.</p>
              </CardContent>
            </Card>

            <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
              <CardHeader className="flex-row items-center justify-between gap-3 border-b border-[#E5EEFC] px-5 py-4">
                <div className="flex items-center gap-3">
                  <div className="grid size-10 place-items-center rounded-2xl bg-[#F2EAFE] text-[#7C3AED] shadow-[0_12px_28px_rgba(124,58,237,0.10)]"><BookCheck className="size-[18px]" /></div>
                  <div><CardTitle className="text-base font-semibold text-[#1E2A4A]">Módulos completados</CardTitle><p className="mt-1 text-xs text-[#7C8CAB]">Contenido finalizado</p></div>
                </div>
                <span className="text-sm font-semibold text-[#52617C]">{student.completed_modules?.length ?? 0}</span>
              </CardHeader>
              <CardContent className="p-5">
                {student.completed_modules?.length ? (
                  <ul className="space-y-3">
                    {student.completed_modules.map((module) => (
                      <li key={module} className="flex items-center gap-3 text-sm text-[#52617C]">
                        <span className="grid size-5 place-items-center rounded-full bg-[#DFF4FF] text-[10px] font-bold text-[#2563FF]">✓</span>{module}
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm leading-6 text-[#7C8CAB]">La API todavía no proporciona información de módulos completados.</p>
                )}
              </CardContent>
            </Card>
          </div>

          <div className="flex justify-end">
            <Button onClick={loadStudent} variant="outline" className="rounded-xl border-[#D8E5F8] bg-white text-[#52617C] shadow-sm hover:bg-[#F4F8FF]">
              <RefreshCw className="size-4" /> Actualizar perfil
            </Button>
          </div>
        </div>
      )}
    </AdminLayout>
  );
}

export default AdminStudentDetail;
