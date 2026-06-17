import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, Bell, LogOut, Search, Star } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import PrimaryButton from "../components/student/PrimaryButton";
import { getStudentDashboard } from "../services/dashboardService";
import { clearAuthData, getStudentId } from "../utils/auth";

const fallbackStudent = {
  name: "Estudiante Demo",
  grade: "6to grado",
  section: "A",
  level: "Intermedio",
  points: 320,
  gender: "female",
};

const fallbackModules = [
  {
    id: 1,
    title: "Problemas de cantidad",
    description: "Operaciones, numeros y situaciones de conteo.",
    progress: 6,
    total: 10,
    unlocked: true,
    active: true,
  },
  {
    id: 2,
    title: "Regularidad, equivalencia y cambio",
    description: "Patrones, secuencias y relaciones.",
    progress: 2,
    total: 10,
    unlocked: true,
    active: false,
  },
  {
    id: 3,
    title: "Forma, movimiento y localizacion",
    description: "Figuras, medidas y ubicacion espacial.",
    progress: 0,
    total: 10,
    unlocked: false,
    active: false,
  },
  {
    id: 4,
    title: "Gestion de datos e incertidumbre",
    description: "Tablas, graficos y probabilidades.",
    progress: 0,
    total: 10,
    unlocked: false,
    active: false,
  },
];

const recentActivitiesData = [
  { title: "Fracciones basicas", detail: "Completaste una practica", image: "/assets/card-fracciones-basic.png", points: "+25 pts" },
  { title: "Decimales", detail: "Repasaste ejercicios guiados", image: "/assets/card-decimales-basic.png", points: "+15 pts" },
];

const upcomingChallengesData = [
  { title: "Resolver 5 fracciones", progress: 60, points: "30 pts" },
  { title: "Practicar decimales", progress: 35, points: "20 pts" },
  { title: "Revisar porcentajes", progress: 15, points: "25 pts" },
];

const badgesData = ["Constancia", "Rapidez", "Precision"];

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
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [password, setPassword] = useState("");
  const [student, setStudent] = useState(fallbackStudent);
  const [modules, setModules] = useState(fallbackModules);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboard = useCallback(async () => {
    const studentId = getStudentId();

    if (!studentId) {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const profile = await getStudentDashboard(studentId);
      const [grade = "", section = ""] = (profile.gradoSeccion || "").split(" ");

      setStudent({
        name: profile.nombreCompleto || fallbackStudent.name,
        grade: grade || fallbackStudent.grade,
        section: section || fallbackStudent.section,
        level: profile.nivelActual || fallbackStudent.level,
        points: profile.puntosTotales ?? fallbackStudent.points,
        gender: profile.gender || profile.genero,
      });

      if (Array.isArray(profile.modulos) && profile.modulos.length > 0) {
        setModules(
          profile.modulos.map((module, index) => ({
            id: module.id,
            title: module.titulo,
            description: "Modulo asignado segun tu nivel diagnostico.",
            progress: module.ejerciciosCompletados ?? 0,
            total: module.ejerciciosTotales ?? 0,
            unlocked: module.estado !== "BLOQUEADO",
            active: module.estado === "EN_CURSO" || index === 0,
            levels: getModuleLevels(module).map(normalizeLevel).filter(Boolean),
          }))
        );
      } else {
        setModules(fallbackModules);
      }
    } catch (err) {
      console.error("Error fetching dashboard:", err);
      setError(err);
      setStudent(fallbackStudent);
      setModules(fallbackModules);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboard();
  }, [fetchDashboard]);

  const handleLogout = () => {
    clearAuthData();
    navigate("/login", { replace: true });
  };

  const handleDeleteAccount = () => {
    if (!password.trim()) return;
    clearAuthData();
    setShowDeleteModal(false);
    navigate("/login", { replace: true });
  };

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

  const handleOpenModule = (module) => {
    if (!module?.unlocked) return;
    navigate(`/module/${module.id}`, { state: { module } });
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

  const totalExercises = modules.reduce((sum, module) => sum + (module.total || 0), 0);
  const completedExercises = modules.reduce((sum, module) => sum + (module.progress || 0), 0);
  const overallProgress = totalExercises ? Math.round((completedExercises / totalExercises) * 100) : 0;

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

  const learningCards = [
    {
      title: "Fracciones - Basico",
      subtitle: "Que es una fraccion?",
      image: "/assets/card-fracciones-basic.png",
      module: modules[0],
      tone: "bg-nt-green",
      gradient: "linear-gradient(135deg, #4A00E0 0%, #8E2DE2 100%)",
      glow: "shadow-violet-950/25",
      buttonTone: "bg-white/95 text-[#4A00E0] hover:bg-white",
      progressTone: "bg-[#D9C5FF]",
    },
    {
      title: "Decimales - Basico",
      subtitle: "Introduccion a los decimales",
      image: "/assets/card-decimales-basic.png",
      module: modules[1],
      tone: "bg-nt-yellow",
      gradient: "linear-gradient(135deg, #2948ff 0%, #396afc 100%)",
      glow: "shadow-blue-950/25",
      buttonTone: "bg-white/95 text-[#2948ff] hover:bg-white",
      progressTone: "bg-[#C7D4FF]",
    },
    {
      title: "Porcentajes - Basico",
      subtitle: "Porcentajes en la vida diaria",
      image: "/assets/card-porcentajes-basic.png",
      module: modules[2],
      tone: "bg-nt-red",
      gradient: "linear-gradient(135deg, #2C7744 0%, #52c234 100%)",
      glow: "shadow-emerald-950/25",
      buttonTone: "bg-white/95 text-[#2C7744] hover:bg-white",
      progressTone: "bg-[#B9F58C]",
    },
  ].map(card => ({
    ...card,
    percentage: getModulePercentage(card.module)
  }));

  const subjectProgress = learningCards.map(card => ({
    label: card.title.split(" - ")[0],
    percentage: card.percentage,
    tone: card.tone,
  }));

  const studentGender = (student.gender || student.genero || "").toLowerCase();
  const avatarSrc = studentGender === "male" || studentGender === "masculino"
    ? "/assets/avatar-boy.png"
    : "/assets/avatar-girl.png";

  const sidebarFooter = (
    <button
      type="button"
      className="flex w-full items-center gap-3 rounded-nt-button px-3 py-3 text-sm font-extrabold text-nt-text-secondary transition hover:bg-nt-sky hover:text-nt-blue"
      onClick={handleLogout}
    >
      <LogOut className="size-5" aria-hidden="true" />
      <span>Cerrar sesion</span>
    </button>
  );

  const sidebarItems = [
    { label: "Inicio", active: true, onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", onClick: () => openModuleFlow() },
    { label: "Mis Logros", onClick: () => navigate("/student-dashboard") },
    { label: "Perfil", onClick: () => setShowDeleteModal(true) },
  ];

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} footer={sidebarFooter} />}>
        <div className="flex min-h-[400px] items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  if (error) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} footer={sidebarFooter} />}>
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
      sidebar={<AppSidebar items={sidebarItems} footer={sidebarFooter} />}
      topbar={
        <header className="flex w-full flex-col gap-3 rounded-[28px] bg-white/35 px-3 py-2 backdrop-blur-sm md:flex-row md:items-center md:justify-between">
          <label className="relative min-w-0 flex-1 md:max-w-lg">
            <Search
              className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
              aria-hidden="true"
            />
            <span className="sr-only">Buscar</span>
            <input
              type="search"
              placeholder="Buscar modulos o retos"
              className="h-12 w-full rounded-nt-button border border-white/80 bg-white/90 pl-11 pr-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />
          </label>

          <div className="flex items-center justify-end gap-2">
            <button
              type="button"
              className="grid size-12 place-items-center rounded-nt-button bg-white/90 text-nt-blue shadow-sm transition hover:bg-nt-blue hover:text-white"
              aria-label="Notificaciones"
            >
              <Bell className="size-5" aria-hidden="true" />
            </button>
            <div className="relative">
              <button
                type="button"
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex h-12 items-center gap-2 rounded-nt-button bg-white/90 p-1.5 pr-3 text-left shadow-sm transition hover:bg-white"
              >
                <img src={avatarSrc} alt="" className="size-9 rounded-full object-cover shadow-sm" />
                <span className="hidden max-w-32 truncate text-sm font-extrabold text-nt-text-primary sm:block">
                  {student.name}
                </span>
              </button>

              {showUserMenu && (
                <div className="absolute right-0 top-[calc(100%+12px)] z-50 w-56 overflow-hidden rounded-[22px] border border-nt-border bg-white shadow-nt-soft">
                  <button
                    type="button"
                    className="w-full px-4 py-3 text-left text-sm font-extrabold text-nt-text-primary transition hover:bg-nt-sky"
                    onClick={handleLogout}
                  >
                    Cerrar sesion
                  </button>
                  <button
                    type="button"
                    className="w-full px-4 py-3 text-left text-sm font-extrabold text-nt-red transition hover:bg-red-50"
                    onClick={() => {
                      setShowDeleteModal(true);
                      setShowUserMenu(false);
                    }}
                  >
                    Eliminar cuenta
                  </button>
                </div>
              )}
            </div>
          </div>
        </header>
      }
      rightPanel={
        <div className="rounded-nt-card border border-white/80 bg-white/95 p-5 shadow-nt-card">
          <div className="text-center">
            <img src={avatarSrc} alt="" className="mx-auto size-24 rounded-full object-cover shadow-nt-card" />
            <h2 className="mt-3 text-xl font-black text-nt-text-primary">{student.name}</h2>
            <p className="text-sm font-extrabold text-nt-text-secondary">
              {student.grade} - Seccion {student.section}
            </p>
            <div className="mt-4 rounded-[22px] bg-nt-sky/70 p-3 text-left">
              <div className="flex items-center justify-between text-sm font-black text-nt-text-primary">
                <span>Nivel {student.level}</span>
                <span>{overallProgress}%</span>
              </div>
              <div className="mt-2 h-2.5 overflow-hidden rounded-full bg-white">
                <div className="h-full rounded-full bg-nt-blue" style={{ width: `${overallProgress}%` }} />
              </div>
            </div>
          </div>

          <div className="mt-5 border-t border-nt-border pt-5">
            <h2 className="text-lg font-black text-nt-text-primary">Progreso</h2>
            <div className="mt-4 grid gap-3">
              {subjectProgress.map((item) => (
                <div key={item.label}>
                  <div className="mb-1 flex items-center justify-between text-sm font-extrabold text-nt-text-primary">
                    <span>{item.label}</span>
                    <span>{item.percentage}%</span>
                  </div>
                  <div className="h-2.5 overflow-hidden rounded-full bg-nt-border">
                    <div className={`h-full rounded-full ${item.tone}`} style={{ width: `${item.percentage}%` }} />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-5 border-t border-nt-border pt-5">
            <h2 className="text-lg font-black text-nt-text-primary">Insignias recientes</h2>
            <div className="mt-4 grid grid-cols-3 gap-2">
              {badgesData.map((badge) => (
                <div key={badge} className="rounded-[18px] bg-nt-sky/70 p-2 text-center">
                  <Star className="mx-auto size-5 fill-nt-yellow text-nt-yellow" />
                  <p className="mt-1 truncate text-[11px] font-black text-nt-text-primary">{badge}</p>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-5 border-t border-nt-border pt-5">
            <h2 className="text-lg font-black text-nt-text-primary">Reto semanal</h2>
            <p className="mt-1 text-sm font-semibold text-nt-text-secondary">Completa 10 ejercicios esta semana</p>
            <div className="mt-4 h-3 overflow-hidden rounded-full bg-nt-border">
              <div className="h-full rounded-full bg-nt-purple" style={{ width: `${Math.min(100, (completedExercises / 10) * 100)}%` }} />
            </div>
            <p className="mt-2 text-xs font-black text-nt-text-secondary">
              {Math.min(completedExercises, 10)}/10 ejercicios completados
            </p>
          </div>
        </div>
      }
    >
      <section className="relative isolate min-h-[560px] overflow-visible" aria-label="Islas principales de aprendizaje">
        <div className="relative z-10 flex min-h-[520px] flex-col gap-1 px-1 py-3 sm:px-3 sm:py-4 lg:min-h-[540px] xl:min-h-[560px]">
          <div className="flex items-start justify-between gap-4">
            <div className="max-w-lg pt-1 text-nt-text-primary drop-shadow-[0_2px_0_rgba(255,255,255,0.75)]">
              <span className="inline-flex rounded-full bg-nt-purple px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-purple/25">
                Panel de aprendizaje
              </span>
              <h1 className="mt-2 text-2xl font-black leading-tight text-nt-text-primary md:text-3xl">
                Hola, {student.name}
              </h1>
              <p className="mt-1 text-base font-black text-nt-blue md:text-lg">
                Continua tu aventura matematica
              </p>
            </div>
            <img src="/assets/neo.png" alt="NEO" className="hidden h-36 w-auto shrink-0 translate-y-2 drop-shadow-[0_18px_30px_rgba(30,58,138,0.25)] md:block lg:h-48 xl:h-56" />
          </div>

          <div className="-mt-10 grid gap-2 md:-mt-16 md:grid-cols-3 md:items-start lg:-mt-24">
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

      <div className="grid gap-4 xl:grid-cols-2">
        <article className="rounded-nt-card border border-white/80 bg-white/85 p-5 shadow-nt-card backdrop-blur">
          <h2 className="text-lg font-black text-nt-text-primary">Actividad reciente</h2>
          <p className="mb-4 text-sm font-semibold text-nt-text-secondary">Ultimo avance registrado</p>
          <div className="grid gap-2">
            {recentActivitiesData.map((activity) => (
              <div key={activity.title} className="flex items-center gap-3 rounded-[18px] bg-white px-3 py-2 shadow-sm">
                <img src={activity.image} alt="" className="size-11 rounded-[14px] object-cover" />
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-black text-nt-text-primary">{activity.title}</p>
                  <p className="truncate text-xs font-semibold text-nt-text-secondary">{activity.detail}</p>
                </div>
                <span className="shrink-0 rounded-full bg-nt-green/15 px-2.5 py-1 text-xs font-black text-green-700">
                  {activity.points}
                </span>
              </div>
            ))}
          </div>
        </article>

        <article className="rounded-nt-card border border-white/80 bg-white/85 p-5 shadow-nt-card backdrop-blur">
          <h2 className="text-lg font-black text-nt-text-primary">Proximos desafios</h2>
          <p className="mb-4 text-sm font-semibold text-nt-text-secondary">Retos sugeridos para hoy</p>
          <div className="grid gap-2">
            {upcomingChallengesData.map((challenge) => (
              <div key={challenge.title} className="rounded-[18px] bg-white px-3 py-2 shadow-sm">
                <div className="flex items-center justify-between gap-3">
                  <span className="text-sm font-extrabold text-nt-text-primary">{challenge.title}</span>
                  <span className="shrink-0 rounded-full bg-nt-purple/10 px-2.5 py-1 text-xs font-black text-nt-purple">
                    {challenge.points}
                  </span>
                </div>
                <div className="mt-2 h-2 overflow-hidden rounded-full bg-nt-border">
                  <div className="h-full rounded-full bg-nt-blue" style={{ width: `${challenge.progress}%` }} />
                </div>
              </div>
            ))}
          </div>
        </article>
      </div>

      {showDeleteModal && (
        <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/55 p-5">
          <div className="w-full max-w-md rounded-nt-card border border-white/80 bg-white p-6 shadow-nt-soft">
            <h2 className="text-2xl font-black text-nt-text-primary">Estas seguro de eliminar tu cuenta?</h2>
            <p className="mt-3 text-sm font-semibold leading-6 text-nt-text-secondary">
              Esta accion eliminara tu acceso al sistema. Ingresa tu contrasena para confirmar.
            </p>
            <input
              type="password"
              placeholder="Ingresa tu contrasena"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-5 h-12 w-full rounded-nt-button border border-nt-border bg-white px-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />
            <div className="mt-5 grid gap-3 sm:grid-cols-2">
              <PrimaryButton tone="blue" className="bg-slate-100 text-nt-text-primary shadow-none hover:bg-slate-200" onClick={() => setShowDeleteModal(false)}>
                Cancelar
              </PrimaryButton>
              <PrimaryButton tone="purple" onClick={handleDeleteAccount}>
                Confirmar eliminacion
              </PrimaryButton>
            </div>
          </div>
        </div>
      )}
    </StudentLayout>
  );
}

export default StudentDashboard;
