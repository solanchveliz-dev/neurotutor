import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { AlertCircle, Eye, RefreshCw, Search, Users } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { getAdminStudents } from "@/services/adminService";
import { AdminLayout, ErrorState } from "./AdminDashboard";

const statusLabels = {
  active: "Activo",
  inactive: "Inactivo",
};

function StudentStatusBadge({ status }) {
  const isActive = status === "active";

  return (
    <Badge
      className={`h-6 rounded-full px-3 font-black ${
        isActive
          ? "bg-emerald-50 text-emerald-700 ring-1 ring-emerald-100 hover:bg-emerald-50"
          : "bg-slate-100 text-slate-600 ring-1 ring-slate-200 hover:bg-slate-100"
      }`}
    >
      {statusLabels[status] || status}
    </Badge>
  );
}

function AdminStudents() {
  const [students, setStudents] = useState([]);
  const [query, setQuery] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);

  const loadStudents = async () => {
    setIsLoading(true);
    setHasError(false);

    try {
      const data = await getAdminStudents();
      setStudents(data);
    } catch {
      setHasError(true);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadStudents();
  }, []);

  const filteredStudents = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();

    if (!normalizedQuery) return students;

    return students.filter((student) =>
      [student.name, student.email, student.grade, student.section, student.level, student.status]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(normalizedQuery))
    );
  }, [query, students]);

  return (
    <AdminLayout
      title="Estudiantes"
      description="Consulta la informacion basica de estudiantes entregada por el backend administrativo Django."
    >
      <div className="space-y-5">
        <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
          <CardContent className="flex flex-col gap-4 p-4 md:flex-row md:items-center md:justify-between">
            <div className="relative w-full md:max-w-md">
              <Search className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-slate-400" aria-hidden="true" />
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Buscar por nombre, correo, grado o nivel"
                className="h-11 w-full rounded-2xl border border-slate-200 bg-slate-50 pl-11 pr-4 text-sm font-semibold text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-300 focus:bg-white focus:ring-4 focus:ring-blue-100"
              />
            </div>
            <Button onClick={loadStudents} variant="outline" className="h-10 rounded-2xl border-slate-200 bg-white font-bold">
              <RefreshCw className="size-4" aria-hidden="true" />
              Actualizar
            </Button>
          </CardContent>
        </Card>

        {isLoading && (
          <div className="grid gap-4">
            {[1, 2, 3].map((item) => (
              <Card key={item} className="rounded-3xl border border-slate-200 bg-white shadow-sm">
                <CardContent className="p-5">
                  <div className="h-5 w-48 animate-pulse rounded-full bg-slate-100" />
                  <div className="mt-4 h-4 w-72 max-w-full animate-pulse rounded-full bg-slate-100" />
                </CardContent>
              </Card>
            ))}
          </div>
        )}

        {!isLoading && hasError && <ErrorState onRetry={loadStudents} />}

        {!isLoading && !hasError && filteredStudents.length === 0 && (
          <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
            <CardContent className="flex items-center gap-3 p-6 text-slate-500">
              <AlertCircle className="size-5" aria-hidden="true" />
              <p className="text-sm font-bold">No se encontraron estudiantes con ese criterio.</p>
            </CardContent>
          </Card>
        )}

        {!isLoading && !hasError && filteredStudents.length > 0 && (
          <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
            <div className="hidden grid-cols-[1.5fr_1.2fr_0.7fr_0.8fr_0.8fr_0.4fr] gap-4 border-b border-slate-200 bg-slate-50 px-5 py-3 text-xs font-black uppercase tracking-wide text-slate-400 lg:grid">
              <span>Estudiante</span>
              <span>Correo</span>
              <span>Grado</span>
              <span>Nivel</span>
              <span>Estado</span>
              <span className="text-right">Ver</span>
            </div>

            <div className="divide-y divide-slate-100">
              {filteredStudents.map((student) => (
                <article
                  key={student.id}
                  className="grid gap-4 px-5 py-5 transition hover:bg-slate-50 lg:grid-cols-[1.5fr_1.2fr_0.7fr_0.8fr_0.8fr_0.4fr] lg:items-center"
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <div className="flex size-11 shrink-0 items-center justify-center rounded-2xl bg-blue-50 text-blue-700">
                      <Users className="size-5" aria-hidden="true" />
                    </div>
                    <div className="min-w-0">
                      <h3 className="truncate text-sm font-black text-slate-950">{student.name}</h3>
                      <p className="text-xs font-bold text-slate-400">ID {student.id}</p>
                    </div>
                  </div>
                  <p className="min-w-0 truncate text-sm font-semibold text-slate-600">{student.email}</p>
                  <p className="text-sm font-bold text-slate-700">
                    {student.grade} {student.section}
                  </p>
                  <p className="text-sm font-bold text-violet-700">{student.level}</p>
                  <StudentStatusBadge status={student.status} />
                  <div className="flex justify-start lg:justify-end">
                    <Button asChild variant="outline" size="icon" className="rounded-2xl border-slate-200 bg-white">
                      <Link to={`/admin/students/${student.id}`} aria-label={`Ver detalle de ${student.name}`}>
                        <Eye className="size-4" aria-hidden="true" />
                      </Link>
                    </Button>
                  </div>
                </article>
              ))}
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}

export { StudentStatusBadge };
export default AdminStudents;
