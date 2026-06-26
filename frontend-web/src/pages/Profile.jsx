import { useEffect, useState } from "react";
import { Save, UserRound } from "lucide-react";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getStudentDashboard } from "../services/dashboardService";
import { getStudentProfile, updateStudentProfile } from "../services/profileService";
import { getStudentId } from "../utils/auth";

const emptyProfile = {
  name: "",
  grade: "",
  section: "",
  avatar_url: "",
  gender: "",
};

function Profile() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(emptyProfile);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const studentId = getStudentId();

  const mapLegacyProfile = (data) => {
    const [grade = "", section = ""] = (data?.gradoSeccion || "").split(" ");
    return {
      name: data?.nombreCompleto ?? "",
      grade,
      section,
      avatar_url: "",
      gender: data?.genero ?? "",
      email: "",
      level: data?.nivelActual ?? "",
      points: data?.puntosTotales ?? 0,
      diagnostic_completed: true,
      isLegacyFallback: true,
    };
  };

  const getErrorStatus = (error) => error?.response?.status;
  const getErrorPath = (error) => error?.response?.data?.path || "";

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", onClick: () => navigate("/learning-path") },
    { label: "Mis Logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", active: true, onClick: () => navigate("/profile") },
  ];

  useEffect(() => {
    if (!studentId) {
      setError("No se encontro el ID del estudiante. Inicia sesion nuevamente.");
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError("");

    getStudentProfile(studentId)
      .then((data) => {
        setProfile({
          name: data.name ?? "",
          grade: data.grade ?? "",
          section: data.section ?? "",
          avatar_url: data.avatar_url ?? "",
          gender: data.gender ?? "",
          email: data.email ?? "",
          level: data.level ?? "",
          points: data.points ?? 0,
          diagnostic_completed: data.diagnostic_completed,
        });
      })
      .catch(async (profileError) => {
        try {
          const legacyProfile = await getStudentDashboard(studentId);
          setProfile(mapLegacyProfile(legacyProfile));
          setError("El endpoint nuevo de perfil no esta disponible en el backend activo. Se muestran datos basicos del dashboard.");
        } catch {
          setError("No se pudo cargar tu perfil desde el servidor.");
        }
      })
      .finally(() => setIsLoading(false));
  }, [studentId]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setProfile((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!studentId) return;

    setIsSaving(true);
    setMessage("");
    setError("");

    try {
      const payload = {
        ...(profile.name?.trim() ? { name: profile.name.trim() } : {}),
        grade: profile.grade?.trim() || null,
        section: profile.section?.trim() || null,
        avatar_url: profile.avatar_url?.trim() || null,
        gender: profile.gender || null,
      };

      const updated = await updateStudentProfile(studentId, payload);

      setProfile((current) => ({ ...current, ...updated }));
      setMessage("Perfil actualizado correctamente.");
    } catch (saveError) {
      if (getErrorStatus(saveError) === 404 && getErrorPath(saveError).includes("/profile")) {
        setError("El backend activo no tiene disponible PUT /api/students/{id}/profile. Despliega los cambios nuevos o apunta VITE_API_URL a tu backend local actualizado.");
      } else if (getErrorStatus(saveError) === 404) {
        setError("No se encontro el estudiante para actualizar el perfil.");
      } else {
        setError("No se pudo actualizar el perfil. Intenta nuevamente.");
      }
    } finally {
      setIsSaving(false);
    }
  };

  const avatarSrc =
    profile.avatar_url ||
    (String(profile.gender).toLowerCase() === "masculino" ? "/assets/avatar-boy.png" : "/assets/avatar-girl.png");

  return (
    <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
      <section className="rounded-nt-card border border-white/85 bg-white/92 p-5 shadow-nt-card backdrop-blur">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <span className="inline-flex rounded-full bg-nt-blue/10 px-3 py-1 text-xs font-black text-nt-blue">
              Perfil
            </span>
            <h1 className="mt-3 text-3xl font-black text-nt-text-primary md:text-4xl">
              Mi informacion
            </h1>
            <p className="mt-2 text-sm font-semibold text-nt-text-secondary">
              Actualiza tus datos visibles dentro de NeuroTutor.
            </p>
          </div>
          <div className="flex items-center gap-3 rounded-[26px] bg-nt-sky/70 p-3">
            <img src={avatarSrc} alt="" className="size-20 rounded-full border-4 border-white object-cover shadow-sm" />
            <div>
              <p className="text-lg font-black text-nt-text-primary">{profile.name || "Estudiante"}</p>
              <p className="text-sm font-bold text-nt-text-secondary">{profile.level || "Nivel pendiente"}</p>
              <p className="text-xs font-black text-nt-blue">{profile.points ?? 0} pts</p>
            </div>
          </div>
        </div>
      </section>

      <Card className="rounded-nt-card border border-white/85 bg-white/92 p-0 shadow-nt-card">
        <CardContent className="p-5">
          {isLoading ? (
            <div className="flex min-h-[280px] items-center justify-center">
              <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
            </div>
          ) : (
            <form className="grid gap-4" onSubmit={handleSubmit}>
              {error && (
                <p className="rounded-[18px] border border-red-200 bg-red-50 px-4 py-3 text-sm font-bold text-red-700">
                  {error}
                </p>
              )}
              {message && (
                <p className="rounded-[18px] border border-green-200 bg-green-50 px-4 py-3 text-sm font-bold text-green-700">
                  {message}
                </p>
              )}

              <div className="grid gap-4 md:grid-cols-2">
                <label className="grid gap-2">
                  <span className="text-sm font-black text-nt-text-primary">Nombre</span>
                  <input name="name" value={profile.name} onChange={handleChange} className="h-12 rounded-[18px] border border-nt-border bg-white px-4 text-sm font-bold outline-none focus:border-nt-blue" />
                </label>
                <label className="grid gap-2">
                  <span className="text-sm font-black text-nt-text-primary">Correo</span>
                  <input value={profile.email || ""} disabled className="h-12 rounded-[18px] border border-nt-border bg-slate-50 px-4 text-sm font-bold text-nt-text-secondary" />
                </label>
                <label className="grid gap-2">
                  <span className="text-sm font-black text-nt-text-primary">Grado</span>
                  <input name="grade" value={profile.grade} onChange={handleChange} className="h-12 rounded-[18px] border border-nt-border bg-white px-4 text-sm font-bold outline-none focus:border-nt-blue" />
                </label>
                <label className="grid gap-2">
                  <span className="text-sm font-black text-nt-text-primary">Seccion</span>
                  <input name="section" value={profile.section} onChange={handleChange} className="h-12 rounded-[18px] border border-nt-border bg-white px-4 text-sm font-bold outline-none focus:border-nt-blue" />
                </label>
                <label className="grid gap-2">
                  <span className="text-sm font-black text-nt-text-primary">Genero</span>
                  <select name="gender" value={profile.gender} onChange={handleChange} className="h-12 rounded-[18px] border border-nt-border bg-white px-4 text-sm font-bold outline-none focus:border-nt-blue">
                    <option value="">Sin especificar</option>
                    <option value="femenino">Femenino</option>
                    <option value="masculino">Masculino</option>
                  </select>
                </label>
                <label className="grid gap-2">
                  <span className="text-sm font-black text-nt-text-primary">Avatar URL</span>
                  <input name="avatar_url" value={profile.avatar_url} onChange={handleChange} placeholder="/assets/avatar-girl.png" className="h-12 rounded-[18px] border border-nt-border bg-white px-4 text-sm font-bold outline-none focus:border-nt-blue" />
                </label>
              </div>

              <Button type="submit" disabled={isSaving} className="mt-2 h-12 rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white">
                {isSaving ? (
                  "Guardando..."
                ) : (
                  <>
                    <Save className="size-4" />
                    Guardar perfil
                  </>
                )}
              </Button>
            </form>
          )}
        </CardContent>
      </Card>

      <section className="rounded-[28px] border border-white/80 bg-white/80 p-4 text-sm font-bold text-nt-text-secondary shadow-sm">
        <UserRound className="mb-2 size-5 text-nt-blue" />
        El correo no se puede editar desde esta pantalla.
      </section>
    </StudentLayout>
  );
}

export default Profile;
