import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { AlertTriangle, ArrowLeft, ArrowRight, BookOpen, Check, CheckCircle2, Clock3, GraduationCap, Lightbulb, Sparkles, Target } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import { Button } from "@/components/ui/button";
import { getTheoryLesson, getTheoryLessons } from "../services/learningService";
import { getModuleProgress, markTheoryCompleted } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const parseLessonContent = (contentHtml = "", { hideTip = false } = {}) => {
  if (!contentHtml || typeof DOMParser === "undefined") {
    return { contentHtml, learningObjectives: [], tipText: null };
  }

  const document = new DOMParser().parseFromString(contentHtml, "text/html");
  const objectiveList = document.querySelector(".lesson-list");
  const learningObjectives = objectiveList
    ? Array.from(objectiveList.querySelectorAll(":scope > li"))
        .map((item) => item.textContent?.trim())
        .filter(Boolean)
    : [];
  const tipText = document.querySelector(".tip-box")?.textContent?.trim() || null;

  if (objectiveList) {
    const objectiveSection = objectiveList.closest("section");
    const heading = objectiveSection?.querySelector("h2, h3");

    if (objectiveSection && heading) objectiveSection.remove();
    else objectiveList.remove();
  }

  // Visual-only rule: keep backend content unchanged and omit tip boxes only
  // when the current lesson is the introductory welcome lesson.
  if (hideTip) {
    document.querySelectorAll(".tip-box").forEach((tip) => tip.remove());
  }

  return { contentHtml: document.body.innerHTML, learningObjectives, tipText };
};

const validPercentage = (value) => {
  if (value === null || value === undefined || value === "") return null;
  return Math.min(100, Math.max(0, Number(value) || 0));
};

const assetUrl = (asset) => {
  if (!asset) return null;
  return String(asset).startsWith("/") ? asset : `/assets/${asset}`;
};

function StructuredSection({ section }) {
  const visual = assetUrl(section.visual);
  if (section.type === "main_concept" || section.type === "example") {
    const Icon = section.type === "main_concept" ? Target : BookOpen;
    return <section className="grid items-center gap-4 rounded-[22px] border border-blue-100 bg-gradient-to-br from-white to-sky-50 p-4 sm:grid-cols-[minmax(0,1fr)_140px]"><div><h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary"><Icon className="size-5 text-nt-blue" />{section.title}</h2>{section.text && <p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{section.text}</p>}</div>{visual && <img src={visual} alt="" className="mx-auto size-32 object-contain drop-shadow-md" />}{Array.isArray(section.items) && <div className="grid gap-2 sm:col-span-2 sm:grid-cols-3">{section.items.map((item) => <article key={item.label} className="rounded-2xl border border-amber-100 bg-white/85 px-4 py-3 text-center shadow-sm"><strong className="text-xl font-black text-nt-purple">{item.label}</strong><p className="mt-1 text-xs font-semibold leading-5 text-slate-600">{item.description}</p></article>)}</div>}</section>;
  }
  if (section.type === "important_idea") {
    return <aside className="rounded-[22px] border border-amber-200 bg-amber-50 p-4"><h2 className="flex items-center gap-2 text-base font-black text-amber-900"><Lightbulb className="size-5 text-amber-500" />{section.title}</h2><p className="mt-2 text-sm font-semibold leading-6 text-amber-900">{section.text}</p></aside>;
  }
  if (section.type === "common_mistakes" && Array.isArray(section.items)) {
    return <section><h2 className="flex items-center gap-2 text-lg font-black text-red-700"><AlertTriangle className="size-5" />{section.title}</h2><div className="mt-3 grid gap-2">{section.items.map((item) => <div key={item} className="flex gap-3 rounded-2xl border border-red-100 bg-red-50 px-4 py-3"><span className="font-black text-red-500">×</span><p className="text-sm font-semibold text-slate-700">{item}</p></div>)}</div></section>;
  }
  if (section.type === "reflection") {
    return <section className="rounded-[22px] border border-violet-100 bg-gradient-to-r from-violet-50 to-sky-50 p-4"><h2 className="text-base font-black text-nt-purple">{section.title}</h2><p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{section.text}</p></section>;
  }
  if (section.type === "observe") {
    return <section className="grid items-center gap-4 rounded-[22px] border border-sky-100 bg-sky-50 p-4 sm:grid-cols-[minmax(0,1fr)_180px]"><div><h2 className="text-lg font-black text-nt-blue">{section.title}</h2><p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{section.text}</p></div>{section.image && <img src={assetUrl(section.image)} alt="" className="mx-auto h-32 w-full object-contain" />}</section>;
  }
  if (section.type === "summary" && Array.isArray(section.items)) {
    return <section className="rounded-[22px] border border-emerald-100 bg-emerald-50 p-4"><h2 className="text-lg font-black text-emerald-800">{section.title}</h2><div className="mt-3 grid gap-2">{section.items.map((item) => <div key={item} className="flex items-start gap-2"><span className="mt-0.5 grid size-5 shrink-0 place-items-center rounded-full bg-emerald-500 text-white"><Check className="size-3" strokeWidth={3} /></span><p className="text-sm font-semibold text-slate-700">{item}</p></div>)}</div></section>;
  }
  return null;
}

function LessonSidebar({ lesson, lessons, position, level, progress, neoTip, nextLessonData, heroContent }) {
  const totalLessons = progress?.total_lessons ?? lessons.length;
  const completedLessons = progress?.completed_lessons
    ?? progress?.theory_completed_lessons
    ?? (progress?.theory_completed === true ? totalLessons : null);
  const theoryPercentage = validPercentage(
    progress?.theory_progress_percentage
      ?? (completedLessons !== null && totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : null)
  );
  const lessonProgressData = lesson?.lessonProgress ?? lesson?.lesson_progress;
  const completedFlag = lesson?.completed ?? lesson?.is_completed ?? lessonProgressData?.completed ?? lessonProgressData?.is_completed;
  const lessonStatus = lesson?.status ?? lessonProgressData?.status;
  const lessonPercentage = validPercentage(
    lesson?.progress_percentage
      ?? lesson?.progress
      ?? lesson?.completion_percentage
      ?? lessonProgressData?.progress_percentage
      ?? lessonProgressData?.progress
      ?? (completedFlag === true || String(lessonStatus ?? "").toUpperCase() === "COMPLETADO" ? 100 : null)
  );
  const estimatedMinutes = heroContent?.time ?? lesson?.estimated_minutes ?? lesson?.estimated_duration_minutes;
  const completionPoints = heroContent?.points ?? lesson?.completion_points ?? lesson?.points;

  return (
    <div className="space-y-5">
      <section className="rounded-[26px] border border-white bg-white p-4 shadow-[0_14px_34px_rgba(30,58,138,0.11)]">
        <img src="/assets/teoria.png" alt="" className="mx-auto h-28 w-full object-contain drop-shadow-[0_12px_18px_rgba(30,58,138,0.16)]" />
        <h2 className="mt-1 text-center text-lg font-black text-nt-blue">Tu progreso en teoría</h2>
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
            <p className="text-xs font-semibold text-nt-text-secondary">
              {completedLessons === null ? `— / ${totalLessons}` : `${completedLessons} / ${totalLessons}`} lecciones completadas
            </p>
            <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-200">
              <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-nt-green" style={{ width: `${theoryPercentage ?? 0}%` }} />
            </div>
          </div>
        </div>
      </section>

      <section className="rounded-[26px] border border-violet-100 bg-gradient-to-br from-white via-violet-50 to-sky-50 p-4 shadow-[0_14px_34px_rgba(76,29,149,0.10)]">
        <div className="flex items-center gap-2">
          <span className="grid size-9 place-items-center rounded-[12px] bg-violet-100 text-violet-700"><Sparkles className="size-[18px]" /></span>
          <div><p className="text-xs font-bold text-nt-text-secondary">Lección actual</p><h2 className="text-base font-black text-nt-text-primary">{position}/{lessons.length}</h2></div>
        </div>
        <h3 className="mt-3 text-sm font-black leading-5 text-nt-text-primary">{lesson.title}</h3>
        <dl className="mt-3 grid gap-2 text-xs">
          {lessonPercentage !== null && <div className="flex items-center justify-between gap-3"><dt className="text-nt-text-secondary">Progreso</dt><dd className="font-black text-nt-blue">{lessonPercentage}%</dd></div>}
          {lessonPercentage === null && lessonStatus && <div className="flex items-center justify-between gap-3"><dt className="text-nt-text-secondary">Estado</dt><dd className="font-black text-nt-blue">{lessonStatus}</dd></div>}
          <div className="flex items-center justify-between gap-3"><dt className="flex items-center gap-1.5 text-nt-text-secondary"><GraduationCap className="size-3.5" />Nivel</dt><dd className="font-black text-nt-text-primary">{level.name}</dd></div>
          <div className="flex items-center justify-between gap-3"><dt className="text-nt-text-secondary">Contenido</dt><dd className="font-black text-nt-text-primary">Teoría</dd></div>
          {estimatedMinutes !== null && estimatedMinutes !== undefined && <div className="flex items-center justify-between gap-3"><dt className="flex items-center gap-1.5 text-nt-text-secondary"><Clock3 className="size-3.5" />Tiempo</dt><dd className="font-black text-nt-text-primary">{typeof estimatedMinutes === "string" ? estimatedMinutes : `${estimatedMinutes} min`}</dd></div>}
          {completionPoints !== null && completionPoints !== undefined && <div className="flex items-center justify-between gap-3"><dt className="text-nt-text-secondary">Puntos</dt><dd className="font-black text-emerald-700">+{completionPoints}</dd></div>}
        </dl>
      </section>

      <aside className="relative min-h-[156px] overflow-hidden rounded-3xl border border-violet-200 bg-gradient-to-br from-white via-violet-50 to-sky-100 p-4 shadow-[0_14px_34px_rgba(99,102,241,0.14)]">
          <span className="absolute left-4 top-3 text-sm text-violet-300/80" aria-hidden="true">✦</span>
          <span className="absolute right-5 top-4 text-xs text-amber-300/80" aria-hidden="true">✦</span>
          <div className="relative z-10 max-w-[58%] pt-3">
            <h2 className="text-lg font-black text-nt-text-primary">{neoTip?.title ?? "NEO dice"}</h2>
            <p className="mt-2 line-clamp-4 text-xs font-semibold leading-5 text-slate-700">{neoTip?.text ?? "Cuando repartes una pizza, una barra de chocolate o una torta en partes iguales, ya estás usando fracciones."}</p>
          </div>
          <div className="absolute -bottom-10 -right-8 size-32 rounded-full bg-white/50 blur-2xl" />
          <img src={assetUrl(neoTip?.image) ?? "/assets/neo_leccion.png"} alt="NEO" className="absolute bottom-0 right-1 z-10 size-38 object-contain drop-shadow-[0_12px_18px_rgba(76,29,149,0.18)]" />
      </aside>
      {nextLessonData && (
        <aside className="relative overflow-hidden rounded-3xl border border-sky-100 bg-gradient-to-br from-white to-sky-50 p-4 shadow-sm">
          <div className="max-w-[68%]"><p className="text-xs font-black text-nt-blue">Próxima lección</p><h2 className="mt-1 text-sm font-black text-nt-text-primary">{nextLessonData.title}</h2><p className="mt-2 text-xs font-semibold leading-5 text-nt-text-secondary">{nextLessonData.description}</p></div>
          <img src="/assets/libro.png" alt="" className="absolute bottom-1 right-1 size-20 object-contain" />
        </aside>
      )}
    </div>
  );
}

function TheoryLesson() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId, lessonId } = useParams();
  const [lesson, setLesson] = useState(null);
  const [lessons, setLessons] = useState([]);
  const [moduleProgress, setModuleProgress] = useState(null);
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

  useEffect(() => {
    if (!studentId) return undefined;
    let active = true;
    getModuleProgress(studentId, levelId)
      .then((progressData) => {
        if (active) setModuleProgress(progressData);
      })
      .catch(() => {
        if (active) setModuleProgress(null);
      });
    return () => {
      active = false;
    };
  }, [studentId, levelId]);

  const lessonIndex = useMemo(
    () => lessons.findIndex((item) => String(item.id) === String(lessonId)),
    [lessons, lessonId]
  );
  const previousLesson = lessonIndex > 0 ? lessons[lessonIndex - 1] : null;
  const nextLesson = lessonIndex >= 0 ? lessons[lessonIndex + 1] : null;
  const position = lessonIndex >= 0 ? lessonIndex + 1 : 0;
  const routeProgress = lessons.length ? Math.round((position / lessons.length) * 100) : 0;
  const normalizedLessonTitle = String(lesson?.title ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();
  const isWelcomeLesson = position === 1
    || normalizedLessonTitle.includes("bienvenida al mundo de las fracciones")
    || normalizedLessonTitle.includes("bienvenido al mundo de las fracciones");
  // Objectives are parsed from backend-provided content_html. The initial database
  // content is hardcoded by backend-spring-boot/.../config/TheoryLessonSeeder.java.
  const parsedLessonContent = useMemo(
    () => parseLessonContent(lesson?.content_html, { hideTip: isWelcomeLesson }),
    [lesson?.content_html, isWelcomeLesson]
  );
  const webContent = useMemo(() => {
    const value = lesson?.web_content_json ?? lesson?.webContent;
    if (!value) return null;
    if (typeof value === "object") return value;
    try {
      return JSON.parse(value);
    } catch {
      return null;
    }
  }, [lesson?.web_content_json, lesson?.webContent]);
  const webSections = Array.isArray(webContent?.sections) ? webContent.sections : [];
  const learningObjectivesSection = webSections.find((section) => section.type === "learning_objectives");
  const learningObjectives = Array.isArray(learningObjectivesSection?.items)
    ? learningObjectivesSection.items.filter(Boolean)
    : parsedLessonContent.learningObjectives;
  const neoTip = webSections.find((section) => section.type === "neo_tip") ?? null;
  const contentSections = webSections.filter((section) => !["learning_objectives", "neo_tip"].includes(section.type));
  const heroContent = webContent?.hero ?? null;

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true },
    { label: "Mis Logros", onClick: () => navigate("/achievements") },
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
        <LessonSidebar lesson={lesson} lessons={lessons} position={position} level={level} progress={moduleProgress} neoTip={neoTip} nextLessonData={webContent?.nextLesson} heroContent={heroContent} />
      }
    >
      <article className="rounded-nt-card border border-white/85 bg-white/95 p-3 shadow-nt-card sm:p-4 lg:p-5">
        <section className="relative overflow-hidden rounded-[24px] border border-blue-100 bg-gradient-to-br from-white via-blue-50 to-sky-50 px-4 py-4 shadow-[0_10px_26px_rgba(59,130,246,0.10)] sm:px-5">
          <div className="pointer-events-none absolute -right-16 -top-16 size-56 rounded-full bg-sky-200/25 blur-3xl" />
          <div className="relative grid items-center gap-4 md:grid-cols-[minmax(0,1fr)_minmax(240px,42%)] md:gap-5">
            <div className="min-w-0">
              <span className="inline-flex rounded-full bg-violet-100 px-3 py-1 text-xs font-black text-violet-700">
                {heroContent?.badge ?? `Lección ${position}`}
              </span>
              <h1 className="mt-2.5 text-2xl font-black leading-tight text-nt-text-primary sm:text-3xl">
                {heroContent?.title ?? lesson.title}
              </h1>
              {(heroContent?.subtitle ?? lesson.subtitle) && <p className="mt-1.5 text-sm font-black text-nt-purple sm:text-base">{heroContent?.subtitle ?? lesson.subtitle}</p>}
              {(heroContent?.description ?? lesson.summary) && (
                <p className="mt-2 max-w-2xl text-sm font-semibold leading-5 text-slate-600">{heroContent?.description ?? lesson.summary}</p>
              )}
              {heroContent && (heroContent.time || heroContent.points !== undefined) && (
                <div className="mt-3 flex flex-wrap gap-3 text-xs">
                  {heroContent.time && <span className="rounded-xl border border-blue-100 bg-white/80 px-3 py-2 font-black text-nt-blue">{heroContent.time}</span>}
                  <span className="rounded-xl border border-violet-100 bg-white/80 px-3 py-2 font-black text-nt-purple">{position} de {lessons.length}</span>
                  {heroContent.points !== null && heroContent.points !== undefined && <span className="rounded-xl border border-amber-100 bg-white/80 px-3 py-2 font-black text-amber-700">+{heroContent.points} pts</span>}
                </div>
              )}
            </div>
            <div className="order-first flex min-h-[150px] items-center justify-center md:order-none md:min-h-[180px]">
              <img
                src={assetUrl(heroContent?.image) ?? "/assets/lecciones_saludo.png"}
                alt=""
                className="max-h-[205px] w-full object-contain drop-shadow-[0_16px_22px_rgba(30,58,138,0.18)]"
              />
            </div>
          </div>
        </section>

        {learningObjectives.length > 0 && (
          <section className="mt-4 px-1 pb-1">
            <div className="mb-3 flex items-center gap-2.5">
              <BookOpen className="size-5 shrink-0 text-emerald-700" aria-hidden="true" />
              <h2 className="text-xl font-black text-nt-text-primary">{learningObjectivesSection?.title ?? "Hoy aprenderás"}</h2>
            </div>
            <div className="grid gap-2">
              {learningObjectives.map((objective) => (
                <article
                  key={objective}
                  className="flex items-center gap-3 rounded-2xl border border-emerald-100 bg-emerald-50 px-4 py-3 shadow-sm transition duration-200 hover:-translate-y-0.5 hover:shadow-md"
                >
                  <span className="grid size-7 shrink-0 place-items-center rounded-full bg-emerald-500 text-white shadow-sm">
                    <Check className="size-4" strokeWidth={3} aria-hidden="true" />
                  </span>
                  <p className="text-sm font-bold leading-5 text-slate-700">{objective}</p>
                </article>
              ))}
            </div>
          </section>
        )}

        {webContent && contentSections.length > 0 && (
          <div className="mt-5 grid gap-4">
            {contentSections.map((section, index) => (
              <StructuredSection key={`${section.type}-${index}`} section={section} />
            ))}
          </div>
        )}

        {!webContent && !isWelcomeLesson && parsedLessonContent.contentHtml.trim() && (
        <div
          className="mt-5 grid gap-4 text-base font-semibold leading-7 text-slate-700
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
          dangerouslySetInnerHTML={{ __html: parsedLessonContent.contentHtml }}
        />
        )}
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
