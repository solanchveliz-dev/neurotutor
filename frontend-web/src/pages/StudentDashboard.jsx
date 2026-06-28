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

const normalizeText = (value = "") =>
  value
    .toString()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();

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
      const [grade = "", section = ""] = (legacyProfile?.gradoSeccion || "").split(" ");

      setProgressSummary(progress);
      setAchievements(Array.isArray(achievementData?.unlocked) ? achievementData.unlocked : []);

      setStudent({
        name: profile.name,
        grade: profile.grade || grade,
        section: profile.section || section,
        level: profile.level,
        points: profile.points ?? 0,
        gender: profile?.gender || legacyProfile?.gender || legacyProfile?.genero,
        avatarUrl: profile?.avatar_url,
      });

      const progressByModule = new Map(
        (progress?.modules ?? []).map((item) => [String(item.module_id), item])
      );

      if (Array.isArray(legacyProfile?.modulos) && legacyProfile.modulos.length > 0) {
        setModules(
          legacyProfile.modulos.map((module, index) => {
            const moduleProgress = progressByModule.get(String(module.id));
            return {
              id: module.id,
              title: module.titulo,
              description: "Modulo asignado segun tu nivel diagnostico.",
              progress: moduleProgress?.progress_percentage ?? module.ejerciciosCompletados ?? 0,
              total: module.ejerciciosTotales ?? 0,
              unlocked: module.estado !== "BLOQUEADO",
              active: module.estado === "EN_CURSO" || index === 0,
              levels: getModuleLevels(module).map(normalizeLevel).filter(Boolean),
            };
          })
        );
      } else if (Array.isArray(progress?.modules) && progress.modules.length > 0) {
        setModules(
          progress.modules.map((module, index) => ({
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

  const overallProgress = progressSummary?.overall_progress ?? 0;

  const getModulePercentage = (module) => {
    if (!module?.total) return 0;
    return Math.round((module.progress / module.total) * 100);
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

  const subjectProgress = learningCards.map(card => ({
    label: card.title.split(" - ")[0],
    percentage: card.percentage,
    tone: card.tone,
  }));

  const studentGender = (student?.gender || student?.genero || "").toLowerCase();
  const avatarSrc = studentGender === "male" || studentGender === "masculino"
    ? "/assets/avatar-boy.png"
    : "/assets/avatar-girl.png";
  const profileAvatar = student?.avatarUrl || avatarSrc;

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
          <section className="overflow-hidden rounded-nt-card border border-white/85 bg-white/95 p-5 text-center shadow-nt-card">
            <div className="relative mx-auto size-28">
              <div className="absolute inset-0 rounded-full bg-gradient-to-br from-nt-blue-light/35 to-nt-purple-light/35 blur-xl" />
              <img src={profileAvatar} alt="" className="relative size-28 rounded-full border-4 border-white object-cover shadow-nt-card" />
            </div>
            <h2 className="mt-3 text-xl font-black text-nt-text-primary">{student.name}</h2>
            <p className="text-sm font-extrabold text-nt-text-secondary">
              {student.grade} - Seccion {student.section}
            </p>
            <div className="mt-4 rounded-[24px] border border-white/80 bg-gradient-to-br from-nt-sky/85 to-white p-3 text-left shadow-sm">
              <div className="flex items-center justify-between text-sm font-black text-nt-text-primary">
                <span>Nivel {student.level}</span>
                <span>{overallProgress}%</span>
              </div>
              <div className="mt-2 h-3 overflow-hidden rounded-full bg-white shadow-inner">
                <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-nt-purple" style={{ width: `${overallProgress}%` }} />
              </div>
            </div>
          </section>

          <section className="rounded-nt-card border border-white/85 bg-white/95 p-5 shadow-nt-card">
            <h2 className="text-lg font-black text-nt-text-primary">Progreso</h2>
            <div className="mt-4 grid gap-4">
              {subjectProgress.map((item) => (
                <div key={item.label} className="rounded-[22px] bg-nt-sky/45 p-3">
                  <div className="mb-2 flex items-center justify-between text-sm font-extrabold text-nt-text-primary">
                    <span>{item.label}</span>
                    <span>{item.percentage}%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-white shadow-inner">
                    <div className={`h-full rounded-full ${item.tone}`} style={{ width: `${item.percentage}%` }} />
                  </div>
                </div>
              ))}
            </div>
          </section>

          <section className="rounded-nt-card border border-white/85 bg-white/90 p-5 shadow-nt-card">
            <div className="flex items-center gap-3">
              <div className="grid size-11 place-items-center rounded-[18px] bg-nt-blue/10 text-nt-blue">
                <BookOpenCheck className="size-5" />
              </div>
              <div>
                <h2 className="text-lg font-black text-nt-text-primary">Cómo avanzas</h2>
                <p className="text-xs font-bold text-nt-text-secondary">Progreso registrado por nivel</p>
              </div>
            </div>
            <p className="mt-4 text-sm font-semibold leading-6 text-nt-text-secondary">
              Cada nivel suma 33% por teoría, 33% por práctica y 34% cuando apruebas el examen final.
            </p>
          </section>
        </div>
      }
    >
      <section className="relative isolate min-h-[500px] overflow-visible" aria-label="Islas principales de aprendizaje">
        <div className="relative z-10 flex min-h-[470px] flex-col gap-0 px-1 py-0 sm:px-3 lg:min-h-[500px] xl:min-h-[520px]">
          <div className="flex items-start justify-between gap-4">
            <div className="relative max-w-xl rounded-[32px] border border-white/50 bg-white/25 px-4 py-3 text-nt-text-primary shadow-[0_20px_44px_rgba(37,99,235,0.12)] backdrop-blur-sm">
              <div className="pointer-events-none absolute -left-4 -top-4 size-24 rounded-full bg-white/45 blur-2xl" />
              <h1 className="relative text-3xl font-black leading-tight text-nt-text-primary drop-shadow-[0_3px_0_rgba(255,255,255,0.8)] md:text-4xl">
                Hola,{" "}
                <span className="bg-gradient-to-r from-nt-blue to-nt-purple bg-clip-text text-transparent">
                  {student.name}
                </span>
              </h1>
              <p className="relative mt-1 text-base font-black text-nt-blue drop-shadow-[0_2px_0_rgba(255,255,255,0.75)] md:text-xl">
                Continua tu aventura matematica
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
          <h2 className="text-xl font-black text-nt-text-primary">Continua aprendiendo</h2>
          <button
            type="button"
            className="inline-flex items-center gap-1 text-sm font-black text-nt-blue transition hover:text-nt-purple"
            onClick={() => openModuleFlow()}
          >
            Ver todo
            <ArrowRight className="size-4" aria-hidden="true" />
          </button>
        </div>

        <div className="grid gap-3 xl:grid-cols-3">
          {learningCards.map((card) => (
            <button
              key={card.title}
              type="button"
              className={`group relative min-h-[188px] overflow-hidden rounded-[24px] p-3 text-left shadow-xl ${card.glow} transition hover:-translate-y-1 hover:shadow-nt-soft focus:outline-none focus:ring-4 focus:ring-nt-blue-light/30`}
              style={{ background: card.gradient }}
              onClick={() => handleOpenLearningCard(card.module)}
            >
              <div className="pointer-events-none absolute -right-10 -top-14 size-32 rounded-full bg-white/15 blur-2xl" />
              <div className="pointer-events-none absolute -bottom-16 left-8 size-36 rounded-full bg-white/10 blur-3xl" />

              <div className="relative z-10 flex h-full flex-col gap-3">
                <div className="grid gap-3 sm:grid-cols-[104px_minmax(0,1fr)] sm:items-center xl:grid-cols-1 2xl:grid-cols-[104px_minmax(0,1fr)]">
                  <div className="relative mx-auto grid size-24 shrink-0 place-items-center rounded-[22px] bg-white/18 p-1.5 shadow-[inset_0_1px_0_rgba(255,255,255,0.35),0_14px_26px_rgba(15,23,42,0.2)] backdrop-blur-sm sm:mx-0">
                    <img
                      src={card.image}
                      alt=""
                      className="h-full w-full rounded-[18px] object-cover drop-shadow-[0_12px_18px_rgba(15,23,42,0.22)] transition duration-300 group-hover:scale-105"
                    />
                  </div>

                  <div className="min-w-0 text-white">
                    <h3 className="text-lg font-black leading-tight text-white">
                      {card.title}
                    </h3>
                    <p className="mt-1 text-xs font-bold leading-4 text-white/75">
                      {card.subtitle}
                    </p>
                  </div>
                </div>

                <div className="mt-auto">
                  <div className="mb-1.5 flex items-center justify-between text-[11px] font-black text-white/90">
                    <span>Progreso</span>
                    <span>{card.percentage}%</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-white/25">
                    <div
                      className={`h-full rounded-full ${card.progressTone} shadow-[0_0_10px_rgba(255,255,255,0.45)]`}
                      style={{ width: `${card.percentage}%` }}
                    />
                  </div>

                  <div className="mt-3 flex justify-end">
                    <span className={`inline-flex h-9 items-center justify-center gap-1.5 rounded-nt-button px-4 text-xs font-black shadow-md transition ${card.buttonTone}`}>
                      Continuar
                      <ArrowRight className="size-3.5" aria-hidden="true" />
                    </span>
                  </div>
                </div>
              </div>
            </button>
          ))}
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
            {achievements.slice(0, 3).map((achievement) => (
              <div key={achievement.id} className="rounded-[22px] border border-white bg-gradient-to-br from-white to-nt-sky/70 p-4 shadow-sm">
                <Medal className="size-7 text-nt-purple" />
                <h3 className="mt-3 font-black text-nt-text-primary">{achievement.title}</h3>
                <p className="mt-1 text-xs font-semibold leading-5 text-nt-text-secondary">{achievement.description}</p>
              </div>
            ))}
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
