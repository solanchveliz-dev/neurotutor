import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, BookOpenCheck, Medal } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import PrimaryButton from "../components/student/PrimaryButton";
import { getStudentDashboard } from "../services/dashboardService";
import { getStudentProfile } from "../services/profileService";
import { getStudentProgress } from "../services/progressService";
import { getStudentAchievements } from "../services/achievementService";
import { getStudentId } from "../utils/auth";
import { getAchievementImage, sortAchievementsByUnlockedAt } from "../utils/achievementVisuals";

const normalizeText = (value) => {
  if (value === null || value === undefined) return "";

  return String(value)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();
};

const clampPercentage = (value) => Math.min(Math.max(Number(value) || 0, 0), 100);

const achievementCardVisuals = {
  FIRST_THEORY_COMPLETED: {
    card: "border-violet-200 bg-gradient-to-br from-violet-50 via-purple-50 to-white",
    glow: "bg-violet-400",
  },
  POINTS_100: {
    card: "border-amber-200 bg-gradient-to-br from-amber-50 via-yellow-50 to-white",
    glow: "bg-amber-400",
  },
  DIAGNOSTIC_COMPLETED: {
    card: "border-sky-200 bg-gradient-to-br from-sky-50 via-cyan-50 to-white",
    glow: "bg-sky-400",
  },
};

const defaultAchievementVisual = {
  card: "border-blue-100 bg-gradient-to-br from-slate-50 via-blue-50 to-white",
  glow: "bg-blue-400",
};

const getModuleLevels = (module) => {
  const rawLevels = module?.levels ?? module?.niveles ?? module?.topics ?? module?.ruta;
  return Array.isArray(rawLevels) ? rawLevels : [];
};

const normalizeLevel = (level) => {
  if (!level) return null;

  const status = level.status ?? level.estado ?? "DISPONIBLE";

  return {
    ...level,
    id: level.id ?? level.levelId,
    name: level.name ?? level.titulo ?? level.nombre ?? "Nivel",
    backendTitle: level.backendTitle ?? level.titulo ?? level.name ?? level.nombre ?? "Contenido del nivel",
    status,
    progress: level.progress ?? level.progreso ?? 0,
    unlocked: level.unlocked ?? status !== "BLOQUEADO",
  };
};

function StudentDashboard() {
  const navigate = useNavigate();
  const [student, setStudent] = useState(null);
  const [modules, setModules] = useState([]);
  const [progressSummary, setProgressSummary] = useState(null);
  const [achievements, setAchievements] = useState([]);
  const [achievementError, setAchievementError] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboard = useCallback(async () => {
    const studentId = getStudentId();

    if (!studentId) {
      setError(new Error("No se encontró la sesión del estudiante."));
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);
    setAchievementError("");

    try {
      const [profile, progress, achievementData] = await Promise.all([
        getStudentProfile(studentId),
        getStudentProgress(studentId),
        getStudentAchievements(studentId).catch(() => {
          setAchievementError("No pudimos cargar tus logros ahora.");
          return null;
        }),
      ]);
      const legacyProfile = await getStudentDashboard(studentId).catch(() => null);
      const [grade = "", section = ""] = String(legacyProfile?.gradoSeccion ?? "").split(" ");

      setProgressSummary(progress);
      setAchievements(
        Array.isArray(achievementData?.unlocked) ? achievementData.unlocked.filter(Boolean) : []
      );

      setStudent({
        name: profile.name,
        grade: profile.grade || grade,
        section: profile.section || section,
        level: profile.level,
        points: profile.points ?? 0,
        gender: profile?.gender,
        avatarUrl: profile?.avatar_url,
      });

      const progressModules = Array.isArray(progress?.modules) ? progress.modules : [];
      const progressByModule = new Map(
        progressModules.map((item) => [String(item?.module_id), item])
      );

      if (Array.isArray(legacyProfile?.modulos) && legacyProfile.modulos.length > 0) {
        setModules(
          legacyProfile.modulos.filter(Boolean).map((module, index) => {
            const moduleProgress = progressByModule.get(String(module.id));
            return {
              id: module.id,
              title: module.titulo,
              description: "Modulo asignado segun tu nivel diagnostico.",
              progress: moduleProgress?.progress_percentage ?? module.ejerciciosCompletados ?? 0,
              total: moduleProgress ? 100 : module.ejerciciosTotales ?? 0,
              unlocked: module.estado !== "BLOQUEADO",
              active: module.estado === "EN_CURSO" || index === 0,
              levels: getModuleLevels(module).map(normalizeLevel).filter(Boolean),
            };
          })
        );
      } else if (Array.isArray(progress?.modules) && progress.modules.length > 0) {
        setModules(
          progress.modules.filter(Boolean).map((module, index) => ({
            id: module.module_id,
            title: module.title,
            description: "Progreso registrado en el servidor.",
            progress: module.progress_percentage ?? 0,
            total: 100,
            unlocked: true,
            active: index === 0,
            levels: [],
          }))
        );
      } else {
        setModules([]);
      }
    } catch (err) {
      console.error("Error fetching dashboard:", err);
      setError(err);
      setStudent(null);
      setModules([]);
      setAchievements([]);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboard();
  }, [fetchDashboard]);

  const getFirstAvailableModule = () =>
    modules.find((module) => module?.unlocked) ?? modules[0] ?? null;

  const getAvailableLevel = (module) => {
    const levels = getModuleLevels(module).map(normalizeLevel).filter(Boolean);
    return levels.find((level) => level?.unlocked) ?? levels[0] ?? null;
  };

  const openModuleFlow = (module) => {
    const selectedRealModule = module?.unlocked ? module : getFirstAvailableModule();

    if (!selectedRealModule?.id) return;

    const availableLevel = getAvailableLevel(selectedRealModule);

    if (availableLevel?.id) {
      navigate(`/module/${selectedRealModule.id}/level/${availableLevel.id}`, {
        state: { module: selectedRealModule, level: availableLevel },
      });
      return;
    }

    navigate(`/module/${selectedRealModule.id}`, { state: { module: selectedRealModule } });
  };

  const handleOpenLearningCard = (module) => {
    if (!module?.id) return;

    const availableLevel = getAvailableLevel(module);

    if (availableLevel?.id) {
      navigate(`/module/${module.id}/level/${availableLevel.id}`, {
        state: { module, level: availableLevel },
      });
      return;
    }

    navigate(`/module/${module.id}`, { state: { module } });
  };

  const overallProgress = clampPercentage(progressSummary?.overall_progress);

  const getModulePercentage = (module) => {
    if (!module?.total) return 0;
    return clampPercentage(Math.round((module.progress / module.total) * 100));
  };

  const getIslandModule = (label, index) => {
    const normalizedLabel = normalizeText(label);
    return (
      modules.find((module) => normalizeText(module?.title).includes(normalizedLabel)) ??
      modules[index] ??
      null
    );
  };

  const islandCards = [
    { label: "Fracciones", image: "/assets/island-fracciones.png", module: getIslandModule("Fracciones", 0) },
    { label: "Decimales", image: "/assets/island-decimales.png", module: getIslandModule("Decimales", 1) },
    { label: "Porcentajes", image: "/assets/island-porcentajes.png", module: getIslandModule("Porcentajes", 2) },
  ];

  const learningCardVisuals = [
    {
      image: "/assets/card-fracciones-basic.png",
      tone: "bg-nt-green",
      gradient: "linear-gradient(135deg, #4A00E0 0%, #8E2DE2 100%)",
      glow: "shadow-violet-950/25",
      buttonTone: "bg-white/95 text-[#4A00E0] hover:bg-white",
      progressTone: "bg-[#D9C5FF]",
    },
    {
      image: "/assets/card-decimales-basic.png",
      tone: "bg-nt-yellow",
      gradient: "linear-gradient(135deg, #2948ff 0%, #396afc 100%)",
      glow: "shadow-blue-950/25",
      buttonTone: "bg-white/95 text-[#2948ff] hover:bg-white",
      progressTone: "bg-[#C7D4FF]",
    },
    {
      image: "/assets/card-porcentajes-basic.png",
      tone: "bg-nt-red",
      gradient: "linear-gradient(135deg, #2C7744 0%, #52c234 100%)",
      glow: "shadow-emerald-950/25",
      buttonTone: "bg-white/95 text-[#2C7744] hover:bg-white",
      progressTone: "bg-[#B9F58C]",
    },
  ];
  const learningCards = modules.slice(0, 3).map((module, index) => ({
    ...learningCardVisuals[index],
    title: module.title,
    subtitle: module.description,
    module,
    percentage: getModulePercentage(module),
  }));

  const progressModules = Array.isArray(progressSummary?.modules) ? progressSummary.modules : [];
  const currentModuleProgress =
    progressModules.find((item) => (item.progress_percentage ?? 0) < 100) ??
    progressModules[0] ??
    null;
  const nextObjective = currentModuleProgress?.progress_percentage >= 100
    ? { title: "¡Módulo completado!", detail: "Tu progreso está registrado al 100%." }
    : !currentModuleProgress?.theory_completed
      ? { title: "Completa la teoría", detail: "Revisa las lecciones del nivel para avanzar." }
      : !currentModuleProgress?.practice_completed
        ? { title: "Aprueba la práctica", detail: "Resuelve los ejercicios y alcanza el puntaje requerido." }
        : !currentModuleProgress?.exam_passed
          ? { title: "Aprueba el examen final", detail: "Demuestra lo aprendido para completar el nivel." }
          : { title: "¡Módulo completado!", detail: "Tu progreso está registrado al 100%." };
  const currentLearningCard = learningCards[0] ?? null;
  const currentLearningTitle = currentModuleProgress?.title ?? currentLearningCard?.title;
  const currentLearningLevel = currentModuleProgress?.level ?? student?.level;
  const currentLearningPercentage =
    clampPercentage(currentModuleProgress?.progress_percentage ?? currentLearningCard?.percentage);
  const recentAchievements = sortAchievementsByUnlockedAt(achievements).slice(0, 3);

  const studentName = typeof student?.name === "string" && student.name.trim()
    ? student.name.trim()
    : "Estudiante";
  const studentGradeSection = [student?.grade, student?.section]
    .filter((value) => typeof value === "string" && value.trim())
    .map((value) => value.trim())
    .join(" - ") || "Grado y sección no registrados";
  const studentLevel = typeof student?.level === "string" && student.level.trim()
    ? student.level.trim()
    : "Pendiente";
  const studentGender = normalizeText(student?.gender).trim();
  const maleGenders = new Set(["masculino", "male", "m", "hombre"]);
  const femaleGenders = new Set(["femenino", "female", "f", "mujer"]);
  const storedAvatar = typeof student?.avatarUrl === "string" ? student.avatarUrl.trim() : "";
  const profileAvatar = storedAvatar
    || (maleGenders.has(studentGender) ? "/assets/avatar-boy.png" : null)
    || (femaleGenders.has(studentGender) ? "/assets/avatar-girl.png" : null);
  const studentInitials = (typeof student?.name === "string" ? student.name : "")
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join("") || "NT";

  const sidebarItems = [
    { label: "Inicio", active: true, onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", onClick: () => openModuleFlow() },
    { label: "Mis Logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="flex min-h-[400px] items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  if (error) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="flex min-h-[400px] flex-col items-center justify-center gap-4">
          <p className="text-nt-red font-semibold">Error al cargar los datos</p>
          <PrimaryButton tone="blue" onClick={fetchDashboard}>
            Reintentar
          </PrimaryButton>
        </div>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      rightPanel={
        <div className="grid gap-4">
          <section className="rounded-nt-card border border-white/85 bg-gradient-to-br from-white via-blue-50/35 to-violet-50/45 p-4 shadow-nt-card">
            <div className="flex items-center gap-4">
              <div className="relative size-20 shrink-0">
                <div className="absolute inset-0 rounded-full bg-gradient-to-br from-nt-blue-light/35 to-nt-purple-light/35 blur-lg" />
                {profileAvatar ? (
                  <img src={profileAvatar} alt="" className="relative size-20 rounded-full border-4 border-white object-cover shadow-md" />
                ) : (
                  <div className="relative grid size-20 place-items-center rounded-full border-4 border-white bg-gradient-to-br from-nt-blue to-nt-purple text-xl font-black text-white shadow-md">
                    {studentInitials}
                  </div>
                )}
              </div>
              <div className="min-w-0 text-left">
                <h2 className="truncate text-lg font-black text-nt-text-primary">{studentName}</h2>
                <p className="mt-0.5 text-xs font-bold text-nt-text-secondary">
                  {studentGradeSection}
                </p>
                <span className="mt-2 inline-flex max-w-full rounded-full border border-violet-200 bg-violet-50 px-3 py-1 text-[11px] font-black text-nt-purple">
                  Nivel {studentLevel}
                </span>
              </div>
            </div>
            <div className="mt-4 border-t border-blue-100 pt-4">
              <h2 className="text-center text-base font-black text-nt-text-primary">Progreso general</h2>
            <div className="mt-3 flex flex-col items-center">
              <div
                className="grid size-28 place-items-center rounded-full shadow-[0_14px_30px_rgba(37,99,235,0.15)]"
                style={{ background: `conic-gradient(#2563EB 0deg, #7C3AED ${overallProgress * 3.6}deg, #DBEAFE ${overallProgress * 3.6}deg 360deg)` }}
              >
                <div className="grid size-20 place-items-center rounded-full bg-white shadow-inner">
                  <span className="text-2xl font-black text-nt-text-primary">{overallProgress}%</span>
                </div>
              </div>
              <p className="mt-2 text-xs font-black text-nt-blue">Avance registrado</p>
            </div>
            </div>
          </section>

          <section className="rounded-nt-card border border-blue-100 bg-gradient-to-br from-nt-sky/90 via-white to-violet-50/70 p-5 shadow-nt-card">
            <div className="flex items-center gap-3">
              <div className="grid size-11 place-items-center rounded-[18px] bg-nt-blue/10 text-nt-blue">
                <BookOpenCheck className="size-5" />
              </div>
              <div>
                <h2 className="text-lg font-black text-nt-text-primary">Cómo avanzas</h2>
                <p className="text-xs font-bold text-nt-text-secondary">Progreso registrado por nivel</p>
              </div>
            </div>
            <div className="mt-4 grid gap-2">
              <div className="flex items-center justify-between rounded-[18px] border border-blue-100 bg-blue-50/85 p-3">
                <span className="flex items-center gap-2 text-sm font-black text-nt-text-primary"><img src="/assets/icon_theory.webp" alt="" className="size-6 object-contain sm:size-8" />Teoría</span>
                <span className="text-sm font-black text-nt-blue">33%</span>
              </div>
              <div className="flex items-center justify-between rounded-[18px] border border-green-100 bg-green-50/85 p-3">
                <span className="flex items-center gap-2 text-sm font-black text-nt-text-primary"><img src="/assets/icon_practice.webp" alt="" className="size-6 object-contain sm:size-8" />Práctica</span>
                <span className="text-sm font-black text-green-700">33%</span>
              </div>
              <div className="flex items-center justify-between rounded-[18px] border border-violet-100 bg-violet-50/85 p-3">
                <span className="flex items-center gap-2 text-sm font-black text-nt-text-primary"><img src="/assets/icon_similar.webp" alt="" className="size-6 object-contain sm:size-8" />Examen final</span>
                <span className="text-sm font-black text-nt-purple">34%</span>
              </div>
            </div>
          </section>
        </div>
      }
    >
      <section className="relative isolate min-h-[500px] overflow-visible" aria-label="Islas principales de aprendizaje">
        <div className="relative z-10 flex min-h-[470px] flex-col gap-0 px-1 py-0 sm:px-3 lg:min-h-[500px] xl:min-h-[520px]">
          <div className="flex items-start justify-between gap-4">
            <div className="max-w-xl px-1 py-2 text-nt-text-primary">
              <h1 className="text-3xl font-black leading-tight text-nt-text-primary drop-shadow-[0_3px_2px_rgba(255,255,255,0.92)] md:text-4xl">
                Hola,{" "}
                <span className="bg-gradient-to-r from-nt-blue to-nt-purple bg-clip-text text-transparent">
                  {studentName}
                </span>
              </h1>
              <p className="mt-1 text-base font-black text-nt-blue drop-shadow-[0_2px_2px_rgba(255,255,255,0.9)] md:text-xl">
                Continúa tu aventura matemática
              </p>
            </div>
            <img src="/assets/neo.png" alt="NEO" className="hidden h-36 w-auto shrink-0 translate-y-2 drop-shadow-[0_18px_30px_rgba(30,58,138,0.25)] md:block lg:h-48 xl:h-56" />
          </div>

          <div className="-mt-14 grid gap-2 md:-mt-20 md:grid-cols-3 md:items-start lg:-mt-32">
            {islandCards.map((island) => (
              <div key={island.label} className="rounded-[34px] bg-transparent p-0 text-left">
                <div className="relative">
                  <button
                    type="button"
                    className="group block w-full rounded-[34px] bg-transparent focus:outline-none focus:ring-4 focus:ring-nt-blue-light/40"
                    onClick={() => openModuleFlow(island.module)}
                  >
                    <img
                      src={island.image}
                      alt={island.label}
                      className="mx-auto h-[304px] w-full object-contain drop-shadow-[0_34px_46px_rgba(30,58,138,0.34)] transition duration-300 group-hover:scale-110 group-hover:drop-shadow-[0_42px_64px_rgba(37,99,235,0.48)] sm:h-[336px] md:h-[336px] lg:h-[400px] 2xl:h-[450px]"
                    />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="space-y-4 rounded-nt-card border border-white/80 bg-white/80 p-5 shadow-nt-card backdrop-blur">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-2.5">
            <div className="grid size-9 place-items-center rounded-[14px] bg-nt-blue/10 text-nt-blue">
              <BookOpenCheck className="size-4.5" />
            </div>
            <h2 className="text-xl font-black text-nt-text-primary">Continúa aprendiendo</h2>
          </div>
          <button
            type="button"
            className="inline-flex items-center gap-1 text-sm font-black text-nt-blue transition hover:text-nt-purple"
            onClick={() => openModuleFlow()}
          >
            Ver todo
            <ArrowRight className="size-4" aria-hidden="true" />
          </button>
        </div>

        <div className="grid gap-4 lg:grid-cols-3">
          <article className="flex min-h-[205px] flex-col rounded-[24px] border border-blue-200/80 bg-gradient-to-br from-blue-50 via-sky-50/85 to-white p-4 shadow-[0_12px_30px_rgba(37,99,235,0.12)]">
            <div className="flex items-start justify-between gap-3">
              <div className="min-w-0">
                <p className="text-xs font-black uppercase text-nt-blue">Módulo actual</p>
                <h3 className="mt-2 line-clamp-2 text-lg font-black leading-6 text-nt-text-primary">
                  {currentLearningTitle || "Sin módulo disponible"}
                </h3>
                {currentLearningLevel && (
                  <p className="mt-1 text-xs font-bold text-nt-text-secondary">Nivel {currentLearningLevel}</p>
                )}
              </div>
              {currentLearningCard?.image && (
                <img src={currentLearningCard.image} alt="" className="size-16 shrink-0 rounded-[18px] object-cover shadow-sm" />
              )}
            </div>
            <div className="mt-auto pt-4">
              <div className="mb-2 flex items-center justify-between text-xs font-black text-nt-text-secondary">
                <span>Progreso</span>
                <span className="text-nt-blue">{currentLearningPercentage}%</span>
              </div>
              <div className="h-2.5 overflow-hidden rounded-full bg-white shadow-inner">
                <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-nt-purple" style={{ width: `${currentLearningPercentage}%` }} />
              </div>
              <button
                type="button"
                className="mt-3 inline-flex h-9 w-full items-center justify-center gap-2 rounded-[14px] bg-gradient-to-r from-nt-blue to-nt-purple text-xs font-black text-white shadow-md shadow-nt-blue/15"
                onClick={() => handleOpenLearningCard(currentLearningCard?.module)}
                disabled={!currentLearningCard?.module}
              >
                Continuar
                <ArrowRight className="size-3.5" />
              </button>
            </div>
          </article>

          <article className="relative min-h-[205px] overflow-hidden rounded-[24px] border border-emerald-200/80 bg-gradient-to-br from-emerald-50 via-cyan-50/85 to-white p-4 shadow-[0_12px_30px_rgba(16,185,129,0.12)]">
            <img
              src="/assets/progreso.png"
              alt=""
              className="pointer-events-none absolute right-2 top-2 size-16 object-contain drop-shadow-[0_10px_16px_rgba(16,185,129,0.2)] sm:size-20 lg:size-24"
            />
            <div className="relative z-10 flex items-center gap-3">
              <div className="pr-16 sm:pr-20 lg:pr-24">
                <h3 className="font-black text-nt-text-primary">Tu progreso hoy</h3>
                <p className="text-xs font-semibold text-nt-text-secondary">Resumen real de tu cuenta</p>
              </div>
            </div>
            <div className="relative z-10 mt-4 grid grid-cols-2 gap-2">
              <div className="rounded-[15px] bg-white/80 p-2.5">
                <p className="text-lg font-black text-nt-blue">{progressSummary?.points ?? student.points ?? 0}</p>
                <p className="text-[10px] font-bold text-nt-text-secondary">Puntos</p>
              </div>
              <div className="rounded-[15px] bg-white/80 p-2.5">
                <p className="text-lg font-black text-emerald-700">{overallProgress}%</p>
                <p className="text-[10px] font-bold text-nt-text-secondary">Progreso general</p>
              </div>
              <div className="rounded-[15px] bg-white/80 p-2.5">
                <p className="text-lg font-black text-nt-purple">{achievements.length}</p>
                <p className="text-[10px] font-bold text-nt-text-secondary">Logros</p>
              </div>
              <div className="rounded-[15px] bg-white/80 p-2.5">
                <p className="text-lg font-black text-nt-text-primary">{progressModules.length}</p>
                <p className="text-[10px] font-bold text-nt-text-secondary">Con actividad</p>
              </div>
            </div>
          </article>

          <article className="relative flex min-h-[205px] flex-col overflow-hidden rounded-[24px] border border-orange-200/80 bg-gradient-to-br from-orange-50 via-amber-50/90 to-white p-4 shadow-[0_12px_30px_rgba(245,158,11,0.13)]">
            <img
              src="/assets/proximo_objetivo.png"
              alt=""
              className="pointer-events-none absolute right-1 top-1 size-[72px] object-contain drop-shadow-[0_10px_18px_rgba(245,158,11,0.22)] sm:size-20 lg:size-[104px]"
            />
            <div className="relative z-10 flex items-start gap-3">
              <div className="min-w-0 pr-[72px] sm:pr-20 lg:pr-[104px]">
                <p className="text-xs font-black uppercase text-orange-600">Próximo objetivo</p>
                <h3 className="mt-2 text-lg font-black leading-6 text-nt-text-primary">{nextObjective.title}</h3>
                <p className="mt-1 text-xs font-semibold leading-5 text-nt-text-secondary">{nextObjective.detail}</p>
              </div>
            </div>
            <button
              type="button"
              className="relative z-10 mt-auto inline-flex h-9 w-full items-center justify-center gap-2 rounded-[14px] bg-gradient-to-r from-amber-400 to-orange-500 text-xs font-black text-white shadow-md shadow-orange-200 transition hover:from-amber-500 hover:to-orange-500"
              onClick={() => openModuleFlow(currentLearningCard?.module)}
              disabled={!currentLearningCard?.module}
            >
              Continuar aprendiendo
              <ArrowRight className="size-3.5" />
            </button>
          </article>
        </div>
      </section>

      <section className="rounded-nt-card border border-white/80 bg-white/85 p-5 shadow-nt-card backdrop-blur">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="grid size-12 place-items-center rounded-[20px] bg-nt-purple/12 text-nt-purple">
              <Medal className="size-6" />
            </div>
            <div>
              <h2 className="text-xl font-black text-nt-text-primary">Mis Logros</h2>
              <p className="text-sm font-semibold text-nt-text-secondary">Insignias obtenidas con tu progreso real.</p>
            </div>
          </div>
          <button
            type="button"
            className="inline-flex items-center gap-2 rounded-nt-button bg-nt-purple px-4 py-2.5 text-sm font-black text-white shadow-lg shadow-nt-purple/20"
            onClick={() => navigate("/achievements")}
          >
            Ver logros
            <ArrowRight className="size-4" />
          </button>
        </div>

        {achievementError ? (
          <p className="mt-4 rounded-[18px] bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">{achievementError}</p>
        ) : achievements.length ? (
          <div className="mt-4 grid gap-3 md:grid-cols-3">
            {recentAchievements.map((achievement) => {
              const visual = achievementCardVisuals[achievement.code] ?? defaultAchievementVisual;
              const image = getAchievementImage(achievement.code);

              return (
                <div
                  key={achievement.id}
                  className={`relative flex flex-col items-center overflow-hidden rounded-[24px] border px-4 py-4 text-center shadow-sm transition duration-300 hover:-translate-y-0.5 hover:shadow-lg ${visual.card}`}
                >
                  <span className={`pointer-events-none absolute top-6 size-20 rounded-full opacity-30 blur-xl sm:size-24 ${visual.glow}`} aria-hidden="true" />
                  {image ? (
                    <img
                      src={image}
                      alt=""
                      className="relative z-10 mx-auto size-[72px] object-contain drop-shadow-[0_14px_20px_rgba(37,99,235,0.24)] sm:size-[88px] lg:size-[104px]"
                    />
                  ) : (
                    <Medal className="relative z-10 mx-auto size-[72px] text-nt-purple drop-shadow-[0_12px_18px_rgba(124,58,237,0.24)] sm:size-[88px] lg:size-[104px]" />
                  )}
                  <h3 className="relative z-10 mt-3 font-black text-nt-text-primary">{achievement.title}</h3>
                  <p className="relative z-10 mt-1 text-xs font-semibold leading-5 text-nt-text-secondary">{achievement.description}</p>
                </div>
              );
            })}
          </div>
        ) : (
          <p className="mt-4 rounded-[20px] bg-nt-sky/60 px-4 py-4 text-sm font-bold text-nt-text-secondary">
            Completa actividades para desbloquear logros.
          </p>
        )}
      </section>

    </StudentLayout>
  );
}

export default StudentDashboard;
