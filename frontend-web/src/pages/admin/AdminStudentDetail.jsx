import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, Award, Mail, RefreshCw, School, UserRound } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { getAdminStudentById } from "@/services/adminService";
import { AdminLayout, ErrorState } from "./AdminDashboard";
import { StudentStatusBadge } from "./AdminStudents";

function DetailItem({ label, value, icon: Icon }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="mb-4 flex size-11 items-center justify-center rounded-2xl bg-blue-50 text-blue-700">
        <Icon className="size-5" aria-hidden="true" />
      </div>
      <p className="text-xs font-black uppercase tracking-wide text-slate-400">{label}</p>
      <p className="mt-2 break-words text-base font-black text-slate-950">{value || "Sin dato"}</p>
    </div>
  );
}

function AdminStudentDetail() {
  const { id } = useParams();
  const [student, setStudent] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);

  const loadStudent = async () => {
    setIsLoading(true);
    setHasError(false);

    try {
      const data = await getAdminStudentById(id);
      setStudent(data);
    } catch {
      setHasError(true);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadStudent();
  }, [id]);

  return (
    <AdminLayout
      title="Detalle del estudiante"
      description="Informacion individual obtenida directamente desde la API administrativa Django."
    >
      <div className="space-y-5">
        <Button asChild variant="outline" className="h-10 rounded-2xl border-slate-200 bg-white font-bold">
          <Link to="/admin/students">
            <ArrowLeft className="size-4" aria-hidden="true" />
            Volver a estudiantes
          </Link>
        </Button>

        {isLoading && (
          <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
            <CardContent className="p-6">
              <div className="h-7 w-64 max-w-full animate-pulse rounded-full bg-slate-100" />
              <div className="mt-4 h-4 w-96 max-w-full animate-pulse rounded-full bg-slate-100" />
              <div className="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                {[1, 2, 3, 4].map((item) => (
                  <div key={item} className="h-32 animate-pulse rounded-3xl bg-slate-100" />
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        {!isLoading && hasError && <ErrorState onRetry={loadStudent} />}

        {!isLoading && !hasError && student && (
          <>
            <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
              <CardHeader className="border-b border-slate-100">
                <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                  <div className="flex min-w-0 items-center gap-4">
                    <div className="flex size-14 shrink-0 items-center justify-center rounded-3xl bg-gradient-to-br from-blue-600 to-violet-600 text-white shadow-lg shadow-blue-500/20">
                      <UserRound className="size-6" aria-hidden="true" />
                    </div>
                    <div className="min-w-0">
                      <CardTitle className="truncate text-2xl font-black text-slate-950">{student.name}</CardTitle>
                      <p className="mt-1 text-sm font-semibold text-slate-500">{student.email}</p>
                    </div>
                  </div>
                  <StudentStatusBadge status={student.status} />
                </div>
              </CardHeader>
              <CardContent className="p-5">
                <div className="flex flex-wrap gap-2">
                  <Badge className="h-7 bg-blue-50 px-3 font-black text-blue-700 ring-1 ring-blue-100 hover:bg-blue-50">
                    ID {student.id}
                  </Badge>
                  <Badge className="h-7 bg-violet-50 px-3 font-black text-violet-700 ring-1 ring-violet-100 hover:bg-violet-50">
                    Nivel {student.level}
                  </Badge>
                  <Badge className="h-7 bg-amber-50 px-3 font-black text-amber-700 ring-1 ring-amber-100 hover:bg-amber-50">
                    {student.points} puntos
                  </Badge>
                </div>
              </CardContent>
            </Card>

            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
              <DetailItem label="Nombre" value={student.name} icon={UserRound} />
              <DetailItem label="Correo" value={student.email} icon={Mail} />
              <DetailItem label="Grado y seccion" value={`${student.grade} ${student.section}`} icon={School} />
              <DetailItem label="Puntaje" value={student.points} icon={Award} />
            </div>

            <Card className="rounded-3xl border border-slate-200 bg-white shadow-sm">
              <CardContent className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h3 className="text-base font-black text-slate-950">Datos temporales</h3>
                  <p className="mt-1 text-sm font-semibold text-slate-500">
                    Este detalle usa la API Django inicial con listas Python en memoria.
                  </p>
                </div>
                <Button onClick={loadStudent} variant="outline" className="h-10 rounded-2xl border-slate-200 bg-white font-bold">
                  <RefreshCw className="size-4" aria-hidden="true" />
                  Recargar
                </Button>
              </CardContent>
            </Card>
          </>
        )}
      </div>
    </AdminLayout>
  );
}

export default AdminStudentDetail;
