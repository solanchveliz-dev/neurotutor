import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Award,
  BadgeCheck,
  BookOpenCheck,
  Building2,
  GraduationCap,
  Mail,
  Medal,
  Pencil,
  Save,
  Sparkles,
  Trophy,
  UserRound,
  X,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getStudentProfile, updateStudentProfile } from "../services/profileService";
import { getStudentProgress } from "../services/progressService";
import { getStudentAchievements } from "../services/achievementService";
import { getStudentId } from "../utils/auth";
import { getAchievementImage, sortAchievementsByUnlockedAt } from "../utils/achievementVisuals";
import { getCachedStudentData, setCachedStudentData } from "../utils/studentDataCache";

const emptyProfile = {
  name: "",
  firstName: "",
  lastName: "",
  grade: "",
  section: "",
  avatar_url: "",
  gender: "",
  email: "",
  level: "",
  points: 0,
  diagnostic_completed: false,
};

const normalizeGenderValue = (value = "") => {
  const normalized = String(value).trim().toLowerCase();
  if (["femenino", "female", "f", "mujer"].includes(normalized)) return "femenino";
  if (["masculino", "male", "m", "hombre"].includes(normalized)) return "masculino";
  return "";
};

const splitFullName = (fullName = "") => {
  const parts = String(fullName).trim().split(/\s+/).filter(Boolean);
  if (parts.length <= 1) return { firstName: parts[0] ?? "", lastName: "" };
  const givenNameCount = parts.length >= 4 ? 2 : 1;
  return {
    firstName: parts.slice(0, givenNameCount).join(" "),
    lastName: parts.slice(givenNameCount).join(" "),
  };
};

const clampPercentage = (value) => Math.min(100, Math.max(0, Number(value) || 0));
const inputClass = "h-12 w-full rounded-[18px] border border-slate-200 bg-white/90 px-4 text-sm font-bold text-nt-text-primary shadow-[0_4px_14px_rgba(59,130,246,0.04)] outline-none transition duration-200 hover:border-blue-200 hover:bg-white focus:border-blue-400 focus:bg-white focus:ring-4 focus:ring-blue-100 disabled:cursor-not-allowed disabled:border-slate-200/90 disabled:bg-slate-100/90 disabled:text-slate-700 disabled:shadow-none disabled:opacity-100";

function Profile() {
  const navigate = useNavigate();
  const studentId = getStudentId();
  const cachedUser = getCachedStudentData(studentId, "user");
  const cachedAchievements = getCachedStudentData(studentId, "achievements");
  const cachedName = splitFullName(cachedUser?.name);
  const initialProfile = cachedUser ? {
    ...emptyProfile,
    ...cachedUser,
    firstName: cachedUser.firstName ?? cachedName.firstName,
    lastName: cachedUser.lastName ?? cachedName.lastName,
    avatar_url: cachedUser.avatar_url ?? cachedUser.avatarUrl ?? "",
  } : emptyProfile;
  const [profile, setProfile] = useState(initialProfile);
  const [progress, setProgress] = useState(null);
  const [achievements, setAchievements] = useState(cachedAchievements?.unlocked ?? []);
  const [isLoading, setIsLoading] = useState(!cachedUser);
  const [isSaving, setIsSaving] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editSnapshot, setEditSnapshot] = useState(null);
  const [profileLoaded, setProfileLoaded] = useState(Boolean(cachedUser));
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [progressError, setProgressError] = useState("");

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos" },
    { label: "Mis Logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", active: true, onClick: () => navigate("/profile") },
  ];

  const loadProfile = useCallback(() => {
    if (!studentId) {
      setError("No se encontró el ID del estudiante. Inicia sesión nuevamente.");
      setProfileLoaded(false);
      setIsLoading(false);
      return;
    }

    setIsLoading(!cachedUser);
    setError("");
    setMessage("");

    getStudentProgress(studentId)
      .then((loadedProgress) => {
        setProgress(loadedProgress);
        setProgressError("");
      })
      .catch(() => setProgressError("No disponible temporalmente"));

    Promise.allSettled([
      getStudentProfile(studentId),
      getStudentAchievements(studentId),
    ])
      .then(([profileResult, achievementsResult]) => {
        if (achievementsResult.status === "fulfilled") {
          const achievementData = achievementsResult.value;
          setAchievements(Array.isArray(achievementData?.unlocked)
            ? sortAchievementsByUnlockedAt(achievementData.unlocked).filter(Boolean)
            : []);
          setCachedStudentData(studentId, "achievements", achievementData);
        }

        if (profileResult.status === "fulfilled") {
          const data = profileResult.value;
          const separatedName = splitFullName(data.name);
          const normalizedProfile = {
            name: data.name ?? "",
            firstName: data.first_name ?? separatedName.firstName,
            lastName: data.last_name ?? separatedName.lastName,
            grade: data.grade ?? "",
            section: data.section ?? "",
            avatar_url: data.avatar_url ?? "",
            gender: normalizeGenderValue(data.gender ?? data.genero),
            email: data.email ?? "",
            level: data.level ?? "",
            points: data.points ?? 0,
            diagnostic_completed: Boolean(data.diagnostic_completed),
          };
          setProfile(normalizedProfile);
          setCachedStudentData(studentId, "user", normalizedProfile);
          setIsEditing(false);
          setEditSnapshot(null);
          setProfileLoaded(true);
        } else {
          if (!cachedUser) {
            setProfile(emptyProfile);
            setProfileLoaded(false);
            setError("No se pudo cargar tu perfil desde el servidor.");
          }
        }
      })
      .finally(() => setIsLoading(false));
  }, [studentId]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setProfile((current) => ({
      ...current,
      [name]: value,
      ...(name === "avatar_url" && value === "/assets/avatar-girl.png" ? { gender: "femenino" } : {}),
      ...(name === "avatar_url" && value === "/assets/avatar-boy.png" ? { gender: "masculino" } : {}),
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!studentId || !isEditing) return;

    const fullName = [profile.firstName, profile.lastName].map((value) => value?.trim()).filter(Boolean).join(" ");
    if (!profile.firstName?.trim()) {
      setError("El nombre es obligatorio.");
      return;
    }

    setIsSaving(true);
    setMessage("");
    setError("");

    try {
      const updated = await updateStudentProfile(studentId, {
        name: fullName,
        grade: profile.grade?.trim() ?? "",
        section: profile.section?.trim() ?? "",
        avatar_url: profile.avatar_url?.trim() ?? "",
        gender: profile.gender?.trim() ?? "",
      });
      const separatedName = splitFullName(updated.name ?? fullName);
      setProfile((current) => ({
        ...current,
        ...updated,
        name: updated.name ?? fullName,
        firstName: updated.first_name ?? separatedName.firstName,
        lastName: updated.last_name ?? separatedName.lastName,
        gender: normalizeGenderValue(updated.gender ?? current.gender),
      }));
      setCachedStudentData(studentId, "user", {
        ...profile,
        ...updated,
        name: updated.name ?? fullName,
      });
      setIsEditing(false);
      setEditSnapshot(null);
      setMessage("Perfil actualizado correctamente.");
    } catch (saveError) {
      const status = saveError?.response?.status;
      const path = saveError?.response?.data?.path || "";
      if (status === 404 && path.includes("/profile")) {
        setError("El backend activo no tiene disponible la actualización del perfil.");
      } else if (status === 404) {
        setError("No se encontró el estudiante para actualizar el perfil.");
      } else {
        setError("No se pudo actualizar el perfil. Intenta nuevamente.");
      }
    } finally {
      setIsSaving(false);
    }
  };

  const startEditing = () => {
    setEditSnapshot({ ...profile });
    setMessage("");
    setError("");
    setIsEditing(true);
  };

  const cancelEditing = () => {
    if (editSnapshot) setProfile(editSnapshot);
    setEditSnapshot(null);
    setMessage("");
    setError("");
    setIsEditing(false);
  };

  const progressModules = Array.isArray(progress?.modules) ? progress.modules : [];
  const overallProgress = clampPercentage(progress?.overall_progress);
  const totalPoints = Number(progress?.points ?? profile.points) || 0;
  const lessonsCompleted = progressModules.filter((module) => module.theory_completed).length;
  const examsPassed = progressModules.filter((module) => module.exam_passed).length;
  const fullName = [profile.firstName, profile.lastName].filter(Boolean).join(" ") || profile.name || "Estudiante";
  const initials = fullName.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]?.toUpperCase()).join("") || "NT";
  const normalizedGender = normalizeGenderValue(profile.gender);
  const genderAvatar = normalizedGender === "femenino"
    ? "/assets/avatar-girl.png"
    : normalizedGender === "masculino"
      ? "/assets/avatar-boy.png"
      : "";
  const displayedAvatar = genderAvatar;

  const academicStats = useMemo(() => [
    { label: "Puntos totales", value: totalPoints, image: "/assets/icon_star.webp", imageScale: "scale-90", tone: "green" },
    { label: "Lecciones completadas", value: lessonsCompleted, image: "/assets/leccion_completada.png", imageScale: "scale-150", tone: "blue" },
    { label: "Exámenes aprobados", value: examsPassed, image: "/assets/icon_similar.webp", imageScale: "scale-95", tone: "purple" },
    { label: "Logros desbloqueados", value: achievements.length, image: "/assets/icon_trophy.webp", imageScale: "scale-[1.8]", tone: "orange" },
  ], [achievements.length, examsPassed, lessonsCompleted, totalPoints]);

  const statStyles = {
    green: "border-emerald-100 bg-emerald-50/80 text-emerald-600",
    blue: "border-blue-100 bg-blue-50/80 text-blue-600",
    purple: "border-violet-100 bg-violet-50/80 text-violet-600",
    orange: "border-amber-100 bg-amber-50/80 text-amber-500",
  };

  if (!isLoading && !profileLoaded) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />} topbar={<BackButton onClick={() => navigate("/student-dashboard")}>Volver al inicio</BackButton>}>
        <section className="rounded-nt-card border border-amber-200 bg-white/92 p-8 text-center shadow-nt-card">
          <UserRound className="mx-auto size-12 text-nt-blue" />
          <h1 className="mt-4 text-2xl font-black text-nt-text-primary">No pudimos cargar tu perfil</h1>
          <p className="mt-2 text-sm font-semibold text-nt-text-secondary">{error}</p>
          <Button type="button" className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 font-black text-white" onClick={loadProfile}>Reintentar</Button>
        </section>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={<BackButton onClick={() => navigate("/student-dashboard")}>Volver al inicio</BackButton>}
      contentClassName="2xl:col-span-2"
    >
      <header>
        <h1 className="text-3xl font-black text-nt-text-primary md:text-4xl">Mi perfil</h1>
        <p className="mt-1 text-base font-semibold text-slate-700">Gestiona tu información personal y sigue tu progreso.</p>
      </header>

      <section className="rounded-nt-card border border-white/90 bg-white/95 p-5 shadow-nt-card backdrop-blur md:p-6">
        <div className="grid items-center gap-5 lg:grid-cols-[minmax(280px,1.35fr)_repeat(3,minmax(130px,0.65fr))]">
          <div className="flex min-w-0 items-center gap-4">
            {displayedAvatar ? (
              <img src={displayedAvatar} alt={`Avatar de ${fullName}`} className="size-24 shrink-0 rounded-full border-4 border-sky-100 object-cover shadow-md" />
            ) : (
              <div className="grid size-24 shrink-0 place-items-center rounded-full border-4 border-white bg-gradient-to-br from-nt-blue to-nt-purple text-2xl font-black text-white shadow-md">{initials}</div>
            )}
            <div className="min-w-0">
              <h2 className="truncate text-xl font-black text-nt-text-primary md:text-2xl">{fullName}</h2>
              <p className="mt-1 flex items-center gap-2 truncate text-xs font-semibold text-nt-text-secondary sm:text-sm"><Mail className="size-4 shrink-0 text-nt-blue" />{profile.email || "Correo no registrado"}</p>
              <span className="mt-2 inline-flex items-center gap-1.5 rounded-full bg-emerald-100 px-3 py-1 text-xs font-black text-emerald-700"><BadgeCheck className="size-4" />Estudiante activo</span>
            </div>
          </div>

          <IdentityFact icon={GraduationCap} label="Grado" value={profile.grade || "Sin registrar"} iconClass="from-blue-500 to-cyan-400" />
          <IdentityFact icon={Building2} label="Sección" value={profile.section || "Sin registrar"} iconClass="from-cyan-500 to-sky-400" />
          <IdentityFact icon={Medal} label="Nivel diagnóstico" value={profile.level || "Pendiente"} iconClass="from-violet-600 to-fuchsia-400" />
        </div>
      </section>

      <section className="grid gap-5 xl:grid-cols-[minmax(330px,0.8fr)_minmax(0,1.35fr)]">
        <div className="rounded-nt-card border border-white/90 bg-white/95 p-5 shadow-nt-card">
          <SectionTitle icon={BookOpenCheck} title="Resumen académico" iconClass="text-blue-500" />
          <div className="mt-5 grid grid-cols-2 gap-3 sm:grid-cols-4 xl:grid-cols-2 2xl:grid-cols-4">
            {academicStats.map(({ label, value, icon: Icon, image, imageScale, tone }) => (
              <article key={label} className={`flex min-h-36 flex-col items-center justify-center rounded-[20px] border p-3 text-center ${statStyles[tone]}`}>
                {image ? (
                  <div className="flex size-20 shrink-0 items-center justify-center">
                    <img src={image} alt="" className={`size-14 object-contain drop-shadow-md ${imageScale}`} />
                  </div>
                ) : (
                  <Icon className="size-10 drop-shadow-sm" strokeWidth={2.4} />
                )}
                <strong className="mt-2 text-3xl font-black">{value}</strong>
                <span className="mt-1 text-xs font-bold leading-4 text-nt-text-primary">{label}</span>
              </article>
            ))}
          </div>
        </div>

        <div className="rounded-nt-card border border-white/90 bg-white/95 p-5 shadow-nt-card">
          <SectionTitle icon={Sparkles} title="Progreso general + módulos activos" iconClass="text-violet-500" />
          <div className="mt-5 grid gap-6 lg:grid-cols-[180px_minmax(0,1fr)]">
            <div className="flex flex-col items-center justify-center border-b border-slate-100 pb-6 lg:border-b-0 lg:border-r lg:pb-0 lg:pr-6">
              <p className="mb-3 text-xs font-black text-nt-text-primary">Progreso general</p>
              <div className="relative grid size-36 place-items-center rounded-full" style={{ background: `conic-gradient(#2563eb ${overallProgress * 3.6}deg, #e8eef9 0deg)` }}>
                <div className="grid size-28 place-items-center rounded-full bg-white text-center shadow-inner">
                  <div><strong className="block text-3xl font-black text-nt-text-primary">{overallProgress}%</strong><span className="text-xs font-bold text-nt-text-secondary">Completado</span></div>
                </div>
              </div>
              <p className="mt-3 text-center text-xs font-bold text-nt-text-secondary">¡Sigue avanzando a tu ritmo!</p>
            </div>

            <div className="min-w-0">
              <p className="mb-3 text-xs font-black text-nt-text-primary">Módulos activos</p>
              {progressError ? (
                <p className="rounded-[18px] bg-amber-50 p-4 text-sm font-semibold text-amber-800">{progressError}</p>
              ) : progressModules.length ? (
                <div className="grid gap-4">
                  {progressModules.map((module, index) => {
                    const percentage = clampPercentage(module.progress_percentage);
                    const moduleColors = ["from-blue-600 to-cyan-400", "from-emerald-500 to-teal-400", "from-violet-600 to-fuchsia-400"];
                    return (
                      <article key={module.module_id} className="grid grid-cols-[44px_minmax(0,1fr)_42px] items-center gap-3">
                        <span className={`grid size-11 place-items-center rounded-[14px] bg-gradient-to-br text-white shadow-sm ${moduleColors[index % moduleColors.length]}`}><Award className="size-5" /></span>
                        <div className="min-w-0">
                          <div className="flex items-baseline justify-between gap-2">
                            <h3 className="truncate text-sm font-black text-nt-text-primary">{module.title}</h3>
                            <span className="shrink-0 text-[11px] font-bold text-nt-text-secondary">Nivel {module.level || "—"}</span>
                          </div>
                          <div className="mt-2 h-2.5 overflow-hidden rounded-full bg-slate-200"><div className={`h-full rounded-full bg-gradient-to-r ${moduleColors[index % moduleColors.length]}`} style={{ width: `${percentage}%` }} /></div>
                        </div>
                        <strong className="text-right text-xs font-black text-nt-text-primary">{percentage}%</strong>
                      </article>
                    );
                  })}
                </div>
              ) : (
                <p className="rounded-[18px] bg-slate-50 p-4 text-sm font-semibold text-nt-text-secondary">Aún no hay progreso por módulo disponible.</p>
              )}
            </div>
          </div>
        </div>
      </section>

      <Card className="rounded-[28px] border border-blue-200/90 bg-gradient-to-br from-white/95 via-blue-50/90 to-indigo-50/95 p-0 shadow-[0_18px_45px_rgba(59,130,246,0.14)] backdrop-blur-sm">
        <CardContent className="p-5 md:p-6">
          {isLoading ? (
            <div className="flex min-h-64 items-center justify-center"><div className="size-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" /></div>
          ) : (
            <form className="grid gap-5" onSubmit={handleSubmit}>
              <div className="flex flex-wrap items-center justify-between gap-3 border-b border-blue-200/80 pb-4">
                <SectionTitle icon={UserRound} title="Información personal" iconClass="text-violet-500" />
                {!isEditing && (
                  <Button type="button" variant="outline" onClick={startEditing} className="h-11 rounded-[16px] border-blue-200 bg-white/80 px-5 font-black text-nt-blue shadow-sm hover:border-blue-300 hover:bg-blue-50">
                    <Pencil className="size-4" />Editar perfil
                  </Button>
                )}
              </div>
              {error && <p className="rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">{error}</p>}
              {message && <p className="rounded-[18px] border border-green-200 bg-green-50 px-4 py-3 text-sm font-bold text-green-700">{message}</p>}

              <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                <ProfileField label="Nombres"><input name="firstName" value={profile.firstName} onChange={handleChange} disabled={!isEditing} className={inputClass} /></ProfileField>
                <ProfileField label="Apellidos"><input name="lastName" value={profile.lastName} onChange={handleChange} disabled={!isEditing} className={inputClass} /></ProfileField>
                <ProfileField label="Correo electrónico" className="md:col-span-2 xl:col-span-1">
                  <input value={profile.email || ""} readOnly className={`${inputClass} cursor-not-allowed bg-slate-100/90 text-slate-700`} />
                  <span className="text-xs font-semibold leading-4 text-slate-500">Tu correo está vinculado a tu cuenta y no puede editarse aquí.</span>
                </ProfileField>
                <ProfileField label="Grado"><input name="grade" value={profile.grade} onChange={handleChange} disabled={!isEditing} className={inputClass} /></ProfileField>
                <ProfileField label="Sección"><input name="section" value={profile.section} onChange={handleChange} disabled={!isEditing} className={inputClass} /></ProfileField>
                <ProfileField label="Género">
                  <select name="gender" value={profile.gender} onChange={handleChange} disabled={!isEditing} className={inputClass}>
                    <option value="">Sin especificar</option><option value="femenino">Femenino</option><option value="masculino">Masculino</option>
                  </select>
                </ProfileField>
                <ProfileField label="Avatar" className="md:col-span-2 xl:col-span-3">
                  <select name="avatar_url" value={profile.avatar_url} onChange={handleChange} disabled={!isEditing} className={inputClass}>
                    <option value="">Usar avatar según género o iniciales</option>
                    {profile.avatar_url && !["/assets/avatar-girl.png", "/assets/avatar-boy.png"].includes(profile.avatar_url) && <option value={profile.avatar_url}>Avatar actual</option>}
                    <option value="/assets/avatar-girl.png">Avatar femenino</option><option value="/assets/avatar-boy.png">Avatar masculino</option>
                  </select>
                </ProfileField>
              </div>

              {isEditing && (
                <div className="flex flex-wrap justify-end gap-3 border-t border-blue-100/80 pt-4">
                  <Button type="button" variant="outline" disabled={isSaving} onClick={cancelEditing} className="h-12 rounded-[18px] border-slate-200 bg-white/80 px-6 text-sm font-black text-slate-600 hover:bg-slate-50"><X className="size-4" />Cancelar</Button>
                  <Button type="submit" disabled={isSaving} className="h-12 min-w-48 rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple px-6 text-sm font-black text-white shadow-lg shadow-blue-200"><Save className="size-4" />{isSaving ? "Guardando..." : "Guardar cambios"}</Button>
                </div>
              )}
            </form>
          )}
        </CardContent>
      </Card>

      <section className="rounded-nt-card border border-white/90 bg-white/95 p-5 shadow-nt-card">
        <div className="flex items-center justify-between gap-3">
          <SectionTitle icon={Trophy} title="Logros recientes" iconClass="text-amber-500" />
          <Button type="button" variant="ghost" className="rounded-[16px] font-black text-nt-blue" onClick={() => navigate("/achievements")}>Ver todos</Button>
        </div>
        {achievements.length ? (
          <div className="mt-4 grid gap-3 sm:grid-cols-3">
            {achievements.slice(0, 3).map((achievement) => {
              const image = getAchievementImage(achievement);
              return <article key={achievement.id} className="flex items-center gap-3 rounded-[20px] border border-sky-100 bg-gradient-to-br from-white to-sky-50 p-2.5">{image ? <img src={image} alt="" className="size-40 shrink-0 object-contain drop-shadow-lg" /> : <Trophy className="size-16 shrink-0 text-nt-purple drop-shadow-md" />}<div className="min-w-0"><h3 className="truncate text-sm font-black text-nt-text-primary">{achievement.title}</h3><p className="mt-1 line-clamp-2 text-xs font-semibold text-nt-text-secondary">{achievement.description}</p></div></article>;
            })}
          </div>
        ) : <p className="mt-4 rounded-[20px] bg-slate-50 p-4 text-sm font-semibold text-nt-text-secondary">Aún no hay logros desbloqueados.</p>}
      </section>
    </StudentLayout>
  );
}

function IdentityFact({ icon: Icon, label, value, iconClass }) {
  return (
    <div className="flex items-center gap-3 border-t border-slate-100 pt-4 lg:border-l lg:border-t-0 lg:pl-5 lg:pt-0">
      <span className={`grid size-11 shrink-0 place-items-center rounded-[15px] bg-gradient-to-br text-white shadow-sm ${iconClass}`}><Icon className="size-6" /></span>
      <div className="min-w-0"><p className="text-xs font-bold text-nt-text-secondary">{label}</p><p className="truncate text-sm font-black text-nt-text-primary">{value}</p></div>
    </div>
  );
}

function SectionTitle({ icon: Icon, title, iconClass }) {
  return <div className="flex items-center gap-2"><Icon className={`size-5 ${iconClass}`} /><h2 className="text-lg font-black text-nt-text-primary">{title}</h2></div>;
}

function ProfileField({ label, className = "", children }) {
  return <label className={`grid content-start gap-2 ${className}`}><span className="text-sm font-black text-nt-text-primary">{label}</span>{children}</label>;
}

export default Profile;
