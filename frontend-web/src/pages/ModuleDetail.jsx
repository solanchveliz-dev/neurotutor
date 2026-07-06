import { ArrowRight, BookOpen, CheckCircle2, Lock, Play } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import PrimaryButton from "../components/student/PrimaryButton";
import BackButton from "../components/student/BackButton";
import { getModuleDetails, getTopicRuta } from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const moduleAssets = {
  fracciones: "/assets/level-fracciones.png",
  decimales: "/assets/level-decimales.png",
  porcentajes: "/assets/level-porcentajes.png",
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

function normalizeBackendLevel(metadata, routeItem, progress, index) {
  const visual = getLevelVisualByIndex(index, metadata.level);
  const status = routeItem.estado;

  return {
    id: metadata.id,
    name: visual.label,
    backendTitle: metadata.title,
    description: metadata.description,
    status,
    progress: progress?.progress_percentage ?? getProgress(routeItem),
    unlocked: status !== "BLOQUEADO",
    icon: visual.icon,
    image: visual.image,
    lessonCount: metadata.lessons_count ?? 0,
  };
}

function getModuleKey(title = "") {
  const normalized = title.toLowerCase();
  if (normalized.includes("decimal")) return "decimales";
  if (normalized.includes("porcentaje")) return "porcentajes";
  if (normalized.includes("fraccion") || normalized.includes("fracción")) return "fracciones";
  return null;
}

const statusLabels = {
  COMPLETADO: "Completado",
  EN_CURSO: "En curso",
  DISPONIBLE: "Disponible",
  BLOQUEADO: "Bloqueado",
};

function getLevelState(level) {
  if (!level.unlocked || level.status === "BLOQUEADO") return "locked";
  if (level.status === "COMPLETADO" || level.progress >= 100) return "completed";
  if (level.status === "EN_CURSO" || level.progress > 0) return "active";
  return "available";
}

function getLevelButtonStyle(index, state) {
  if (state === "locked") return "bg-slate-200 text-slate-500";
  if (index === 0) return "bg-gradient-to-r from-emerald-500 to-green-500 text-white hover:from-emerald-600 hover:to-green-600";
  if (index === 1) return "bg-gradient-to-r from-sky-500 to-blue-600 text-white hover:from-sky-600 hover:to-blue-700";
  return "bg-gradient-to-r from-violet-500 to-nt-purple text-white hover:from-violet-600 hover:to-purple-700";
}

function getLevelProgressStyle(index, state) {
  if (state === "locked") return "bg-gradient-to-r from-slate-300 to-violet-300";
  if (index === 0) return "bg-gradient-to-r from-emerald-400 to-emerald-600";
  if (index === 1) return "bg-gradient-to-r from-sky-500 to-blue-600";
  return "bg-gradient-to-r from-violet-400 to-purple-600";
}

const levelStateStyles = {
  completed: {
    card: "border-emerald-300/80 bg-gradient-to-br from-emerald-50/95 to-white",
    badge: "bg-emerald-100 text-emerald-700",
    line: "border-emerald-400 drop-shadow-[0_0_6px_rgba(52,211,153,0.65)]",
  },
  active: {
    card: "border-nt-blue-light/60 bg-gradient-to-br from-blue-50 via-white to-violet-50/70 ring-4 ring-nt-blue-light/15",
    badge: "bg-blue-100 text-nt-blue",
    line: "border-blue-500 drop-shadow-[0_0_6px_rgba(59,130,246,0.6)]",
  },
  available: {
    card: "border-sky-200 bg-gradient-to-br from-white to-sky-50/90",
    badge: "bg-sky-100 text-sky-700",
    line: "border-sky-400 drop-shadow-[0_0_5px_rgba(56,189,248,0.55)]",
  },
  locked: {
    card: "border-slate-200 bg-gradient-to-br from-slate-50/95 to-violet-50/70 opacity-80",
    badge: "bg-slate-200/80 text-slate-600",
    line: "border-slate-300 drop-shadow-[0_0_4px_rgba(148,163,184,0.4)]",
  },
};

function ModuleDetail() {
  const navigate = useNavigate();
  const { moduleId } = useParams();
  const [module, setModule] = useState(null);
  const [levels, setLevels] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [neoImageFailed, setNeoImageFailed] = useState(false);

  useEffect(() => {
    const studentId = getStudentId();

    setIsLoading(true);
    setLoadError("");

    if (!studentId) {
      setModule(null);
      setLevels([]);
      setLoadError("No pudimos identificar al estudiante conectado.");
      setIsLoading(false);
      return;
    }

    Promise.all([getModuleDetails(moduleId), getTopicRuta(moduleId, studentId)])
      .then(async ([moduleData, routeData]) => {
        if (!Array.isArray(moduleData?.levels) || !Array.isArray(routeData)) {
          throw new Error("Metadata incompleta");
        }

        const routeById = new Map(routeData.map((item) => [String(item.id), item]));
        const progressResults = await Promise.allSettled(
          moduleData.levels.map((item) => getModuleProgress(studentId, item.id))
        );
        const normalizedLevels = moduleData.levels.map((metadata, index) => {
          const routeItem = routeById.get(String(metadata.id));
          if (!routeItem) throw new Error("Ruta académica incompleta");
          const progressResult = progressResults[index];
          const progress = progressResult.status === "fulfilled" ? progressResult.value : null;
          return normalizeBackendLevel(metadata, routeItem, progress, index);
        });

        setModule(moduleData);
        setLevels(normalizedLevels);
      })
      .catch(() => {
        setModule(null);
        setLevels([]);
        setLoadError("No pudimos cargar la información real del módulo.");
      })
      .finally(() => setIsLoading(false));
  }, [moduleId]);

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", active: true },
    { label: "Mis logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const moduleKey = getModuleKey(module?.title);
  const moduleAsset = moduleAssets[moduleKey];
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

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="flex min-h-[400px] items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  if (!module) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />} topbar={<div />}>
        <section className="rounded-nt-card border border-white/80 bg-white/90 p-6 text-center shadow-nt-card">
          <h1 className="text-2xl font-black text-nt-text-primary">Módulo no disponible</h1>
          <p className="mt-3 text-sm font-semibold text-nt-text-secondary">{loadError}</p>
          <PrimaryButton type="button" className="mt-5" onClick={() => navigate("/student-dashboard")}>
            Volver a la ruta
          </PrimaryButton>
        </section>
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
          <div className="rounded-nt-card border border-white/80 bg-white/95 p-5 text-center shadow-nt-card">
            <div className="flex items-center justify-center gap-2">
              <div className="grid size-[60px] place-items-center rounded-[22px] bg-violet-100 text-nt-purple">
                <img
                  src="/assets/avanzando.png"
                  alt=""
                  className="size-14 object-contain"
                />
              </div>
              <h2 className="text-lg font-black text-nt-text-primary">Progreso del módulo</h2>
            </div>
            <div className="mt-5 flex flex-col items-center">
              <div
                className="relative grid size-36 place-items-center rounded-full"
                style={{ background: `conic-gradient(#7C3AED ${averageProgress * 3.6}deg, #E9D5FF 0deg)` }}
                role="img"
                aria-label={`${averageProgress}% de progreso`}
              >
                <div className="grid size-28 place-items-center rounded-full bg-white shadow-inner">
                  <div>
                    <span className="block text-3xl font-black text-nt-text-primary">{averageProgress}%</span>
                    <span className="mt-0.5 block text-[11px] font-bold text-nt-text-secondary">Completado</span>
                  </div>
                </div>
              </div>
              <div className="mt-5 w-full rounded-[18px] border border-violet-100 bg-gradient-to-r from-slate-50 to-violet-50 px-4 py-3">
                <p className="text-xl font-black text-nt-text-primary">{unlockedLevels} / {levels.length}</p>
                <p className="mt-0.5 text-xs font-bold text-nt-text-secondary">Niveles desbloqueados</p>
              </div>
            </div>
          </div>

          <div className="relative min-h-[148px] overflow-hidden rounded-3xl border border-violet-200 bg-gradient-to-br from-violet-100 via-indigo-50 to-sky-100 p-5 shadow-[0_16px_38px_rgba(99,102,241,0.18)] sm:min-h-40">
            <span className="absolute left-4 top-3 text-sm text-violet-400/70" aria-hidden="true">✦</span>
            <span className="absolute right-5 top-4 text-sm text-amber-400/80" aria-hidden="true">✦</span>
            <span className="absolute bottom-5 left-8 size-1.5 rounded-full bg-cyan-300/70" aria-hidden="true" />
            <div className="relative z-10 max-w-[55%] pt-3">
                <h2 className="text-xl font-black leading-tight text-nt-text-primary">Consejo de NEO</h2>
                <p className="mt-2 text-sm font-semibold leading-5 text-slate-700">
                  Completa teoría, práctica y examen para avanzar con seguridad.
                </p>
            </div>
            {neoImageFailed ? (
              <div className="absolute right-4 top-1/2 grid size-24 -translate-y-1/2 place-items-center rounded-full bg-white/70 text-2xl font-black text-nt-purple sm:size-28 lg:size-32" aria-label="NEO">
                NEO
              </div>
            ) : (
              <img
                src="/assets/neo_pensando.png"
                alt="NEO pensando"
                className="absolute right-3 top-1/2 h-24 w-24 -translate-y-1/2 object-contain sm:right-4 sm:h-28 sm:w-28 lg:right-5 lg:h-[136px] lg:w-[136px]"
                onError={() => setNeoImageFailed(true)}
              />
            )}
          </div>
        </div>
      }
    >
      <section className="relative -mt-4 overflow-hidden md:-mt-7 lg:-mt-12">
        <div className="relative grid gap-0 px-1 py-0 sm:px-3 lg:grid-cols-[minmax(0,1fr)_minmax(360px,1fr)] lg:items-start lg:gap-3">
          <div className="order-2 lg:order-2">
            {moduleAsset ? (
              <img
                src={moduleAsset}
                alt=""
                className="mx-auto h-64 w-full object-contain drop-shadow-[0_22px_28px_rgba(30,58,138,0.24)] md:h-80 lg:h-[420px]"
              />
            ) : (
              <div className="grid h-64 place-items-center text-7xl font-black text-nt-blue md:h-80 lg:h-[420px]">
                {module.title?.charAt(0)}
              </div>
            )}
          </div>

          <div className="order-1 lg:order-1 lg:pt-28">
            <h1 className="text-3xl font-black tracking-tight text-nt-text-primary sm:text-4xl">
              <span className="font-bold">Módulo:</span> {module.title}
            </h1>
            {module.description && (
              <p className="mt-3 max-w-xl text-sm font-bold leading-6 text-slate-800 sm:text-base">
                {module.description}
              </p>
            )}
          </div>
        </div>
      </section>

      <section className="!mt-12 rounded-3xl border border-white/80 bg-white/85 p-3 shadow-[0_18px_45px_rgba(30,58,138,0.12)] backdrop-blur-sm sm:p-4 lg:!mt-16">
        <div className="mb-4 px-1 pt-1">
          <div>
            <h2 className="text-2xl font-black tracking-tight text-nt-text-primary sm:text-3xl">
              Ruta por niveles
            </h2>
          </div>
        </div>

        <div className="grid gap-0">
          {levels.map((item, index) => {
            const isLast = index === levels.length - 1;
            const state = getLevelState(item);
            const styles = levelStateStyles[state];
            const buttonStyle = getLevelButtonStyle(index, state);
            const progressStyle = getLevelProgressStyle(index, state);
            const statusLabel = statusLabels[item.status] ?? statusLabels[state === "locked" ? "BLOQUEADO" : "DISPONIBLE"];

            return (
              <div
                key={item.id}
                className="grid grid-cols-[112px_minmax(0,1fr)] gap-2 md:grid-cols-[144px_minmax(0,1fr)] md:gap-3"
              >
                <div className="relative flex items-center justify-center pb-4">
                  {!isLast && (
                    <div
                      className={`absolute left-1/2 top-1/2 -bottom-8 -translate-x-1/2 border-l-4 border-dashed opacity-95 ${styles.line}`}
                      aria-hidden="true"
                    />
                  )}
                  <div
                    className={`relative z-10 grid size-24 place-items-center rounded-full border-4 bg-white shadow-lg md:size-[120px] ${styles.line} ${state === "locked" ? "grayscale" : ""}`}
                  >
                    <img
                      src={item.image}
                      alt=""
                      className="size-[84px] scale-105 rounded-full object-contain md:size-[108px]"
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
                  className={`mb-4 min-w-0 rounded-nt-card border p-4 text-left shadow-nt-card backdrop-blur transition focus:outline-none focus:ring-4 focus:ring-nt-blue-light/30 ${styles.card} ${item.unlocked ? "hover:-translate-y-1 hover:shadow-nt-soft" : "cursor-not-allowed"}`}
                >
                  <div className="grid min-w-0 gap-4 lg:grid-cols-[minmax(0,1fr)_auto] lg:items-center lg:gap-5">
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <h3 className="text-2xl font-black text-nt-text-primary">
                          {item.name}
                        </h3>
                        <span
                          className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-black ${styles.badge}`}
                        >
                          {state === "completed" && <CheckCircle2 className="size-3.5" aria-hidden="true" />}
                          {state === "locked" && <Lock className="size-3.5" aria-hidden="true" />}
                          {statusLabel}
                        </span>
                      </div>
                      <p className="mt-1 text-sm font-black text-nt-blue">
                        {item.backendTitle}
                      </p>
                      <p className="mt-2 max-w-2xl text-sm font-semibold leading-5 text-nt-text-secondary">
                        {item.description}
                      </p>
                      <p className="mt-2 inline-flex items-center gap-2 rounded-full bg-white/70 px-3 py-1.5 text-xs font-black text-nt-purple">
                        <BookOpen className="size-4" aria-hidden="true" />
                        {item.lessonCount} lecciones de teoría
                      </p>
                      <div className="mt-3 h-2.5 overflow-hidden rounded-full bg-nt-border">
                      <div
                        className={`h-full rounded-full transition-[width] duration-500 ${progressStyle}`}
                        style={{ width: `${item.progress}%` }}
                      />
                    </div>
                    </div>
                    <span
                      className={`inline-flex w-fit shrink-0 items-center justify-center gap-2 rounded-nt-button px-4 py-2.5 text-sm font-black transition-colors lg:min-w-36 ${buttonStyle}`}
                    >
                      {state === "active" ? "Continuar" : item.unlocked ? "Abrir actividades" : "Nivel bloqueado"}
                      {item.unlocked ? (
                        state === "active" ? <Play className="size-4 fill-current" aria-hidden="true" /> : <ArrowRight className="size-4" aria-hidden="true" />
                      ) : (
                        <Lock className="size-4" aria-hidden="true" />
                      )}
                    </span>
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
