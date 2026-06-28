import { useCallback, useEffect, useState } from "react";
import { BadgeCheck, CalendarDays, GraduationCap, Save, Star, UserRound } from "lucide-react";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import ProgressSummaryCard from "../components/student/ProgressSummaryCard";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getStudentProfile, updateStudentProfile } from "../services/profileService";
import { getStudentProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const emptyProfile = {
  name: "",
  grade: "",
  section: "",
  avatar_url: "",
  gender: "",
  email: "",
  level: "",
  points: 0,
  diagnostic_completed: false,
  created_at: "",
};

function Profile() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(emptyProfile);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [progress, setProgress] = useState(null);
  const [progressError, setProgressError] = useState("");
  const [profileLoaded, setProfileLoaded] = useState(false);

  const studentId = getStudentId();

  const getErrorStatus = (error) => error?.response?.status;
  const getErrorPath = (error) => error?.response?.data?.path || "";

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", onClick: () => navigate("/student-dashboard") },
    { label: "Mis Logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", active: true, onClick: () => navigate("/profile") },
  ];

  const loadProfile = useCallback(() => {
    if (!studentId) {
      setError("No se encontro el ID del estudiante. Inicia sesion nuevamente.");
      setProfileLoaded(false);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError("");
    setMessage("");
    setProfileLoaded(false);

    Promise.allSettled([getStudentProfile(studentId), getStudentProgress(studentId)])
      .then(([profileResult, progressResult]) => {
        if (progressResult.status === "fulfilled") {
          setProgress(progressResult.value);
          setProgressError("");
        } else {
          setProgress(null);
          setProgressError("No se pudo cargar el progreso en este momento.");
        }

        if (profileResult.status === "fulfilled") {
          const data = profileResult.value;
          setProfile({
            name: data.name ?? "",
            grade: data.grade ?? "",
            section: data.section ?? "",
            avatar_url: data.avatar_url ?? "",
            gender: data.gender ?? "",
            email: data.email ?? "",
            level: data.level ?? "",
            points: data.points ?? progressResult.value?.points ?? 0,
            diagnostic_completed: data.diagnostic_completed,
            created_at: data.created_at ?? "",
          });
          setProfileLoaded(true);
          return;
        }

        setProfile(emptyProfile);
        setError("No se pudo cargar tu perfil desde el servidor.");
      })
      .finally(() => setIsLoading(false));
  }, [studentId]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setProfile((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!studentId) return;

    const normalizedName = profile.name?.trim() ?? "";
    if (!normalizedName) {
      setError("El nombre es obligatorio.");
      return;
    }

    setIsSaving(true);
    setMessage("");
    setError("");

    try {
      const payload = {
        name: normalizedName,
        grade: profile.grade?.trim() ?? "",
        section: profile.section?.trim() ?? "",
        avatar_url: profile.avatar_url?.trim() ?? "",
        gender: profile.gender?.trim() ?? "",
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

  const initials = profile.name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join("") || "NT";
  const createdAtLabel = profile.created_at
    ? new Intl.DateTimeFormat("es-PE", { day: "numeric", month: "long", year: "numeric" })
        .format(new Date(profile.created_at))
    : "Sin fecha registrada";
  const predefinedAvatars = ["/assets/avatar-girl.png", "/assets/avatar-boy.png"];
  const hasCustomAvatar = profile.avatar_url && !predefinedAvatars.includes(profile.avatar_url);

  if (!isLoading && !profileLoaded) {
    return (
      <StudentLayout
        sidebar={<AppSidebar items={sidebarItems} />}
        topbar={<BackButton onClick={() => navigate("/student-dashboard")}>Volver al inicio</BackButton>}
        rightPanel={<ProgressSummaryCard progress={progress} error={progressError} />}
      >
        <section className="rounded-nt-card border border-amber-200 bg-white/92 p-8 text-center shadow-nt-card">
          <UserRound className="mx-auto size-12 text-nt-blue" />
          <h1 className="mt-4 text-2xl font-black text-nt-text-primary">No pudimos cargar tu perfil</h1>
          <p className="mt-2 text-sm font-semibold text-nt-text-secondary">{error}</p>
          <Button type="button" className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 font-black text-white" onClick={loadProfile}>
            Reintentar
          </Button>
        </section>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={<BackButton onClick={() => navigate("/student-dashboard")}>Volver al inicio</BackButton>}
      rightPanel={<ProgressSummaryCard progress={progress} isLoading={isLoading} error={progressError} />}
    >
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
          <div className="flex items-center gap-3 rounded-[26px] bg-gradient-to-br from-nt-sky/90 to-violet-50 p-3">
            {profile.avatar_url ? (
              <img src={profile.avatar_url} alt="" className="size-20 rounded-full border-4 border-white object-cover shadow-sm" />
            ) : (
              <div className="grid size-20 place-items-center rounded-full border-4 border-white bg-gradient-to-br from-nt-blue to-nt-purple text-xl font-black text-white shadow-sm">
                {initials}
              </div>
            )}
            <div>
              <p className="text-lg font-black text-nt-text-primary">{profile.name || "Estudiante"}</p>
              <p className="text-sm font-bold text-nt-text-secondary">{profile.level || "Nivel pendiente"}</p>
              <p className="text-xs font-black text-nt-blue">{profile.points ?? 0} pts</p>
            </div>
          </div>
        </div>
        <div className="mt-5 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <div className="flex items-center gap-3 rounded-[22px] bg-nt-sky/65 p-4">
            <GraduationCap className="size-5 text-nt-blue" />
            <div><p className="text-xs font-bold text-nt-text-secondary">Grado y sección</p><p className="font-black text-nt-text-primary">{profile.grade || "Sin grado"} {profile.section || ""}</p></div>
          </div>
          <div className="flex items-center gap-3 rounded-[22px] bg-violet-50 p-4">
            <UserRound className="size-5 text-nt-purple" />
            <div><p className="text-xs font-bold text-nt-text-secondary">Nivel</p><p className="font-black text-nt-text-primary">{profile.level || "Pendiente"}</p></div>
          </div>
          <div className="flex items-center gap-3 rounded-[22px] bg-amber-50 p-4">
            <Star className="size-5 fill-nt-yellow text-nt-yellow" />
            <div><p className="text-xs font-bold text-nt-text-secondary">Puntos</p><p className="font-black text-nt-text-primary">{profile.points ?? progress?.points ?? 0} pts</p></div>
          </div>
          <div className="flex items-center gap-3 rounded-[22px] bg-green-50 p-4">
            <BadgeCheck className="size-5 text-green-700" />
            <div><p className="text-xs font-bold text-nt-text-secondary">Diagnóstico</p><p className="font-black text-nt-text-primary">{profile.diagnostic_completed ? "Completado" : "Pendiente"}</p></div>
          </div>
        </div>
        <div className="mt-3 flex items-center gap-2 text-xs font-bold text-nt-text-secondary">
          <CalendarDays className="size-4 text-nt-blue" />
          Miembro desde {createdAtLabel}
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
                <p className="rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">
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
                  <span className="text-sm font-black text-nt-text-primary">Avatar opcional</span>
                  <select name="avatar_url" value={profile.avatar_url} onChange={handleChange} className="h-12 rounded-[18px] border border-nt-border bg-white px-4 text-sm font-bold outline-none focus:border-nt-blue">
                    <option value="">Usar mis iniciales</option>
                    {hasCustomAvatar && <option value={profile.avatar_url}>Avatar actual</option>}
                    <option value="/assets/avatar-girl.png">Avatar 1</option>
                    <option value="/assets/avatar-boy.png">Avatar 2</option>
                  </select>
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
