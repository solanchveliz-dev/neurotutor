import { ArrowLeft, ArrowRight, CheckCircle2, Lock, Search } from "lucide-react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import NeoCard from "../components/student/NeoCard";
import ProgressCard from "../components/student/ProgressCard";
import { modulesData } from "../data/modulesData";

const numericFallbackMap = {
  1: "fracciones",
  2: "decimales",
  3: "porcentajes",
};

function LevelActivities() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId } = useParams();

  const fallbackId = numericFallbackMap[moduleId] ?? moduleId;
  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(fallbackId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => String(item.id) === String(levelId)) ??
    fallbackModule?.levels.find((item) => item.unlocked) ??
    fallbackModule?.levels[0];

  const module = location.state?.module ?? {
    id: moduleId,
    title: fallbackModule?.title ?? "Modulo",
    description: fallbackModule?.description ?? "Actividades del nivel seleccionado.",
  };
  const level = location.state?.level ?? {
    id: levelId,
    name: fallbackLevel?.name ?? "Nivel",
    backendTitle: fallbackLevel?.name ?? "Contenido del nivel",
    description: fallbackLevel?.description ?? "Completa las actividades para avanzar.",
    progress: fallbackLevel?.progress ?? 0,
    status: fallbackLevel?.status ?? "Disponible",
  };

  const completion = {
    theoryCompleted: false,
    practiceCompleted: false,
  };
  const isExamUnlocked = completion.theoryCompleted && completion.practiceCompleted;

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", active: true, onClick: () => navigate(`/module/${moduleId}`, { state: { module } }) },
    { label: "Mis logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", onClick: () => navigate("/student-dashboard") },
  ];

  const activities = [
    {
      title: "Teoria",
      description: "Lee los conceptos clave del nivel antes de resolver ejercicios.",
      detail: completion.theoryCompleted ? "Completado" : "Disponible",
      image: "/assets/teoria.png",
      tone: "bg-nt-green/15",
      barTone: "bg-nt-green",
      badgeTone: "bg-nt-green/15 text-green-700",
      progress: completion.theoryCompleted ? 100 : 0,
      actionLabel: "Abrir teoria",
      onClick: () => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } }),
    },
    {
      title: "Practica",
      description: "Resuelve ejercicios guiados y recibe ayuda cuando lo necesites.",
      detail: completion.practiceCompleted ? "Completado" : "Disponible",
      image: "/assets/practica.png",
      tone: "bg-nt-blue/10",
      barTone: "bg-nt-blue",
      badgeTone: "bg-nt-blue/10 text-nt-blue",
      progress: completion.practiceCompleted ? 100 : 0,
      actionLabel: "Abrir practica",
      onClick: () => navigate(`/practice/${levelId}`, { state: { module, level } }),
    },
    {
      title: "Examen Final",
      description: "Demuestra lo aprendido cuando hayas completado las actividades previas.",
      detail: isExamUnlocked ? "Disponible" : "Bloqueado",
      image: "/assets/examen-final.png",
      tone: isExamUnlocked ? "bg-nt-purple/10" : "bg-slate-100",
      barTone: isExamUnlocked ? "bg-nt-purple" : "bg-slate-300",
      badgeTone: isExamUnlocked ? "bg-nt-purple/10 text-nt-purple" : "bg-slate-100 text-slate-500",
      progress: isExamUnlocked ? 0 : 0,
      actionLabel: "Ir al examen",
      disabled: !isExamUnlocked,
      onClick: () => navigate(`/final-exam/${levelId}`, { state: { module, level } }),
    },
  ];

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <header className="flex w-full flex-col gap-3 rounded-[28px] bg-white/40 px-3 py-2 backdrop-blur-sm md:flex-row md:items-center md:justify-between">
          <button
            type="button"
            className="inline-flex items-center gap-2 text-sm font-black text-nt-blue transition hover:text-nt-purple"
            onClick={() => navigate(`/module/${moduleId}`, { state: { module } })}
          >
            <ArrowLeft className="size-4" aria-hidden="true" />
            Volver a niveles
          </button>
          <label className="relative min-w-0 flex-1 md:max-w-lg">
            <Search
              className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
              aria-hidden="true"
            />
            <span className="sr-only">Buscar</span>
            <input
              type="search"
              placeholder="Buscar actividad"
              className="h-12 w-full rounded-nt-button border border-white/80 bg-white/90 pl-11 pr-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />
          </label>
        </header>
      }
      rightPanel={
        <div className="space-y-5">
          <ProgressCard
            title="Progreso del nivel"
            subtitle={level.status}
            value={level.progress || 0}
            totalLabel={level.name}
            tone="green"
          />
          <div className="rounded-nt-card border border-white/80 bg-white/95 p-5 shadow-nt-card">
            <h2 className="text-lg font-black text-nt-text-primary">Requisitos</h2>
            <div className="mt-4 grid gap-2 text-sm font-semibold">
              <div className="flex items-center gap-2 rounded-[18px] bg-nt-sky/70 p-3 text-nt-text-primary">
                <CheckCircle2 className="size-4 text-nt-green" />
                Teoria disponible
              </div>
              <div className="flex items-center gap-2 rounded-[18px] bg-nt-sky/70 p-3 text-nt-text-primary">
                <CheckCircle2 className="size-4 text-nt-green" />
                Practica disponible
              </div>
              <div className="flex items-center gap-2 rounded-[18px] bg-slate-100 p-3 text-slate-500">
                <Lock className="size-4" />
                Examen bloqueado
              </div>
            </div>
          </div>
          <NeoCard
            title="NEO te acompana"
            message="Completa teoria y practica para desbloquear el examen final."
            actionLabel="Ver teoria"
            onAction={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}
          />
        </div>
      }
    >
      <section className="overflow-hidden rounded-nt-card border border-white/80 bg-white/90 shadow-nt-card backdrop-blur">
        <div className="grid gap-5 p-5 lg:grid-cols-[minmax(0,1fr)_auto] lg:items-center">
          <div>
            <span className="inline-flex rounded-full bg-nt-purple px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-purple/25">
              {module.title}
            </span>
            <h1 className="mt-4 text-3xl font-black leading-tight text-nt-text-primary md:text-4xl">
              {level.name}
            </h1>
            <p className="mt-2 text-sm font-black text-nt-blue">
              {level.backendTitle}
            </p>
            <p className="mt-3 max-w-2xl text-sm font-semibold leading-6 text-nt-text-secondary">
              Elige una actividad para avanzar en este nivel. El examen final se mantiene bloqueado hasta registrar teoria y practica completadas.
            </p>
          </div>
          <div className="rounded-[28px] bg-nt-sky/80 px-5 py-4 text-center">
            <p className="text-sm font-black text-nt-text-secondary">Avance</p>
            <p className="text-4xl font-black text-nt-blue">{level.progress || 0}%</p>
          </div>
        </div>
      </section>

      <section className="grid gap-4">
        {activities.map((item) => (
          <article
            key={item.title}
            className={`rounded-nt-card border border-white/80 bg-white/90 p-4 shadow-nt-card backdrop-blur transition ${
              item.disabled ? "opacity-75" : "hover:-translate-y-1 hover:shadow-nt-soft"
            }`}
          >
            <div className="grid gap-4 xl:grid-cols-[180px_minmax(0,1fr)_auto] xl:items-center">
              <div className={`overflow-hidden rounded-[24px] ${item.tone} p-3`}>
                <img
                  src={item.image}
                  alt=""
                  className={`h-36 w-full object-contain transition ${item.disabled ? "grayscale" : ""}`}
                />
              </div>

              <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-2">
                  <h2 className="text-2xl font-black text-nt-text-primary">{item.title}</h2>
                  <span className={`rounded-full px-3 py-1 text-xs font-black ${item.badgeTone}`}>
                    {item.detail}
                  </span>
                  {item.disabled && <Lock className="size-5 text-slate-400" />}
                </div>
                <p className="mt-1 text-sm font-black text-nt-blue">
                  {level.name} - {level.backendTitle}
                </p>
                <p className="mt-2 max-w-2xl text-sm font-semibold leading-6 text-nt-text-secondary">
                  {item.description}
                </p>

                <div className="mt-4">
                  <div className="mb-2 flex items-center justify-between text-sm font-black text-nt-text-primary">
                    <span>{item.disabled ? "Requisitos pendientes" : "Actividad disponible"}</span>
                    <span>{item.progress}%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-nt-border">
                    <div
                      className={`h-full rounded-full ${item.barTone}`}
                      style={{ width: `${item.progress}%` }}
                    />
                  </div>
                </div>
              </div>

              <button
                type="button"
                disabled={item.disabled}
                onClick={item.onClick}
                className="inline-flex h-12 items-center justify-center gap-2 rounded-nt-button bg-nt-blue px-5 text-sm font-black text-white shadow-lg shadow-nt-blue/20 transition hover:bg-blue-700 disabled:bg-slate-300 disabled:text-slate-500 disabled:shadow-none"
              >
                {item.disabled ? "Bloqueado" : item.actionLabel}
                {item.disabled ? (
                  <Lock className="size-4" aria-hidden="true" />
                ) : (
                  <ArrowRight className="size-4" aria-hidden="true" />
                )}
              </button>
            </div>
          </article>
        ))}
      </section>

      <section className="grid gap-3 md:grid-cols-3">
        <div className="rounded-[24px] border border-white/80 bg-white/85 p-4 shadow-sm">
          <p className="text-sm font-black text-nt-text-primary">Teoria</p>
          <p className="mt-1 text-xs font-semibold text-nt-text-secondary">
            {completion.theoryCompleted ? "Completada" : "Pendiente de completar"}
          </p>
        </div>
        <div className="rounded-[24px] border border-white/80 bg-white/85 p-4 shadow-sm">
          <p className="text-sm font-black text-nt-text-primary">Practica</p>
          <p className="mt-1 text-xs font-semibold text-nt-text-secondary">
            {completion.practiceCompleted ? "Completada" : "Pendiente de completar"}
          </p>
        </div>
        <div className="rounded-[24px] border border-white/80 bg-white/85 p-4 shadow-sm">
          <p className="text-sm font-black text-nt-text-primary">Examen final</p>
          <p className="mt-1 text-xs font-semibold text-nt-text-secondary">
            {isExamUnlocked ? "Disponible" : "Bloqueado por requisitos"}
          </p>
        </div>
      </section>
    </StudentLayout>
  );
}

export default LevelActivities;
