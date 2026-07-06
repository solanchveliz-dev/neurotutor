import { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { ChevronRight, Filter, RefreshCw, Search, Users } from "lucide-react";
import AdminLayout from "@/components/admin/AdminLayout";
import AdminStatusBadge from "@/components/admin/AdminStatusBadge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getAdminStudents } from "@/services/adminService";
import { ErrorState } from "./AdminDashboard";

const levelLabels = { BASICO: "Básico", INTERMEDIO: "Intermedio", AVANZADO: "Avanzado" };

function AdminStudents() {
  const [searchParams] = useSearchParams();
  const [students, setStudents] = useState([]);
  const [query, setQuery] = useState(searchParams.get("query") || "");
  const [grade, setGrade] = useState("all");
  const [level, setLevel] = useState("all");
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const loadStudents = useCallback(async () => {
    setIsLoading(true);
    setHasError(false);
    setErrorMessage("");
    try {
      const result = await getAdminStudents();
      const studentList = Array.isArray(result)
        ? result
        : Array.isArray(result?.students)
          ? result.students
          : [];
      setStudents(studentList);
    } catch (error) {
      setHasError(true);
      setStudents([]);
      setErrorMessage(error.message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStudents();
  }, [loadStudents]);

  useEffect(() => {
    setQuery(searchParams.get("query") || "");
  }, [searchParams]);

  const grades = useMemo(
    () => [...new Set(students.map((student) => student.grade).filter(Boolean))].sort(),
    [students]
  );
  const levels = useMemo(
    () => [...new Set(students.map((student) => student.level).filter(Boolean))],
    [students]
  );

  const filteredStudents = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();
    return students.filter((student) => {
      const matchesQuery = !normalizedQuery || [student.name, student.email, student.section]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(normalizedQuery));
      const matchesGrade = grade === "all" || student.grade === grade;
      const matchesLevel = level === "all" || student.level === level;
      return matchesQuery && matchesGrade && matchesLevel;
    });
  }, [grade, level, query, students]);

  return (
    <AdminLayout
      eyebrow="Gestión académica"
      title="Estudiantes"
      description="Consulta, filtra y revisa el progreso individual de los estudiantes registrados."
      actions={
        <Button onClick={loadStudents} variant="outline" className="rounded-xl border-[#D8E5F8] bg-white text-[#52617C] shadow-sm hover:bg-[#F4F8FF]">
          <RefreshCw className="size-4" /> Actualizar
        </Button>
      }
    >
      <div className="space-y-5">
        <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
          <CardContent className="flex flex-col gap-3 p-4 lg:flex-row lg:items-center">
            <div className="relative min-w-0 flex-1">
              <Search className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-[#7C8CAB]" aria-hidden="true" />
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Buscar por nombre, correo o sección"
                className="h-11 w-full rounded-2xl border border-[#D8E5F8] bg-[#F4F8FF]/80 pl-10 pr-4 text-sm text-[#1E2A4A] outline-none transition focus:border-[#2563FF]/40 focus:bg-white focus:ring-4 focus:ring-[#2563FF]/10"
              />
            </div>
            <div className="flex flex-col gap-3 sm:flex-row">
              <div className="relative">
                <Filter className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-[#7C8CAB]" aria-hidden="true" />
                <select
                  value={grade}
                  onChange={(event) => setGrade(event.target.value)}
                  className="h-11 min-w-40 appearance-none rounded-2xl border border-[#D8E5F8] bg-white pl-10 pr-8 text-sm text-[#52617C] outline-none focus:border-[#2563FF]/40 focus:ring-4 focus:ring-[#2563FF]/10"
                >
                  <option value="all">Todos los grados</option>
                  {grades.map((item) => <option key={item} value={item}>{item}</option>)}
                </select>
              </div>
              <select
                value={level}
                onChange={(event) => setLevel(event.target.value)}
                className="h-11 min-w-40 rounded-2xl border border-[#D8E5F8] bg-white px-3 text-sm text-[#52617C] outline-none focus:border-[#2563FF]/40 focus:ring-4 focus:ring-[#2563FF]/10"
              >
                <option value="all">Todos los niveles</option>
                {levels.map((item) => <option key={item} value={item}>{levelLabels[item] || item}</option>)}
              </select>
            </div>
          </CardContent>
        </Card>

        {isLoading && (
          <div className="space-y-2">
            {[1, 2, 3, 4].map((item) => <div key={item} className="h-20 animate-pulse rounded-2xl bg-white/80" />)}
          </div>
        )}
        {!isLoading && hasError && <ErrorState onRetry={loadStudents} message={errorMessage} />}

        {!isLoading && !hasError && (
          <Card className="overflow-hidden border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)] backdrop-blur">
            <div className="flex items-center justify-between border-b border-[#E5EEFC] px-5 py-4">
              <div>
                <p className="text-sm font-semibold text-[#1E2A4A]">Directorio de estudiantes</p>
                <p className="mt-1 text-xs text-[#7C8CAB]">{filteredStudents.length} resultados</p>
              </div>
              <div className="grid size-10 place-items-center rounded-2xl bg-[#DFF4FF] text-[#2563FF] shadow-[0_12px_28px_rgba(37,99,255,0.10)]">
                <Users className="size-[18px]" aria-hidden="true" />
              </div>
            </div>
            <div className="hidden grid-cols-[1.5fr_.55fr_.55fr_.75fr_.65fr_.7fr_24px] gap-4 border-b border-[#E5EEFC] bg-[#F4F8FF]/70 px-5 py-3 text-[11px] font-semibold uppercase tracking-wide text-[#7C8CAB] lg:grid">
              <span>Estudiante</span><span>Grado</span><span>Sección</span><span>Nivel</span><span>Puntos</span><span>Estado</span><span />
            </div>
            <div className="divide-y divide-[#E5EEFC]">
              {filteredStudents.map((student) => (
                <Link
                  key={student.id}
                  to={`/admin/students/${student.id}`}
                  className="group grid gap-3 px-5 py-4 transition-colors hover:bg-[#F4F8FF]/70 lg:grid-cols-[1.5fr_.55fr_.55fr_.75fr_.65fr_.7fr_24px] lg:items-center lg:gap-4"
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <div className="grid size-10 shrink-0 place-items-center rounded-full bg-gradient-to-br from-[#DFF4FF] to-[#F2EAFE] text-xs font-semibold text-[#2563FF] shadow-[0_10px_22px_rgba(37,99,255,0.10)]">
                      {(student.name || "E").split(" ").slice(0, 2).map((part) => part[0]).join("")}
                    </div>
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-[#1E2A4A]">{student.name}</p>
                      <p className="mt-0.5 truncate text-xs text-[#7C8CAB]">{student.email}</p>
                    </div>
                  </div>
                  <p className="text-sm text-slate-600">{student.grade || "—"}</p>
                  <p className="text-sm text-slate-600">{student.section || "—"}</p>
                  <p className="text-xs font-semibold text-[#7C3AED]">{levelLabels[student.level] || student.level || "—"}</p>
                  <p className="text-sm font-medium text-[#52617C]">{student.points ?? 0}</p>
                  <AdminStatusBadge status={student.status} />
                  <ChevronRight className="size-4 text-[#B0BED3] transition group-hover:translate-x-0.5 group-hover:text-[#2563FF]" aria-hidden="true" />
                </Link>
              ))}
              {filteredStudents.length === 0 && (
                <div className="px-6 py-14 text-center">
                  <p className="text-sm font-medium text-slate-700">No encontramos estudiantes</p>
                  <p className="mt-1 text-xs text-slate-400">Prueba con otros filtros o términos de búsqueda.</p>
                </div>
              )}
            </div>
          </Card>
        )}
      </div>
    </AdminLayout>
  );
}

export default AdminStudents;
