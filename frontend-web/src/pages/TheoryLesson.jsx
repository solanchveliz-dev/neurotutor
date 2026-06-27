import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, ArrowRight, BookOpenCheck, CheckCircle2 } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import ProgressCard from "../components/student/ProgressCard";
import { Button } from "@/components/ui/button";
import { getTheoryLesson, getTheoryLessons } from "../services/learningService";
import { markTheoryCompleted } from "../services/progressService";
import { getStudentId } from "../utils/auth";

function TheoryLesson() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId, lessonId } = useParams();
  const [lesson, setLesson] = useState(null);
  const [lessons, setLessons] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCompleting, setIsCompleting] = useState(false);
  const [error, setError] = useState("");
  const [completionError, setCompletionError] = useState("");

  const module = location.state?.module ?? { id: moduleId, title: "Módulo" };
  const level = location.state?.level ?? { id: levelId, name: "Nivel", backendTitle: "Contenido del nivel" };
  const studentId = getStudentId();

  useEffect(() => {
    let active = true;
    setIsLoading(true);
    setError("");

    Promise.all([getTheoryLesson(lessonId), getTheoryLessons(levelId)])
      .then(([lessonData, lessonList]) => {
        if (!active) return;
        setLesson(lessonData);
        setLessons(Array.isArray(lessonList) ? lessonList : []);
      })
      .catch(() => {
        if (active) setError("No pudimos cargar esta lección desde el servidor.");
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => {
      active = false;
    };
  }, [lessonId, levelId]);

  const lessonIndex = useMemo(
    () => lessons.findIndex((item) => String(item.id) === String(lessonId)),
    [lessons, lessonId]
  );
  const previousLesson = lessonIndex > 0 ? lessons[lessonIndex - 1] : null;
  const nextLesson = lessonIndex >= 0 ? lessons[lessonIndex + 1] : null;
  const position = lessonIndex >= 0 ? lessonIndex + 1 : 0;
  const routeProgress = lessons.length ? Math.round((position / lessons.length) * 100) : 0;

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true, onClick: () => navigate(`/module/${moduleId}`, { state: { module } }) },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const lessonPath = (targetLesson) =>
    `/module/${moduleId}/level/${levelId}/theory/lesson/${targetLesson.id}`;

  const goToLesson = (targetLesson) => {
    navigate(lessonPath(targetLesson), { state: { module, level } });
  };

  const completeTheory = async () => {
    if (!studentId) {
      setCompletionError("No pudimos identificar al estudiante. Inicia sesión nuevamente.");
      return;
    }

    setIsCompleting(true);
    setCompletionError("");
    try {
      await markTheoryCompleted(studentId, levelId);
      navigate(`/module/${moduleId}/level/${levelId}/practice`, { state: { module, level } });
    } catch {
      setCompletionError("No pudimos guardar la teoría como completada. Revisa tu conexión e intenta nuevamente.");
    } finally {
      setIsCompleting(false);
    }
  };

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="grid min-h-[500px] place-items-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  if (error || !lesson) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <section className="rounded-nt-card border border-amber-200 bg-amber-50 p-8 text-center shadow-nt-card">
          <h1 className="text-2xl font-black text-amber-900">Lección no disponible</h1>
          <p className="mt-2 text-sm font-semibold text-amber-800">{error}</p>
          <Button
            className="mt-5 rounded-[18px] bg-nt-blue font-black text-white"
            onClick={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}
          >
            Volver a lecciones
          </Button>
        </section>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <div className="rounded-[28px] border border-white/80 bg-white/85 p-3 shadow-sm backdrop-blur">
          <div className="flex items-center justify-between gap-3">
            <BackButton onClick={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}>
              Lecciones
            </BackButton>
            <h1 className="hidden text-lg font-black text-nt-text-primary sm:block">Teoría - {level.name}</h1>
            <span className="rounded-full bg-nt-blue px-4 py-2 text-sm font-black text-white shadow-lg shadow-nt-blue/20">
              {position}/{lessons.length}
            </span>
          </div>
          <div className="mt-3 flex items-center gap-3">
            <div className="h-3 flex-1 overflow-hidden rounded-full bg-white shadow-inner">
              <div className="h-full rounded-full bg-gradient-to-r from-nt-green via-nt-blue to-nt-purple" style={{ width: `${routeProgress}%` }} />
            </div>
            <span className="text-sm font-black text-nt-blue">{routeProgress}%</span>
          </div>
        </div>
      }
      rightPanel={
        <div className="space-y-5">
          <LearningProgressPanel studentId={studentId} moduloId={levelId} />
          <ProgressCard
            title="Lección actual"
            subtitle={`${position}/${lessons.length}`}
            value={routeProgress}
            totalLabel={level.name}
            tone="purple"
          />
        </div>
      }
    >
      <article className="rounded-nt-card border border-white/85 bg-white/95 p-5 shadow-nt-card sm:p-7 lg:p-8">
        <div className="flex flex-col gap-4 border-b border-nt-border pb-6 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <span className="inline-flex rounded-full bg-nt-green px-3 py-1 text-xs font-black text-white">
              Lección {position}
            </span>
            <h1 className="mt-4 text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
              {lesson.title} <span aria-hidden="true">{lesson.icon}</span>
            </h1>
            {lesson.subtitle && <p className="mt-2 text-base font-black text-nt-purple">{lesson.subtitle}</p>}
            <p className="mt-3 max-w-3xl text-sm font-semibold leading-6 text-nt-text-secondary">{lesson.summary}</p>
          </div>
          <div className="grid size-16 shrink-0 place-items-center rounded-[24px] bg-nt-sky text-3xl shadow-sm">
            {lesson.icon || <BookOpenCheck className="size-7 text-nt-blue" />}
          </div>
        </div>

        <div
          className="mt-7 grid gap-5 text-base font-semibold leading-7 text-slate-700
            [&_h2]:text-2xl [&_h2]:font-black [&_h2]:text-nt-text-primary
            [&_h3]:text-lg [&_h3]:font-black [&_h3]:text-nt-blue
            [&_.lesson-lead]:rounded-[26px] [&_.lesson-lead]:bg-gradient-to-br [&_.lesson-lead]:from-nt-sky/80 [&_.lesson-lead]:to-violet-50 [&_.lesson-lead]:p-5
            [&_.lesson-list]:grid [&_.lesson-list]:gap-2 [&_.lesson-list]:pl-5
            [&_.lesson-list_li]:list-disc
            [&_.lesson-steps]:grid [&_.lesson-steps]:gap-3 [&_.lesson-steps]:pl-6 [&_.lesson-steps_li]:list-decimal
            [&_.math-example]:grid [&_.math-example]:gap-2 [&_.math-example]:rounded-[26px] [&_.math-example]:border [&_.math-example]:border-violet-100 [&_.math-example]:bg-violet-50 [&_.math-example]:p-5 [&_.math-example]:text-center
            [&_.math-example_strong]:text-2xl [&_.math-example_strong]:font-black [&_.math-example_strong]:text-nt-purple
            [&_.math-example_span]:text-sm [&_.math-example_span]:text-nt-text-secondary
            [&_.tip-box]:rounded-[24px] [&_.tip-box]:border [&_.tip-box]:border-amber-200 [&_.tip-box]:bg-amber-50 [&_.tip-box]:p-5 [&_.tip-box]:text-amber-900
            [&_.concept-grid]:grid [&_.concept-grid]:gap-3 [&_.concept-grid]:sm:grid-cols-3
            [&_.concept-grid_div]:grid [&_.concept-grid_div]:gap-1 [&_.concept-grid_div]:rounded-[22px] [&_.concept-grid_div]:bg-nt-sky/65 [&_.concept-grid_div]:p-4 [&_.concept-grid_div]:text-center
            [&_.concept-grid_strong]:text-2xl [&_.concept-grid_strong]:font-black [&_.concept-grid_strong]:text-nt-blue
            [&_.concept-grid_span]:text-sm [&_.concept-grid_span]:text-nt-text-secondary
            [&_.fraction-parts]:grid [&_.fraction-parts]:gap-3 [&_.fraction-parts]:sm:grid-cols-2
            [&_.fraction-parts_div]:grid [&_.fraction-parts_div]:gap-2 [&_.fraction-parts_div]:rounded-[24px] [&_.fraction-parts_div]:bg-nt-sky/70 [&_.fraction-parts_div]:p-5 [&_.fraction-parts_div]:text-center
            [&_.fraction-parts_strong]:text-5xl [&_.fraction-parts_strong]:font-black [&_.fraction-parts_strong]:text-nt-purple
            [&_.problem-box]:rounded-[24px] [&_.problem-box]:bg-green-50 [&_.problem-box]:p-5 [&_.problem-box]:text-green-900"
          dangerouslySetInnerHTML={{ __html: lesson.content_html }}
        />
      </article>

      {completionError && (
        <p className="rounded-[20px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">
          {completionError}
        </p>
      )}

      <section className="flex flex-col gap-3 rounded-[26px] border border-white/85 bg-white/90 p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
        <Button
          type="button"
          variant="ghost"
          disabled={!previousLesson}
          className="h-12 rounded-[18px] px-5 font-black text-nt-text-secondary"
          onClick={() => previousLesson && goToLesson(previousLesson)}
        >
          <ArrowLeft className="size-4" /> Anterior
        </Button>

        {nextLesson ? (
          <Button
            type="button"
            className="h-12 rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple px-6 font-black text-white"
            onClick={() => goToLesson(nextLesson)}
          >
            Siguiente <ArrowRight className="size-4" />
          </Button>
        ) : (
          <Button
            type="button"
            disabled={isCompleting}
            className="h-12 rounded-[18px] bg-gradient-to-r from-nt-green to-emerald-500 px-6 font-black text-white"
            onClick={completeTheory}
          >
            {isCompleting ? "Guardando progreso..." : "Finalizar teoría e ir a práctica"}
            <CheckCircle2 className="size-4" />
          </Button>
        )}
      </section>
    </StudentLayout>
  );
}

export default TheoryLesson;
