import { ArrowRight, BookOpen, CheckCircle2, Clock3, FileQuestion, Gauge, Lock, Percent, Target, Trophy } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import { getLevelDetails, getModuleDetails } from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const levelLabels = {
  BASICO: "Básico",
  INTERMEDIO: "Intermedio",
  AVANZADO: "Avanzado",
};

const levelHeroConfig = {
  BASICO: {
    image: "/assets/logro_basico.png", difficulty: "Fácil", emoji: "🌱", position: "Nivel 1 de 3",
    description: "Aprende las bases de las fracciones: qué representan, cómo se leen y cómo se usan en situaciones cotidianas.",
    heroTone: "from-white via-emerald-50 to-blue-50", borderTone: "border-emerald-100",
    imageGlow: "from-emerald-200/70 via-cyan-100/60 to-blue-100/50",
    labelTone: "bg-emerald-100 text-emerald-700", badgeTone: "border-emerald-100 bg-white/75", accentTone: "text-emerald-700",
  },
  INTERMEDIO: {
    image: "/assets/logro_intermedio.png", difficulty: "Media", emoji: "💎", position: "Nivel 2 de 3",
    description: "Conecta conceptos y estrategias para resolver fracciones con mayor seguridad y precisión.",
    heroTone: "from-white via-cyan-50 to-blue-50", borderTone: "border-cyan-100",
    imageGlow: "from-cyan-200/70 via-sky-100/60 to-blue-100/50",
    labelTone: "bg-cyan-100 text-cyan-700", badgeTone: "border-cyan-100 bg-white/75", accentTone: "text-cyan-700",
  },
  AVANZADO: {
    image: "/assets/logro_avanzado.png", difficulty: "Alta", emoji: "🚀", position: "Nivel 3 de 3",
    description: "Supera retos avanzados y aplica todo lo aprendido para dominar las fracciones.",
    heroTone: "from-white via-violet-50 to-blue-50", borderTone: "border-violet-100",
    imageGlow: "from-violet-200/70 via-fuchsia-100/50 to-blue-100/50",
    labelTone: "bg-violet-100 text-violet-700", badgeTone: "border-violet-100 bg-white/75", accentTone: "text-violet-700",
  },
};

const availableMetric = (label, value, icon) =>
  value === null || value === undefined || value === "" ? null : { label, value, icon };

const percentageMetric = (label, value, icon = Percent) =>
  availableMetric(label, value === null || value === undefined ? null : `${value}%`, icon);

const getLevelButtonTone = (levelKey) => {
  const normalized = String(levelKey ?? "").trim().toUpperCase();
  if (["INTERMEDIO", "INTERMEDIATE"].includes(normalized)) return "bg-blue-500 hover:bg-blue-600 shadow-blue-200";
  if (["AVANZADO", "ADVANCED"].includes(normalized)) return "bg-violet-500 hover:bg-violet-600 shadow-violet-200";
  return "bg-emerald-500 hover:bg-emerald-600 shadow-emerald-200";
};

function LevelActivities() {
  const navigate = useNavigate();
  const { moduleId, levelId } = useParams();
  const [moduleProgress, setModuleProgress] = useState(null);
  const [module, setModule] = useState(null);
  const [level, setLevel] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState("");

  useEffect(() => {
    const studentId = getStudentId();
    setIsLoading(true);
    setLoadError("");

    Promise.all([
      getModuleDetails(moduleId),
      getLevelDetails(levelId),
      studentId ? getModuleProgress(studentId, levelId) : Promise.resolve(null),
    ])
      .then(([moduleData, levelData, progressData]) => {
        setModule(moduleData);
        setLevel({
          id: levelData.id,
          key: levelData.level,
          name: levelLabels[levelData.level] ?? levelData.level,
          backendTitle: levelData.title,
          description: levelData.description,
          recommendation: levelData.recommendation
            ?? levelData.objective
            ?? levelData.learning_goal
            ?? levelData.what_you_learn
            ?? levelData.description,
          lessonCount: levelData.lessons_count ?? 0,
          practiceCount: levelData.practice_count ?? 0,
          examCount: levelData.exam_count ?? 0,
          completedLessons: levelData.completed_lessons,
          estimatedMinutes: levelData.estimated_minutes,
          theoryProgressPercentage: levelData.theory_progress_percentage,
          exercisesCount: levelData.exercises_count ?? levelData.practice_count,
          correctAnswers: levelData.correct_answers,
          practiceAccuracy: levelData.practice_accuracy,
          practiceProgressPercentage: levelData.practice_progress_percentage,
          questionsCount: levelData.questions_count ?? levelData.exam_count,
          passingScore: levelData.passing_score,
          bestScore: levelData.best_score,
          examProgressPercentage: levelData.exam_progress_percentage,
        });
        setModuleProgress(progressData);
      })
      .catch(() => {
        setModule(null);
        setLevel(null);
        setModuleProgress(null);
        setLoadError("No pudimos cargar los datos de este nivel desde el servidor.");
      })
      .finally(() => setIsLoading(false));
  }, [moduleId, levelId]);

  const completion = {
    theoryCompleted: moduleProgress?.theory_completed ?? false,
    practiceCompleted: moduleProgress?.practice_completed ?? false,
  };
  const isExamUnlocked = completion.theoryCompleted && completion.practiceCompleted;
  const progressPercentage = moduleProgress?.progress_percentage ?? 0;
  const hero = levelHeroConfig[level?.key] ?? levelHeroConfig.BASICO;
  const levelButtonTone = getLevelButtonTone(level?.key);
  const practiceTotal = moduleProgress?.practice_total_count;
  const practiceCorrect = moduleProgress?.practice_completed_count;
  const calculatedAccuracy = Number(practiceTotal) > 0 && practiceCorrect !== null && practiceCorrect !== undefined
    ? Math.round((Number(practiceCorrect) / Number(practiceTotal)) * 100)
    : null;

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", active: true },
    { label: "Mis logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const activities = [
    {
      title: "Teoria",
      description: level?.lessonCount
        ? `Avanza por ${level.lessonCount} lecciones antes de resolver ejercicios.`
        : "Lee los conceptos clave del nivel antes de resolver ejercicios.",
      detail: completion.theoryCompleted ? "Completado" : `${level?.lessonCount ?? 0} lecciones`,
      image: "/assets/teoria.png",
      tone: "bg-nt-green/15",
      barTone: "bg-nt-green",
      badgeTone: "bg-nt-green/15 text-green-700",
      progress: completion.theoryCompleted ? 100 : 0,
      metricAccent: "text-emerald-600",
      metrics: [
        availableMetric("Lecciones", level?.lessonCount, BookOpen),
        availableMetric("Completadas", level?.completedLessons, CheckCircle2),
        availableMetric("Tiempo promedio", level?.estimatedMinutes ? `${level.estimatedMinutes} min` : null, Clock3),
        percentageMetric("Completado", level?.theoryProgressPercentage, Gauge),
      ].filter(Boolean),
      actionLabel: "Abrir teoria",
      onClick: () => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } }),
    },
    {
      title: "Practica",
      description: `Resuelve ${level?.practiceCount ?? 0} ejercicios guiados y recibe ayuda cuando lo necesites.`,
      detail: completion.practiceCompleted ? "Completado" : "Disponible",
      image: "/assets/practica.png",
      tone: "bg-nt-blue/10",
      barTone: "bg-nt-blue",
      badgeTone: "bg-nt-blue/10 text-nt-blue",
      progress: completion.practiceCompleted ? 100 : 0,
      metricAccent: "text-blue-600",
      metrics: [
        availableMetric("Ejercicios", level?.exercisesCount, Target),
        availableMetric("Correctos", level?.correctAnswers ?? practiceCorrect, CheckCircle2),
        percentageMetric("Precisión", level?.practiceAccuracy ?? calculatedAccuracy, Gauge),
        percentageMetric("Completado", level?.practiceProgressPercentage, Percent),
      ].filter(Boolean),
      actionLabel: "Abrir practica",
      onClick: () => navigate(`/practice/${levelId}`, { state: { module, level } }),
    },
    {
      title: "Examen Final",
      description: `Demuestra lo aprendido en ${level?.examCount ?? 0} preguntas cuando completes las actividades previas.`,
      detail: isExamUnlocked ? "Disponible" : "Bloqueado",
      image: "/assets/examen-final.png",
      tone: isExamUnlocked ? "bg-nt-purple/10" : "bg-slate-100",
      barTone: isExamUnlocked ? "bg-nt-purple" : "bg-slate-300",
      badgeTone: isExamUnlocked ? "bg-nt-purple/10 text-nt-purple" : "bg-slate-100 text-slate-500",
      progress: isExamUnlocked ? 0 : 0,
      metricAccent: "text-violet-600",
      metrics: [
        availableMetric("Preguntas", level?.questionsCount, FileQuestion),
        percentageMetric("Nota mínima", level?.passingScore, Target),
        percentageMetric("Mejor nota", level?.bestScore ?? (moduleProgress?.exam_best_score > 0 ? moduleProgress.exam_best_score : null), Trophy),
        percentageMetric("Completado", level?.examProgressPercentage, Percent),
      ].filter(Boolean),
      actionLabel: "Ir al examen",
      disabled: !isExamUnlocked,
      onClick: () => navigate(`/final-exam/${levelId}`, { state: { module, level } }),
    },
  ];

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="flex min-h-[420px] items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  if (loadError || !module || !level) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <section className="rounded-nt-card border border-white/80 bg-white/90 p-8 text-center shadow-nt-card">
          <h1 className="text-2xl font-black text-nt-text-primary">Nivel no disponible</h1>
          <p className="mt-3 text-sm font-semibold text-nt-text-secondary">{loadError}</p>
          <button
            type="button"
            className="mt-5 rounded-nt-button bg-nt-blue px-5 py-3 text-sm font-black text-white"
            onClick={() => navigate(`/module/${moduleId}`)}
          >
            Volver al módulo
          </button>
        </section>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <BackButton onClick={() => navigate(`/module/${moduleId}`, { state: { module } })}>
          Volver al módulo
        </BackButton>
      }
      rightPanel={
        <div className="space-y-5">
          <LearningProgressPanel
            studentId={getStudentId()}
            moduloId={levelId ?? moduleId}
            progress={moduleProgress}
            compact
          />
          <aside className="relative min-h-[164px] overflow-hidden rounded-[28px] border border-violet-200 bg-gradient-to-br from-violet-100 via-indigo-50 to-sky-100 p-5 shadow-[0_16px_38px_rgba(99,102,241,0.16)]">
            <span className="absolute left-4 top-3 text-sm text-violet-400/70" aria-hidden="true">✦</span>
            <span className="absolute right-5 top-4 text-sm text-amber-400/80" aria-hidden="true">✦</span>
            <span className="absolute bottom-5 left-8 size-1.5 rounded-full bg-cyan-300/70" aria-hidden="true" />
            <div className="relative z-10 max-w-[58%] pt-3">
              <h2 className="text-xl font-black leading-tight text-nt-text-primary">Sobre este nivel</h2>
              {level.recommendation && (
                <p className="mt-2 text-sm font-semibold leading-5 text-slate-700">{level.recommendation}</p>
              )}
            </div>
            <div className="absolute -bottom-10 -right-8 size-36 rounded-full bg-white/40 blur-2xl" />
            <img
              src="/assets/neo_pensando.png"
              alt="NEO pensando"
              className="absolute right-2 top-1/2 z-10 h-32 w-32 -translate-y-1/2 object-contain drop-shadow-[0_14px_20px_rgba(76,29,149,0.20)]"
            />
          </aside>

        </div>
      }
    >
      <section className={`relative overflow-hidden rounded-[32px] border bg-gradient-to-br ${hero.heroTone} ${hero.borderTone} shadow-[0_18px_45px_rgba(59,130,246,0.12)]`}>
        <div className="pointer-events-none absolute -right-16 -top-20 size-56 rounded-full bg-white/55 blur-3xl" />
        <div className="pointer-events-none absolute bottom-0 left-1/3 h-24 w-72 rounded-full bg-blue-200/20 blur-3xl" />
        <div className="relative grid items-center gap-5 px-5 py-6 md:px-7 lg:grid-cols-[288px_minmax(0,1fr)_150px] lg:gap-7 lg:px-8 lg:py-7">
          <div className="relative mx-auto flex size-56 items-center justify-center sm:size-64 lg:mx-0 lg:size-72">
            <div className={`absolute inset-5 rounded-full bg-gradient-to-br ${hero.imageGlow} blur-2xl`} />
            <img
              src={hero.image}
              alt={`Isla del nivel ${level.name}`}
              className="relative z-10 size-56 object-contain drop-shadow-[0_20px_26px_rgba(30,58,138,0.22)] sm:size-64 lg:size-72"
            />
          </div>

          <div className="min-w-0 text-center lg:text-left">
            <h1 className="text-3xl font-black leading-tight text-nt-text-primary md:text-4xl">Nivel: {level.name}</h1>
            <p className={`mt-2 text-sm font-black sm:text-base ${hero.accentTone}`}>
              {module.title} <span aria-hidden="true">•</span> {hero.position} {hero.emoji}
            </p>
            <p className="mx-auto mt-3 max-w-2xl text-sm font-semibold leading-6 text-slate-600 sm:text-[15px] lg:mx-0">{hero.description}</p>
          </div>

          <aside className={`mx-auto w-full max-w-[220px] rounded-[22px] border p-4 text-center shadow-[0_10px_26px_rgba(30,58,138,0.08)] backdrop-blur ${hero.badgeTone}`}>
            <p className="text-xs font-black uppercase tracking-[0.16em] text-nt-text-secondary">Dificultad</p>
            <p className={`mt-1 text-xl font-black ${hero.accentTone}`}>{hero.difficulty} {hero.emoji}</p>
            <div className="my-3 h-px bg-slate-200/80" />
            <div className="flex items-center justify-between gap-3 text-xs font-black text-nt-text-secondary">
              <span>Avance</span><span className="text-nt-blue">{progressPercentage}%</span>
            </div>
            <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-200/80">
              <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-cyan-400" style={{ width: `${progressPercentage}%` }} />
            </div>
          </aside>
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

                {item.metrics.length > 0 && (
                  <div className="mt-3 flex flex-wrap items-center gap-x-5 gap-y-2">
                    {item.metrics.map((metric) => {
                      const MetricIcon = metric.icon;
                      return (
                        <div key={metric.label} className="flex min-w-0 items-center gap-1.5">
                          <MetricIcon className={`size-4 shrink-0 ${item.metricAccent}`} aria-hidden="true" />
                          <div className="min-w-0 leading-none">
                            <strong className="block truncate text-sm font-black text-nt-text-primary">{metric.value}</strong>
                            <span className="mt-0.5 block truncate text-[10px] font-bold text-nt-text-secondary">{metric.label}</span>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}

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
                className={`inline-flex h-12 items-center justify-center gap-2 rounded-nt-button px-5 text-sm font-black text-white shadow-lg transition disabled:bg-slate-300 disabled:text-slate-500 disabled:shadow-none ${levelButtonTone}`}
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

    </StudentLayout>
  );
}

export default LevelActivities;
