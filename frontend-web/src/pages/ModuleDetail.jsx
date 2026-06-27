import { ArrowRight, CheckCircle2, Lock, Star } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import NeoCard from "../components/student/NeoCard";
import PrimaryButton from "../components/student/PrimaryButton";
import ProgressCard from "../components/student/ProgressCard";
import BackButton from "../components/student/BackButton";
import { modulesData } from "../data/modulesData";
import { getTheoryLessons, getTopicRuta } from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const moduleAssets = {
  fracciones: "/assets/level-fracciones.png",
  decimales: "/assets/level-decimales.png",
  porcentajes: "/assets/level-porcentajes.png",
};

const moduleNumbers = {
  fracciones: "01",
  decimales: "02",
  porcentajes: "03",
};

const numericFallbackMap = {
  1: "fracciones",
  2: "decimales",
  3: "porcentajes",
};

const levelVisuals = [
  {
    key: "basico",
    label: "Básico",
    icon: "B",
    image: "/assets/level-basico.png",
    description: "Conceptos iniciales y ejercicios guiados.",
  },
  {
    key: "intermedio",
    label: "Intermedio",
    icon: "I",
    image: "/assets/level-intermedio.png",
    description: "Retos con mayor razonamiento y aplicacion.",
  },
  {
    key: "avanzado",
    label: "Avanzado",
    icon: "A",
    image: "/assets/level-avanzado.png",
    description: "Problemas integradores para dominar el tema.",
  },
];

function getLevelVisual(title = "") {
  const normalized = title.toLowerCase();

  if (normalized.includes("intermedio")) return levelVisuals[1];
  if (normalized.includes("avanzado")) return levelVisuals[2];
  return levelVisuals[0];
}

function getLevelVisualByIndex(index, title = "") {
  return levelVisuals[index] ?? getLevelVisual(title);
}

function getProgress(item) {
  if (typeof item.progress === "number") {
    return item.progress > 1 ? Math.round(item.progress) : Math.round(item.progress * 100);
  }

  if (item.ejerciciosTotales > 0) {
    return Math.round((item.ejerciciosCompletados / item.ejerciciosTotales) * 100);
  }

  return 0;
}

function normalizeBackendLevel(item, index) {
  const visual = getLevelVisualByIndex(index, item.titulo);
  const status = item.estado || "DISPONIBLE";

  return {
    id: item.id,
    name: visual.label,
    backendTitle: item.titulo || visual.label,
    description: "Contenido adaptado a tu progreso.",
    status,
    progress: getProgress(item),
    unlocked: status !== "BLOQUEADO",
    icon: visual.icon,
    image: visual.image,
  };
}

function normalizeFallbackLevel(item, index) {
  const visual = getLevelVisualByIndex(index, item.name);

  return {
    id: item.id,
    name: visual.label,
    backendTitle: item.name || visual.label,
    description: item.description || visual.description,
    status: item.status || "Disponible",
    progress: 0,
    unlocked: item.unlocked === true,
    icon: visual.icon,
    image: visual.image,
  };
}

function ModuleDetail() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId } = useParams();
  const [levels, setLevels] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isUsingFallback, setIsUsingFallback] = useState(false);

  const dashboardModule = location.state?.module;
  const fallbackId = numericFallbackMap[moduleId] ?? moduleId;
  const fallbackModule = useMemo(
    () =>
      modulesData.find((item) => String(item.id) === String(fallbackId)) ??
      modulesData[0],
    [fallbackId]
  );

  const module = fallbackModule
    ? {
        ...fallbackModule,
        id: moduleId,
        title: dashboardModule?.title ?? fallbackModule.title,
        description:
          dashboardModule?.description ??
          fallbackModule.description ??
          "Elige un nivel para continuar tu aprendizaje.",
      }
    : null;

  useEffect(() => {
    const studentId = getStudentId();
    const fallbackLevels = (fallbackModule?.levels ?? []).map(normalizeFallbackLevel);

    setIsLoading(true);
    setIsUsingFallback(false);

    if (!studentId) {
      setLevels(fallbackLevels);
      setIsUsingFallback(true);
      setIsLoading(false);
      return;
    }

    getTopicRuta(moduleId, studentId)
      .then(async (data) => {
        if (Array.isArray(data) && data.length > 0) {
          const backendLevels = data.map(normalizeBackendLevel);
          const enrichedLevels = await Promise.all(
            backendLevels.map(async (levelItem) => {
              const [lessonsResult, progressResult] = await Promise.allSettled([
                getTheoryLessons(levelItem.id),
                getModuleProgress(studentId, levelItem.id),
              ]);
              return {
                ...levelItem,
                lessonCount:
                  lessonsResult.status === "fulfilled" && Array.isArray(lessonsResult.value)
                    ? lessonsResult.value.length
                    : 0,
                progress:
                  progressResult.status === "fulfilled"
                    ? progressResult.value?.progress_percentage ?? 0
                    : 0,
              };
            })
          );
          setLevels(enrichedLevels);
          setIsUsingFallback(false);
        } else {
          setLevels(fallbackLevels);
          setIsUsingFallback(true);
        }
      })
      .catch(() => {
        setLevels(fallbackLevels);
        setIsUsingFallback(true);
      })
      .finally(() => setIsLoading(false));
  }, [moduleId, fallbackModule]);

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", active: true, onClick: () => navigate("/student-dashboard") },
    { label: "Mis logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const moduleAsset = moduleAssets[fallbackId] ?? moduleAssets[fallbackModule?.id];
  const moduleNumber = moduleNumbers[fallbackId] ?? "01";
  const unlockedLevels = levels.filter((item) => item.unlocked).length;
  const averageProgress = levels.length
    ? Math.round(levels.reduce((sum, item) => sum + (item.progress || 0), 0) / levels.length)
    : 0;

  const handleOpenLevel = (level) => {
    if (!level?.unlocked || !module) return;

    navigate(`/module/${moduleId}/level/${level.id}`, {
      state: { module, level },
    });
  };

  if (!module) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />} topbar={<div />}>
        <section className="rounded-nt-card border border-white/80 bg-white/90 p-6 text-center shadow-nt-card">
          <h1 className="text-2xl font-black text-nt-text-primary">Modulo no encontrado</h1>
          <PrimaryButton type="button" className="mt-5" onClick={() => navigate("/student-dashboard")}>
            Volver a la ruta
          </PrimaryButton>
        </section>
      </StudentLayout>
    );
  }

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="flex min-h-[400px] items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <BackButton onClick={() => navigate("/student-dashboard")}>
          Volver al inicio
        </BackButton>
      }
      rightPanel={
        <div className="space-y-5">
          <ProgressCard
            title="Progreso del modulo"
            subtitle={`${unlockedLevels}/${levels.length} niveles disponibles`}
            value={averageProgress}
            totalLabel={isUsingFallback ? "Vista temporal" : "Datos del servidor"}
            tone="purple"
          />

          <div className="rounded-nt-card border border-white/80 bg-white/95 p-5 shadow-nt-card">
            <div className="flex items-center gap-3">
              <div className="grid size-12 place-items-center rounded-[20px] bg-nt-yellow/30 text-amber-700">
                <Star className="size-6 fill-nt-yellow text-nt-yellow" />
              </div>
              <div>
                <h2 className="text-lg font-black text-nt-text-primary">Ruta del modulo</h2>
                <p className="text-sm font-semibold text-nt-text-secondary">
                  Selecciona un nivel para ver sus actividades.
                </p>
              </div>
            </div>
            <div className="mt-4 grid gap-2">
              {levels.map((item) => (
                <div
                  key={item.id}
                  className="flex items-start gap-2 rounded-[18px] bg-nt-sky/70 p-3 text-sm font-semibold text-nt-text-primary"
                >
                  {item.unlocked ? (
                    <CheckCircle2 className="mt-0.5 size-4 shrink-0 text-nt-green" />
                  ) : (
                    <Lock className="mt-0.5 size-4 shrink-0 text-slate-400" />
                  )}
                  <span className="min-w-0">
                    <span className="block font-black">{item.name}</span>
                    <span className="block truncate text-xs font-semibold text-nt-text-secondary">
                      {item.backendTitle}
                    </span>
                  </span>
                </div>
              ))}
            </div>
          </div>

          <NeoCard
            title="Tip de NEO"
            message="Elige un nivel disponible. Despues podras revisar teoria, practicar y rendir el examen final."
            actionLabel="Volver al panel"
            onAction={() => navigate("/student-dashboard")}
          />
        </div>
      }
    >
      <section className="overflow-hidden rounded-nt-card border border-white/80 bg-white/90 shadow-nt-card backdrop-blur">
        <div className="grid gap-6 p-5 lg:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)] lg:items-center">
          <div className="rounded-[32px] bg-nt-sky/80 p-4">
            {moduleAsset ? (
              <img
                src={moduleAsset}
                alt=""
                className="mx-auto h-64 w-full object-contain drop-shadow-[0_28px_40px_rgba(30,58,138,0.22)] lg:h-80"
              />
            ) : (
              <div className="grid h-64 place-items-center text-7xl lg:h-80">{module.icon}</div>
            )}
          </div>

          <div>
            <span className="inline-flex rounded-full bg-nt-purple px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-purple/25">
              Modulo {moduleNumber}
            </span>
            <h1 className="mt-4 text-4xl font-black leading-tight text-nt-text-primary">
              Modulo {moduleNumber} - {module.title}
            </h1>
            <p className="mt-3 text-base font-semibold leading-7 text-nt-text-secondary">
              {module.description}
            </p>
            {isUsingFallback && (
              <p className="mt-4 rounded-[18px] bg-nt-yellow/25 px-4 py-3 text-sm font-bold text-amber-700">
                Mostrando niveles temporales mientras se sincroniza con el servidor.
              </p>
            )}
          </div>
        </div>
      </section>

      <section className="rounded-nt-card border border-white/80 bg-white/85 p-5 shadow-nt-card backdrop-blur">
        <div className="mb-6 flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
          <div>
            <span className="inline-flex rounded-full bg-nt-green/15 px-3 py-1 text-xs font-black text-green-700">
              Ruta por niveles
            </span>
            <h2 className="mt-3 text-3xl font-black text-nt-text-primary">
              Niveles del modulo
            </h2>
          </div>
          <p className="max-w-md text-sm font-semibold leading-6 text-nt-text-secondary">
            Avanza de Basico a Avanzado. Los niveles bloqueados se habilitan segun tu progreso.
          </p>
        </div>

        <div className="grid gap-0">
          {levels.map((item, index) => {
            const isLast = index === levels.length - 1;
            const isActive = item.unlocked && item.status === "EN_CURSO";

            return (
              <div
                key={item.id}
                className="grid grid-cols-[96px_minmax(0,1fr)] gap-4 md:grid-cols-[128px_minmax(0,1fr)] md:gap-6"
              >
                <div className="relative flex justify-center">
                  {!isLast && (
                    <div
                      className={`absolute top-24 h-[calc(100%-2rem)] border-l-4 border-dashed ${
                        item.unlocked ? "border-nt-blue/35" : "border-slate-300"
                      }`}
                      aria-hidden="true"
                    />
                  )}
                  <div
                    className={`relative z-10 grid size-20 place-items-center rounded-full border-4 bg-white shadow-nt-card md:size-24 ${
                      item.unlocked ? "border-nt-blue/25" : "border-slate-200 opacity-60 grayscale"
                    }`}
                  >
                    <img
                      src={item.image}
                      alt=""
                      className="size-16 rounded-full object-contain md:size-20"
                    />
                    {!item.unlocked && (
                      <div className="absolute inset-0 grid place-items-center rounded-full bg-white/60">
                        <Lock className="size-6 text-slate-500" />
                      </div>
                    )}
                  </div>
                </div>

                <button
                  type="button"
                  disabled={!item.unlocked}
                  onClick={() => handleOpenLevel(item)}
                  className={`mb-6 min-w-0 rounded-nt-card border p-5 text-left shadow-nt-card backdrop-blur transition focus:outline-none focus:ring-4 focus:ring-nt-blue-light/30 ${
                    item.unlocked
                      ? "border-white/80 bg-white/95 hover:-translate-y-1 hover:shadow-nt-soft"
                      : "cursor-not-allowed border-slate-200 bg-white/65 opacity-75"
                  } ${isActive ? "ring-4 ring-nt-blue-light/30" : ""}`}
                >
                  <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <h3 className="text-2xl font-black text-nt-text-primary">
                          {item.name}
                        </h3>
                        <span
                          className={`rounded-full px-3 py-1 text-xs font-black ${
                            item.unlocked ? "bg-nt-blue/10 text-nt-blue" : "bg-slate-100 text-slate-500"
                          }`}
                        >
                          {item.status}
                        </span>
                        {!item.unlocked && <Lock className="size-5 text-slate-400" />}
                      </div>
                      <p className="mt-1 text-sm font-black text-nt-blue">
                        {item.backendTitle}
                      </p>
                      <p className="mt-3 max-w-2xl text-sm font-semibold leading-6 text-nt-text-secondary">
                        {item.description || "Contenido adaptado a tu progreso."}
                      </p>
                      <p className="mt-2 text-xs font-black text-nt-purple">
                        {item.lessonCount ? `${item.lessonCount} lecciones de teoría` : "Lecciones pendientes de sincronizar"}
                      </p>
                    </div>

                    <div className="shrink-0 rounded-[22px] bg-nt-sky/70 px-4 py-3 text-left lg:min-w-36">
                      <p className="text-xs font-black text-nt-text-secondary">Progreso</p>
                      <p className="text-2xl font-black text-nt-text-primary">{item.progress}%</p>
                    </div>
                  </div>

                  <div className="mt-5">
                    <div className="mb-2 h-3 overflow-hidden rounded-full bg-nt-border">
                      <div
                        className={`h-full rounded-full ${item.unlocked ? "bg-nt-green" : "bg-slate-300"}`}
                        style={{ width: `${item.progress}%` }}
                      />
                    </div>
                    <div className="flex flex-wrap items-center justify-between gap-3">
                      <span className="text-xs font-black text-nt-text-secondary">
                        {item.unlocked ? "Nivel disponible para continuar" : "Completa el nivel anterior para desbloquear"}
                      </span>
                      <span
                        className={`inline-flex items-center gap-2 rounded-nt-button px-4 py-2 text-sm font-black ${
                          item.unlocked ? "bg-nt-blue text-white" : "bg-slate-100 text-slate-500"
                        }`}
                      >
                        {item.unlocked ? "Abrir actividades" : "Nivel bloqueado"}
                        {item.unlocked ? (
                          <ArrowRight className="size-4" aria-hidden="true" />
                        ) : (
                          <Lock className="size-4" aria-hidden="true" />
                        )}
                      </span>
                    </div>
                  </div>
                </button>
              </div>
            );
          })}
        </div>
      </section>
    </StudentLayout>
  );
}

export default ModuleDetail;
