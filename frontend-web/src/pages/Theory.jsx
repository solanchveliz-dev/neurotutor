import { ArrowLeft, ArrowRight, BookOpen, CheckCircle2, Clock, PlayCircle, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import NeoCard from "../components/student/NeoCard";
import PrimaryButton from "../components/student/PrimaryButton";
import ProgressCard from "../components/student/ProgressCard";
import { modulesData } from "../data/modulesData";
import { getTheoryLessons } from "../data/theoryLessons";
import { getLearningContent } from "../services/learningService";
import { markTheoryCompleted } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const numericFallbackMap = {
  1: "fracciones",
  2: "decimales",
  3: "porcentajes",
};

function Theory() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId } = useParams();
  const [theoryHtml, setTheoryHtml] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isUsingFallback, setIsUsingFallback] = useState(false);

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
    title: fallbackModule?.title ?? "Módulo",
  };
  const level = location.state?.level ?? {
    id: levelId,
    name: fallbackLevel?.name ?? "Nivel",
    backendTitle: fallbackLevel?.name ?? "Teoría del nivel",
    progress: fallbackLevel?.progress ?? 0,
    status: fallbackLevel?.status ?? "Disponible",
  };

  const lessons = getTheoryLessons({ module, level, moduleId, levelId });
  const hasLocalLessons = lessons.length > 0;
  const firstLesson = lessons[0];
  const overviewProgress = hasLocalLessons ? Math.max(level.progress || 0, 10) : level.progress || 0;

  useEffect(() => {
    if (hasLocalLessons) {
      setTheoryHtml("");
      setIsUsingFallback(false);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setIsUsingFallback(false);

    getLearningContent(levelId)
      .then((content) => {
        setTheoryHtml(content?.teoriaHtml ?? "");
        setIsUsingFallback(!content?.teoriaHtml);
      })
      .catch(() => {
        setTheoryHtml("");
        setIsUsingFallback(true);
      })
      .finally(() => setIsLoading(false));
  }, [levelId, hasLocalLessons]);

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true, onClick: () => navigate(`/module/${moduleId}`, { state: { module } }) },
    { label: "Mis logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const goToLesson = (lesson) => {
    navigate(`/module/${moduleId}/level/${levelId}/theory/${lesson.id}`, {
      state: { module, level },
    });
  };

  const goToPractice = async () => {
    const studentId = getStudentId();
    const progressModuloId = levelId ?? moduleId;

    if (studentId && progressModuloId) {
      try {
        await markTheoryCompleted(studentId, progressModuloId);
      } catch (error) {
        console.warn("No se pudo marcar la teoria como completada.", error);
      }
    }

    navigate(`/module/${moduleId}/level/${levelId}/practice`, {
      state: { module, level },
    });
  };

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <header className="flex w-full flex-col gap-3 rounded-[28px] bg-white/40 px-3 py-2 backdrop-blur-sm md:flex-row md:items-center md:justify-between">
          <button
            type="button"
            className="inline-flex items-center gap-2 text-sm font-black text-nt-blue transition hover:text-nt-purple"
            onClick={() => navigate(`/module/${moduleId}/level/${levelId}`, { state: { module, level } })}
          >
            <ArrowLeft className="size-4" aria-hidden="true" />
            Volver a actividades
          </button>
          <label className="relative min-w-0 flex-1 md:max-w-lg">
            <Search
              className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
              aria-hidden="true"
            />
            <span className="sr-only">Buscar</span>
            <input
              type="search"
              placeholder="Buscar teoría"
              className="h-12 w-full rounded-nt-button border border-white/80 bg-white/90 pl-11 pr-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />
          </label>
        </header>
      }
      rightPanel={
        <div className="space-y-5">
          <ProgressCard
            title="Teoría"
            subtitle={hasLocalLessons ? `${lessons.length} lecciones` : level.status}
            value={overviewProgress}
            totalLabel={level.name}
            tone="green"
          />
          <NeoCard
            title="Tip de NEO"
            message={
              hasLocalLessons
                ? "Completa las lecciones en orden y luego pasa a práctica."
                : "Lee con calma y después pasa a práctica para reforzar lo aprendido."
            }
            actionLabel={hasLocalLessons ? "Empezar teoría" : "Ir a práctica"}
            onAction={() => (firstLesson ? goToLesson(firstLesson) : goToPractice())}
          />
        </div>
      }
    >
      <section className="overflow-hidden rounded-nt-card border border-white/80 bg-white/90 shadow-nt-card backdrop-blur">
        <div className="p-5">
          <span className="inline-flex rounded-full bg-nt-green px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-green/25">
            {module.title}
          </span>
          <h1 className="mt-4 text-3xl font-black leading-tight text-nt-text-primary md:text-4xl">
            Teoría - {level.name}
          </h1>
          <p className="mt-2 text-sm font-black text-nt-blue">{level.backendTitle}</p>
        </div>
      </section>

      {hasLocalLessons ? (
        <>
          <section className="rounded-nt-card border border-white/80 bg-white/90 p-5 shadow-nt-card backdrop-blur">
            <div className="mb-5 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
              <div className="flex items-center gap-3">
                <div className="grid size-12 place-items-center rounded-[20px] bg-nt-green/15 text-green-700">
                  <BookOpen className="size-6" aria-hidden="true" />
                </div>
                <div>
                  <h2 className="text-xl font-black text-nt-text-primary">Lecciones de teoría</h2>
                  <p className="text-sm font-semibold text-nt-text-secondary">
                    Avanza por cada concepto antes de practicar.
                  </p>
                </div>
              </div>
              <span className="inline-flex w-fit rounded-full bg-nt-blue-light/20 px-3 py-1 text-xs font-black text-nt-blue">
                {lessons.length} lecciones
              </span>
            </div>

            <div className="grid gap-4">
              {lessons.map((lesson, index) => (
                <button
                  key={lesson.id}
                  type="button"
                  onClick={() => goToLesson(lesson)}
                  className="group grid w-full gap-4 rounded-[28px] border border-white/80 bg-white/90 p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-nt-blue-light hover:shadow-nt-card md:grid-cols-[auto_1fr_auto]"
                >
                  <div className="flex items-center gap-3 md:block">
                    <div className="grid size-14 shrink-0 place-items-center rounded-full bg-gradient-to-br from-nt-blue to-nt-purple text-lg font-black text-white shadow-lg shadow-nt-blue/20">
                      {index + 1}
                    </div>
                    {index < lessons.length - 1 ? (
                      <div className="ml-7 hidden h-10 border-l-2 border-dashed border-nt-blue-light/60 md:block" />
                    ) : null}
                  </div>

                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2">
                      <h3 className="text-lg font-black text-nt-text-primary">{lesson.title}</h3>
                      <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-2.5 py-1 text-xs font-black text-green-700">
                        <Clock className="size-3.5" aria-hidden="true" />
                        {lesson.duration}
                      </span>
                    </div>
                    <p className="mt-2 text-sm font-semibold leading-6 text-nt-text-secondary">
                      {lesson.summary}
                    </p>
                    <div className="mt-4 h-2 rounded-full bg-nt-sky">
                      <div
                        className="h-full rounded-full bg-gradient-to-r from-nt-green to-nt-blue"
                        style={{ width: `${Math.round(((index + 1) / lessons.length) * 100)}%` }}
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-between gap-3 md:justify-end">
                    <span className="inline-flex items-center gap-1 rounded-full bg-nt-sky px-3 py-1 text-xs font-black text-nt-blue">
                      <CheckCircle2 className="size-4" aria-hidden="true" />
                      Disponible
                    </span>
                    <span className="grid size-11 place-items-center rounded-full bg-nt-blue text-white shadow-lg shadow-nt-blue/20 transition group-hover:bg-nt-purple">
                      <PlayCircle className="size-5" aria-hidden="true" />
                    </span>
                  </div>
                </button>
              ))}
            </div>
          </section>

          <section className="grid gap-3 md:grid-cols-3">
            <div className="rounded-[24px] border border-white/80 bg-white/85 p-4 shadow-sm backdrop-blur">
              <p className="text-xs font-black uppercase tracking-wide text-nt-blue">Ruta</p>
              <p className="mt-1 text-lg font-black text-nt-text-primary">{level.name}</p>
            </div>
            <div className="rounded-[24px] border border-white/80 bg-white/85 p-4 shadow-sm backdrop-blur">
              <p className="text-xs font-black uppercase tracking-wide text-nt-blue">Objetivo</p>
              <p className="mt-1 text-lg font-black text-nt-text-primary">Comprender antes de practicar</p>
            </div>
            <button
              type="button"
              onClick={() => goToLesson(firstLesson)}
              className="rounded-[24px] bg-gradient-to-r from-nt-blue to-nt-purple p-4 text-left text-white shadow-lg shadow-nt-blue/20 transition hover:-translate-y-0.5"
            >
              <p className="text-xs font-black uppercase tracking-wide text-white/80">Siguiente paso</p>
              <p className="mt-1 text-lg font-black">Empezar lección</p>
            </button>
          </section>
        </>
      ) : (
        <section className="rounded-nt-card border border-white/80 bg-white/90 p-5 shadow-nt-card backdrop-blur">
          <div className="mb-4 flex items-center gap-3">
            <div className="grid size-11 place-items-center rounded-[18px] bg-nt-green/15 text-green-700">
              <BookOpen className="size-5" aria-hidden="true" />
            </div>
            <div>
              <h2 className="text-xl font-black text-nt-text-primary">Contenido de teoría</h2>
              <p className="text-sm font-semibold text-nt-text-secondary">
                Revisa los conceptos antes de practicar.
              </p>
            </div>
          </div>

          {isLoading ? (
            <div className="h-40 animate-pulse rounded-[18px] bg-nt-sky/70" />
          ) : theoryHtml ? (
            <div
              className="prose prose-sm max-w-none text-nt-text-primary"
              dangerouslySetInnerHTML={{ __html: theoryHtml }}
            />
          ) : (
            <div className="rounded-[18px] bg-nt-sky/70 p-4 text-sm font-semibold leading-6 text-nt-text-primary">
              {isUsingFallback
                ? "No se pudo cargar la teoría del servidor. Revisa el contenido con tu docente antes de practicar."
                : "La teoría de este nivel aún no está disponible."}
            </div>
          )}

          <div className="mt-5 flex flex-wrap gap-3">
            <PrimaryButton
              type="button"
              onClick={() => navigate(`/module/${moduleId}/level/${levelId}`, { state: { module, level } })}
              className="bg-slate-100 text-nt-text-primary shadow-none hover:bg-slate-200"
            >
              Volver a actividades
            </PrimaryButton>
            <PrimaryButton type="button" onClick={goToPractice}>
              Ir a práctica
            </PrimaryButton>
          </div>
        </section>
      )}
    </StudentLayout>
  );
}

export default Theory;
