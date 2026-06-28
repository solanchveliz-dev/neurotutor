import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { BookOpen, CheckCircle2, ChevronRight, Circle, Layers3 } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import { getTheoryLessons } from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

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
    { label: "Módulos", active: true, onClick: () => navigate(`/module/${moduleId}`, { state: { module } }) },
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
          <LearningProgressPanel
            studentId={studentId}
            moduloId={levelId}
            progress={moduleProgress}
          />
          <section className="rounded-nt-card border border-white/85 bg-white/95 p-5 shadow-nt-card">
            <div className="grid size-11 place-items-center rounded-[18px] bg-nt-green/15 text-green-700">
              <Layers3 className="size-5" />
            </div>
            <h2 className="mt-4 text-xl font-black text-nt-text-primary">Teoría del nivel</h2>
            <p className="mt-1 text-sm font-semibold text-nt-text-secondary">
              {lessons.length} {lessons.length === 1 ? "lección disponible" : "lecciones disponibles"}
            </p>
            <div className="mt-4 h-3 overflow-hidden rounded-full bg-nt-border">
              <div
                className="h-full rounded-full bg-gradient-to-r from-nt-green to-nt-blue"
                style={{ width: theoryCompleted ? "100%" : "0%" }}
              />
            </div>
            <p className="mt-2 text-xs font-bold text-nt-text-secondary">
              {theoryCompleted ? "Teoría completada. Puedes repasar cuando quieras." : "Completa la última lección para registrar tu avance."}
            </p>
          </section>
        </div>
      }
    >
      <section className="rounded-nt-card border border-white/85 bg-white/92 p-5 shadow-nt-card backdrop-blur sm:p-7">
        <span className="inline-flex rounded-full bg-nt-green px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-green/20">
          {module.title}
        </span>
        <h1 className="mt-4 text-3xl font-black text-nt-text-primary sm:text-4xl">
          Teoría - {level.name}
        </h1>
        <p className="mt-2 text-sm font-black text-nt-blue">{level.backendTitle}</p>
        <p className="mt-3 max-w-2xl text-sm font-semibold leading-6 text-nt-text-secondary">
          Explora las lecciones en orden. El contenido y los ejemplos provienen del servidor y están adaptados a este nivel.
        </p>
      </section>

      <section className="rounded-nt-card border border-white/85 bg-white/90 p-5 shadow-nt-card backdrop-blur sm:p-6">
        <div className="mb-5 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="grid size-12 place-items-center rounded-[20px] bg-nt-green/15 text-green-700">
              <BookOpen className="size-6" />
            </div>
            <div>
              <h2 className="text-xl font-black text-nt-text-primary">Lecciones de teoría</h2>
              <p className="text-sm font-semibold text-nt-text-secondary">Un concepto a la vez, de menor a mayor dificultad.</p>
            </div>
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
                className="group grid min-w-0 grid-cols-[56px_minmax(0,1fr)_auto] items-center gap-4 rounded-[26px] border border-white bg-white p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-nt-blue/25 hover:shadow-nt-card"
              >
                <span className="grid size-14 place-items-center rounded-[20px] bg-gradient-to-br from-nt-blue to-nt-purple text-2xl text-white shadow-lg shadow-nt-blue/20">
                  {lesson.icon || index + 1}
                </span>
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
