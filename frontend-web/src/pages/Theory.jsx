import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { BookOpenCheck, CheckCircle2, ChevronRight, Circle, Pencil, Trophy } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import { getTheoryLessons } from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const getLessonLevelKey = (level) => {
  const value = String(level?.key ?? level?.level ?? level?.name ?? "basico")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();
  if (value.includes("intermedio") || value.includes("intermediate")) return "intermedio";
  if (value.includes("avanzado") || value.includes("advanced")) return "avanzado";
  return "basico";
};

const asPercentage = (value) => {
  if (value === null || value === undefined || value === "") return null;
  return Math.min(100, Math.max(0, Number(value) || 0));
};

function TheoryProgressPanel({ progress, lessons }) {
  const totalLessons = progress?.total_lessons ?? lessons.length;
  const completedLessons = progress?.completed_lessons
    ?? progress?.theory_completed_lessons
    ?? (progress?.theory_completed === true ? totalLessons : null);
  const theoryPercentage = asPercentage(
    progress?.theory_progress_percentage
      ?? (completedLessons !== null && totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : null)
  );
  const practicePercentage = asPercentage(
    progress?.practice_progress_percentage ?? (progress?.practice_completed === true ? 100 : null)
  );
  const examPercentage = asPercentage(
    progress?.exam_progress_percentage ?? (progress?.exam_passed === true ? 100 : null)
  );
  const rows = [
    { label: "Teoría", icon: BookOpenCheck, percentage: theoryPercentage, complete: progress?.theory_completed, tone: "from-blue-500 to-indigo-600", surface: "bg-blue-50" },
    { label: "Práctica", icon: Pencil, percentage: practicePercentage, complete: progress?.practice_completed, tone: "from-emerald-400 to-green-600", surface: "bg-emerald-50" },
    { label: "Examen final", icon: Trophy, percentage: examPercentage, complete: progress?.exam_passed, tone: "from-violet-500 to-purple-700", surface: "bg-violet-50" },
  ];

  return (
    <section className="rounded-[26px] border border-white bg-white p-4 shadow-[0_14px_34px_rgba(30,58,138,0.11)]">
      <h2 className="text-lg font-black text-nt-blue">Tu progreso en teoría</h2>
      <div className="mt-4 grid grid-cols-[58px_minmax(0,1fr)] items-center gap-3">
        <div
          className="grid size-[58px] place-items-center rounded-full"
          style={{ background: theoryPercentage === null ? "#e2e8f0" : `conic-gradient(#7c3aed ${theoryPercentage * 3.6}deg, #e2e8f0 0deg)` }}
        >
          <div className="grid size-11 place-items-center rounded-full bg-white text-xs font-black text-nt-purple">
            {theoryPercentage === null ? "—" : `${theoryPercentage}%`}
          </div>
        </div>
        <div className="min-w-0">
          <p className="text-sm font-black text-nt-text-primary">Progreso del nivel</p>
          <p className="mt-0.5 text-xs font-semibold text-nt-text-secondary">
            {completedLessons === null ? `— / ${totalLessons}` : `${completedLessons} / ${totalLessons}`} lecciones completadas
          </p>
          <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-200">
            <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-nt-green" style={{ width: `${theoryPercentage ?? 0}%` }} />
          </div>
        </div>
      </div>
      <div className="mt-4 grid gap-2">
        {rows.map(({ label, icon: Icon, percentage, complete, tone, surface }) => (
          <div key={label} className={`flex items-center gap-3 rounded-[16px] px-3 py-2.5 ${surface}`}>
            <span className={`grid size-9 shrink-0 place-items-center rounded-[12px] bg-gradient-to-br text-white shadow-sm ${tone}`}><Icon className="size-[18px]" /></span>
            <div className="min-w-0 flex-1">
              <p className="text-xs font-black text-nt-text-primary">{label}</p>
              <p className="text-[11px] font-semibold text-nt-text-secondary">{complete ? "Completada" : "Pendiente"}</p>
            </div>
            <strong className="text-xs font-black text-nt-blue">{percentage === null ? "—" : `${percentage}%`}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}

function Theory() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId } = useParams();
  const [lessons, setLessons] = useState([]);
  const [moduleProgress, setModuleProgress] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  const module = location.state?.module ?? { id: moduleId, title: "Módulo" };
  const level = location.state?.level ?? {
    id: levelId,
    name: "Nivel",
    backendTitle: "Contenido del nivel",
  };
  const studentId = getStudentId();
  const lessonLevelKey = getLessonLevelKey(level);

  useEffect(() => {
    let active = true;
    setIsLoading(true);
    setError("");

    Promise.allSettled([
      getTheoryLessons(levelId),
      studentId ? getModuleProgress(studentId, levelId) : Promise.resolve(null),
    ])
      .then(([lessonsResult, progressResult]) => {
        if (!active) return;
        if (lessonsResult.status !== "fulfilled" || !Array.isArray(lessonsResult.value)) {
          setLessons([]);
          setError("No pudimos cargar las lecciones desde el servidor.");
        } else {
          setLessons(lessonsResult.value);
        }
        setModuleProgress(progressResult.status === "fulfilled" ? progressResult.value : null);
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => {
      active = false;
    };
  }, [levelId, studentId]);

  const theoryCompleted = moduleProgress?.theory_completed === true;
  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true },
    { label: "Mis Logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const openLesson = (lesson) => {
    navigate(`/module/${moduleId}/level/${levelId}/theory/lesson/${lesson.id}`, {
      state: { module, level },
    });
  };

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <BackButton onClick={() => navigate(`/module/${moduleId}/level/${levelId}`, { state: { module, level } })}>
          Volver a actividades
        </BackButton>
      }
      rightPanel={
        <div className="space-y-5">
          <TheoryProgressPanel progress={moduleProgress} lessons={lessons} />
          <aside className="relative min-h-[210px] w-full overflow-hidden rounded-3xl border border-violet-100 bg-gradient-to-br from-white via-violet-50 to-sky-100 p-4 shadow-[0_16px_36px_rgba(99,102,241,0.14)]">
            <span className="absolute left-5 top-4 text-sm text-violet-300/70" aria-hidden="true">✦</span>
            <span className="absolute right-6 top-6 text-xs text-amber-300/70" aria-hidden="true">✦</span>
            <span className="absolute bottom-5 left-9 size-1.5 rounded-full bg-cyan-300/60" aria-hidden="true" />
            <div className="absolute -bottom-12 -right-10 size-40 rounded-full bg-violet-300/30 blur-3xl" />
            <div className="absolute -left-10 top-8 size-28 rounded-full bg-sky-200/30 blur-3xl" />
            <div className="relative z-10 grid min-h-[178px] grid-cols-[minmax(0,1fr)_120px] items-center gap-2">
              <div className="min-w-0 self-center">
                <h2 className="text-lg font-black leading-tight text-nt-text-primary">NEO te aconseja</h2>
                <div className="mt-3 rounded-[18px] border border-white/80 bg-white/65 px-3 py-2.5 shadow-sm backdrop-blur">
                  <p className="line-clamp-4 text-xs font-semibold leading-5 text-slate-700">Lee las lecciones en orden para entender mejor los ejercicios.</p>
                </div>
                <p className="mt-2 text-xs font-black text-violet-700">💡 ¡Tú puedes!</p>
              </div>
              <img
                src="/assets/neo_leccion.png"
                alt="NEO"
                className="size-46 justify-self-end object-contain drop-shadow-[0_16px_24px_rgba(76,29,149,0.20)]"
              />
            </div>
          </aside>
        </div>
      }
    >
      <section className="relative overflow-hidden rounded-[32px] border border-sky-100 bg-gradient-to-br from-white via-sky-50 to-emerald-50 p-4 shadow-[0_16px_40px_rgba(59,130,246,0.13)] sm:p-5">
        <div className="pointer-events-none absolute -left-16 -top-20 size-64 rounded-full bg-emerald-200/25 blur-3xl" />
        <div className="pointer-events-none absolute -bottom-20 right-10 size-56 rounded-full bg-blue-200/25 blur-3xl" />
        <div className="relative grid items-center gap-4 md:grid-cols-[minmax(220px,260px)_minmax(0,1fr)] md:gap-6">
          <div className="flex min-h-[210px] items-center justify-center">
            <img
              src="/assets/teoria.png"
              alt="Teoría"
              className="size-[220px] object-contain drop-shadow-[0_18px_25px_rgba(30,58,138,0.20)] sm:size-[240px] lg:size-[250px]"
            />
          </div>
          <div className="min-w-0 text-center md:text-left">
            <h1 className="text-4xl font-black tracking-tight text-nt-text-primary lg:text-5xl">
              Teoría - {level.name}
            </h1>
            <p className="mt-1.5 text-base font-black text-emerald-700">{level.backendTitle}</p>
            {level.description && (
              <p className="mt-2.5 max-w-2xl text-sm font-semibold leading-6 text-slate-700">{level.description}</p>
            )}
          </div>
        </div>
      </section>

      <section className="rounded-nt-card border border-white/85 bg-white/90 p-5 shadow-nt-card backdrop-blur sm:p-6">
        <div className="mb-5 flex items-center justify-between gap-4">
          <div>
            <h2 className="text-xl font-black text-nt-text-primary">Lecciones de teoría</h2>
            <p className="text-sm font-semibold text-nt-text-secondary">Un concepto a la vez, de menor a mayor dificultad.</p>
          </div>
          <span className="rounded-full bg-nt-blue/10 px-3 py-1 text-xs font-black text-nt-blue">
            {lessons.length} lecciones
          </span>
        </div>

        {isLoading ? (
          <div className="grid gap-3">
            {[1, 2, 3].map((item) => <div key={item} className="h-28 animate-pulse rounded-[24px] bg-nt-sky/70" />)}
          </div>
        ) : error || lessons.length === 0 ? (
          <div className="rounded-[24px] border border-amber-200 bg-amber-50 p-6 text-center">
            <p className="font-black text-amber-800">{error || "Este nivel todavía no tiene lecciones publicadas."}</p>
            <p className="mt-2 text-sm font-semibold text-amber-700">Vuelve a actividades e inténtalo nuevamente más tarde.</p>
          </div>
        ) : (
          <div className="grid gap-4">
            {lessons.map((lesson, index) => (
              <button
                key={lesson.id}
                type="button"
                onClick={() => openLesson(lesson)}
                className="group grid min-w-0 grid-cols-[88px_minmax(0,1fr)_auto] items-center gap-4 rounded-[26px] border border-white bg-white p-4 text-left shadow-sm transition duration-200 hover:-translate-y-0.5 hover:border-nt-blue/25 hover:shadow-md"
              >
                <img
                  src={`/assets/lecciones_${lessonLevelKey}${index + 1}.png`}
                  alt=""
                  className="size-[88px] object-contain drop-shadow-[0_10px_14px_rgba(30,58,138,0.16)]"
                  onError={(event) => {
                    event.currentTarget.onerror = null;
                    event.currentTarget.src = "/assets/teoria.png";
                  }}
                />
                <span className="min-w-0">
                  <span className="flex flex-wrap items-center gap-2">
                    <strong className="text-base font-black text-nt-text-primary sm:text-lg">{lesson.title}</strong>
                    {theoryCompleted ? (
                      <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-2 py-1 text-[11px] font-black text-green-700">
                        <CheckCircle2 className="size-3.5" /> Completada
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 rounded-full bg-nt-sky px-2 py-1 text-[11px] font-black text-nt-blue">
                        <Circle className="size-3.5" /> Disponible
                      </span>
                    )}
                  </span>
                  <span className="mt-1 block text-sm font-semibold text-nt-text-secondary">{lesson.summary}</span>
                  {lesson.subtitle && <span className="mt-2 block text-xs font-black text-nt-purple">{lesson.subtitle}</span>}
                </span>
                <span className="grid size-10 place-items-center rounded-full bg-nt-blue text-white transition group-hover:bg-nt-purple">
                  <ChevronRight className="size-5" />
                </span>
              </button>
            ))}
          </div>
        )}
      </section>
    </StudentLayout>
  );
}

export default Theory;
