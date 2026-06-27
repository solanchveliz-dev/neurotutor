import { ArrowLeft, ArrowRight, BookOpen, CheckCircle2 } from "lucide-react";
import { useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import NeoCard from "../components/student/NeoCard";
import PrimaryButton from "../components/student/PrimaryButton";
import ProgressCard from "../components/student/ProgressCard";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import { modulesData } from "../data/modulesData";
import { getTheoryLessons } from "../data/theoryLessons";
import { markTheoryCompleted } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const numericFallbackMap = {
  1: "fracciones",
  2: "decimales",
  3: "porcentajes",
};

const lessonVisuals = {
  "que-es-una-fraccion": {
    title: "¿Qué es una fracción? 🤔",
    text: "Una fracción es una forma de representar partes iguales de un todo.",
    formula: "2/4 → Dos cuartos",
    explanation: "En esta pizza, el entero está dividido en 4 partes iguales y se han tomado 2.",
    remember: "Las partes deben ser exactamente iguales.",
    image: "/assets/pizza.png",
    examples: [
      { label: "1/2", text: "Una mitad" },
      { label: "3/4", text: "Tres cuartos" },
      { label: "1/4", text: "Un cuarto" },
    ],
    type: "image",
  },
  "numerador-y-denominador": {
    title: "Numerador y denominador",
    text: "El numerador dice cuántas partes tomas y el denominador en cuántas partes se divide el todo.",
    formula: "3/5 → 3 partes de 5",
    explanation: "El número de arriba cuenta las partes tomadas. El de abajo cuenta las partes iguales del entero.",
    remember: "Numerador arriba, denominador abajo.",
    examples: [
      { label: "2/6", text: "2 tomadas" },
      { label: "5/8", text: "8 partes" },
      { label: "1/3", text: "1 de 3" },
    ],
    type: "numerator",
  },
  "fracciones-propias": {
    title: "Fracciones propias",
    text: "Una fracción propia tiene el numerador menor que el denominador.",
    formula: "2/5 < 1",
    explanation: "Representa menos de un entero porque todavía faltan partes para completarlo.",
    remember: "Si el número de arriba es menor, la fracción es propia.",
    examples: [
      { label: "1/3", text: "Menor que 1" },
      { label: "2/5", text: "Menor que 1" },
      { label: "7/10", text: "Menor que 1" },
    ],
    type: "proper",
  },
  "fracciones-impropias": {
    title: "Fracciones impropias",
    text: "Una fracción impropia puede representar un entero completo o más.",
    formula: "5/4 > 1",
    explanation: "Hay suficientes partes para formar un entero y todavía puede sobrar una parte.",
    remember: "Si el numerador es mayor o igual, puede formar uno o más enteros.",
    examples: [
      { label: "4/4", text: "Un entero" },
      { label: "5/4", text: "Más de 1" },
      { label: "7/3", text: "Más de 1" },
    ],
    type: "improper",
  },
};

function LessonVisual({ visual }) {
  if (visual.type === "image") {
    return (
      <div className="grid min-h-64 place-items-center rounded-[32px] bg-gradient-to-br from-orange-50 to-yellow-50 p-5">
        <img
          src={visual.image}
          alt="Pizza dividida en fracciones"
          className="max-h-72 w-full max-w-sm object-contain drop-shadow-2xl"
        />
      </div>
    );
  }

  if (visual.type === "numerator") {
    return (
      <div className="relative grid min-h-64 place-items-center rounded-[32px] bg-gradient-to-br from-blue-50 to-violet-50 p-6">
        <div className="rounded-[30px] bg-white p-7 text-center shadow-xl shadow-blue-100">
          <div className="text-6xl font-black leading-none text-nt-blue">3</div>
          <div className="my-3 h-2 w-28 rounded-full bg-nt-purple" />
          <div className="text-6xl font-black leading-none text-nt-purple">5</div>
        </div>
        <span className="absolute left-6 top-8 rounded-full bg-white px-4 py-2 text-xs font-black text-nt-blue shadow-lg">
          Numerador
        </span>
        <span className="absolute bottom-8 right-6 rounded-full bg-white px-4 py-2 text-xs font-black text-nt-purple shadow-lg">
          Denominador
        </span>
      </div>
    );
  }

  if (visual.type === "proper") {
    return (
      <div className="grid min-h-64 place-items-center rounded-[32px] bg-gradient-to-br from-green-50 to-sky-50 p-6">
        <div className="grid grid-cols-5 gap-3">
          {[0, 1, 2, 3, 4].map((item) => (
            <div
              key={item}
              className={`h-28 w-12 rounded-full border-4 border-white shadow-lg ${
                item < 2 ? "bg-nt-green" : "bg-white"
              }`}
            />
          ))}
        </div>
        <p className="mt-4 rounded-full bg-white px-5 py-2 text-base font-black text-green-700 shadow-lg">
          2 de 5 partes
        </p>
      </div>
    );
  }

  return (
    <div className="grid min-h-64 place-items-center rounded-[32px] bg-gradient-to-br from-orange-50 to-violet-50 p-6">
      <div className="flex items-center gap-5">
        <div className="grid size-24 place-items-center rounded-full border-8 border-orange-300 bg-orange-400 text-xl font-black text-white shadow-xl">
          4/4
        </div>
        <div className="grid size-24 place-items-center rounded-full border-8 border-orange-300 bg-gradient-to-r from-orange-400 from-25% to-white to-25% text-xl font-black text-orange-700 shadow-xl">
          1/4
        </div>
      </div>
      <p className="mt-4 rounded-full bg-white px-5 py-2 text-base font-black text-orange-700 shadow-lg">
        5/4 = 1 entero y 1/4
      </p>
    </div>
  );
}

function TheoryLesson() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId, lessonId } = useParams();
  const [completionError, setCompletionError] = useState("");

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
  const foundLessonIndex = lessons.findIndex((lesson) => lesson.id === lessonId);
  const lessonIndex = foundLessonIndex >= 0 ? foundLessonIndex : -1;
  const lesson = lessonIndex >= 0 ? lessons[lessonIndex] : null;
  const previousLesson = lessons[lessonIndex - 1];
  const nextLesson = lessons[lessonIndex + 1];
  const progress = lessons.length ? Math.round(((lessonIndex + 1) / lessons.length) * 100) : 0;
  const lessonVisual = lessonVisuals[lessonId] ?? {
    title: lesson?.title ?? "Lección",
    text: lesson?.summary ?? "Revisa esta idea clave antes de practicar.",
    formula: lesson?.example ?? "Observa, piensa y practica.",
    explanation: "Mira el ejemplo visual y conecta la idea con lo que ya sabes.",
    remember: lesson?.checkpoint ?? "Avanza con calma y revisa cada ejemplo.",
    examples: [
      { label: "Idea", text: "Observa" },
      { label: "Ejemplo", text: "Relaciona" },
      { label: "Práctica", text: "Aplica" },
    ],
    type: "proper",
  };

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true, onClick: () => navigate(`/module/${moduleId}`, { state: { module } }) },
    { label: "Mis logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const goToLesson = (targetLesson) => {
    navigate(`/module/${moduleId}/level/${levelId}/theory/${targetLesson.id}`, {
      state: { module, level },
    });
  };

  const goToPractice = async () => {
    const studentId = getStudentId();
    const progressModuloId = levelId ?? moduleId;

    if (studentId && progressModuloId) {
      try {
        setCompletionError("");
        await markTheoryCompleted(studentId, progressModuloId);
      } catch (error) {
        console.warn("No se pudo marcar la teoria como completada.", error);
        setCompletionError("No pudimos guardar la teoría como completada. Revisa tu conexión e intenta nuevamente.");
        return;
      }
    }

    navigate(`/module/${moduleId}/level/${levelId}/practice`, {
      state: { module, level },
    });
  };

  return (
    <StudentLayout
      className="bg-[linear-gradient(180deg,rgba(255,255,255,0.08)_0%,rgba(255,255,255,0.04)_100%),url('/assets/fondo_diagnostic.png')] bg-cover bg-center bg-fixed"
      contentClassName="mx-auto w-full max-w-5xl space-y-4"
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <header className="w-full rounded-[28px] bg-white/65 px-4 py-3 shadow-sm backdrop-blur-md">
          <div className="mb-3 flex items-center justify-between gap-3">
            <button
              type="button"
              className="inline-flex items-center gap-2 rounded-full bg-white px-4 py-2 text-sm font-black text-nt-blue shadow-sm transition hover:text-nt-purple"
              onClick={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}
            >
              <ArrowLeft className="size-4" aria-hidden="true" />
              Lecciones
            </button>
            <h1 className="text-center text-lg font-black text-nt-text-primary md:text-2xl">
              Teoría - {level.name}
            </h1>
            <span className="rounded-full bg-nt-blue px-4 py-2 text-sm font-black text-white shadow-lg shadow-nt-blue/20">
              {lesson ? `${lessonIndex + 1}/${lessons.length}` : "0/0"}
            </span>
          </div>
          <div className="flex items-center gap-3">
            <div className="h-3 flex-1 overflow-hidden rounded-full bg-white shadow-inner">
              <div
                className="h-full rounded-full bg-gradient-to-r from-nt-green via-nt-blue to-nt-purple transition-all"
                style={{ width: `${progress}%` }}
              />
            </div>
            <span className="text-sm font-black text-nt-blue">{progress}%</span>
          </div>
        </header>
      }
      rightPanel={
        <div className="space-y-5">
          <LearningProgressPanel studentId={getStudentId()} moduloId={levelId ?? moduleId} />
          <ProgressCard
            title="Lección actual"
            subtitle={lesson ? `${lessonIndex + 1}/${lessons.length}` : "Sin lecciones"}
            value={progress}
            totalLabel={level.name}
            tone="green"
          />
          <NeoCard
            title="Tip de NEO"
            message="Lee una lección a la vez. Cuando termines todas, pasa a practicar."
            actionLabel={nextLesson ? "Siguiente lección" : "Ir a práctica"}
            onAction={() => (nextLesson ? goToLesson(nextLesson) : goToPractice())}
          />
        </div>
      }
    >
      {completionError && (
        <div className="rounded-[20px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">
          {completionError}
        </div>
      )}
      {!lesson ? (
        <section className="rounded-nt-card border border-white/80 bg-white/90 p-6 text-center shadow-nt-card backdrop-blur">
          <h1 className="text-2xl font-black text-nt-text-primary">Lección no encontrada</h1>
          <PrimaryButton
            type="button"
            className="mt-5"
            onClick={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}
          >
            Volver a teoría
          </PrimaryButton>
        </section>
      ) : (
        <>
          <section className="rounded-[34px] border border-white/80 bg-white/92 p-5 shadow-nt-card backdrop-blur md:p-6">
            <div className="mb-5 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
              <div>
                <span className="inline-flex rounded-full bg-nt-green px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-green/25">
                  Lección {lessonIndex + 1}
                </span>
                <h2 className="mt-3 text-3xl font-black leading-tight text-nt-text-primary md:text-4xl">
                  {lessonVisual.title}
                </h2>
              </div>
              <div className="flex w-fit items-center gap-2 rounded-full bg-nt-sky px-4 py-2 text-sm font-black text-nt-blue">
                <BookOpen className="size-4" aria-hidden="true" />
                {lesson.title}
              </div>
            </div>

            <div className="grid items-center gap-6 lg:grid-cols-[0.95fr_1.05fr]">
              <div className="space-y-4">
                <div className="rounded-[30px] bg-gradient-to-br from-blue-50 to-white p-5 shadow-inner">
                  <p className="text-xl font-black leading-8 text-nt-text-primary md:text-2xl">
                    {lessonVisual.text}
                  </p>
                </div>

                <div className="rounded-[28px] border border-violet-100 bg-violet-50 p-5 text-center shadow-sm">
                  <p className="text-xs font-black uppercase tracking-wide text-violet-600">Ejemplo visual</p>
                  <p className="mt-2 text-3xl font-black text-violet-700 md:text-4xl">
                    {lessonVisual.formula}
                  </p>
                </div>

                <p className="rounded-[24px] bg-white p-4 text-sm font-bold leading-6 text-nt-text-secondary shadow-sm">
                  {lessonVisual.explanation}
                </p>
              </div>

              <LessonVisual visual={lessonVisual} />
            </div>

            <div className="mt-5 grid gap-4 lg:grid-cols-[0.85fr_1.15fr]">
              <article className="flex items-center gap-4 rounded-[28px] border border-yellow-200 bg-yellow-50 p-4 shadow-sm">
                <div className="grid size-14 shrink-0 place-items-center rounded-[20px] bg-white shadow-md">
                  <img src="/assets/icon_hint.webp" alt="" className="size-9 object-contain" />
                </div>
                <div>
                  <h3 className="text-lg font-black text-yellow-700">¡Recuerda!</h3>
                  <p className="text-sm font-bold leading-6 text-yellow-800">{lessonVisual.remember}</p>
                </div>
              </article>

              <article className="rounded-[28px] border border-white/80 bg-nt-sky/70 p-4">
                <h3 className="text-sm font-black uppercase tracking-wide text-nt-blue">Más ejemplos</h3>
                <div className="mt-3 grid gap-3 sm:grid-cols-3">
                  {lessonVisual.examples.map((example) => (
                    <div key={example.label} className="rounded-[22px] bg-white p-3 text-center shadow-sm">
                      <p className="text-2xl font-black text-nt-text-primary">{example.label}</p>
                      <p className="mt-1 text-xs font-bold text-nt-text-secondary">{example.text}</p>
                    </div>
                  ))}
                </div>
              </article>
            </div>
          </section>

          <section className="grid gap-3 md:grid-cols-4">
            {lessons.map((item, index) => (
              <div
                key={item.id}
                className={`rounded-[22px] border p-3 text-sm font-black ${
                  index <= lessonIndex
                    ? "border-green-200 bg-green-50 text-green-700"
                    : "border-white/80 bg-white/80 text-nt-text-secondary"
                }`}
              >
                <CheckCircle2 className="mb-2 size-4" aria-hidden="true" />
                {index + 1}. {item.title}
              </div>
            ))}
          </section>

          <div className="flex flex-wrap justify-between gap-3">
            <PrimaryButton
              type="button"
              className="bg-white text-nt-text-primary shadow-sm hover:bg-slate-100"
              onClick={() =>
                previousLesson
                  ? goToLesson(previousLesson)
                  : navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })
              }
            >
              <ArrowLeft className="size-4" aria-hidden="true" />
              {previousLesson ? "Anterior" : "Lecciones"}
            </PrimaryButton>
            <PrimaryButton type="button" onClick={() => (nextLesson ? goToLesson(nextLesson) : goToPractice())}>
              {nextLesson ? "Siguiente" : "Ir a práctica"}
              <ArrowRight className="size-4" aria-hidden="true" />
            </PrimaryButton>
          </div>
        </>
      )}
    </StudentLayout>
  );
}

export default TheoryLesson;
