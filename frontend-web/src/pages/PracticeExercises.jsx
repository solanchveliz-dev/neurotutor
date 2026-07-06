import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, ArrowRight, Check, CheckCircle2, Flag, HelpCircle, Sprout, Star, Target, TrendingUp, XCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import { getLearningContent } from "../services/learningService";
import { submitPracticeAttempt } from "../services/progressService";
import { getStudentId } from "../utils/auth";

function mapExercise(exercise) {
  return {
    id: exercise.id,
    question: exercise.question,
    options: exercise.options ?? [],
    correctAnswer: exercise.correctAnswerIndex,
    explanation: exercise.tutorExplanation,
    image: exercise.image_url ?? exercise.imageUrl ?? exercise.image ?? null,
    points: exercise.points ?? 10,
  };
}

const getTitle = (item, fallback = "") =>
  item?.title ?? item?.titulo ?? item?.nombre ?? item?.name ?? fallback;

const inferLevelName = (value = "") => {
  const text = String(value).toLowerCase();

  if (text.includes("intermedio") || /\bii\b/.test(text)) return "Intermedio";
  if (text.includes("avanzado") || /\biii\b/.test(text)) return "Avanzado";
  if (text.includes("basico") || text.includes("básico") || /\bi\b/.test(text)) return "Básico";
  return value;
};

function FractionText({ children }) {
  return String(children ?? "").split(/(\d+\s*\/\s*\d+)/g).map((part, index) => {
    const fraction = part.match(/^(\d+)\s*\/\s*(\d+)$/);
    if (!fraction) return <span key={index}>{part}</span>;
    return <span key={index} className="inline-grid min-w-9 align-middle text-center text-xl font-black leading-none"><span className="border-b-2 border-current px-1 pb-1">{fraction[1]}</span><span className="px-1 pt-1">{fraction[2]}</span></span>;
  });
}

function BasicPracticeProgress({ answeredCount, totalCount }) {
  const safeTotal = Math.max(0, Number(totalCount) || 0);
  const safeAnswered = Math.min(safeTotal, Math.max(0, Number(answeredCount) || 0));
  const percentage = safeTotal ? Math.round((safeAnswered / safeTotal) * 100) : 0;

  return <section className="rounded-[26px] border border-emerald-100 bg-white p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]">
    <h2 className="flex items-center gap-2 text-xl font-black text-green-700"><Sprout className="size-6" />Tu progreso</h2>
    <div className="mt-4 grid grid-cols-[minmax(0,1fr)_92px] items-center gap-4">
      <div className="min-w-0"><p className="text-2xl font-black text-nt-text-primary">{safeAnswered} <span className="text-base">/ {safeTotal} ejercicios</span></p><div className="mt-5 flex h-3 overflow-hidden rounded-full bg-slate-200">{Array.from({ length: safeTotal }, (_, index) => <span key={index} className={`h-full flex-1 border-r border-white last:border-r-0 ${index < safeAnswered ? "bg-gradient-to-r from-emerald-500 to-green-600" : "bg-transparent"}`} />)}</div></div>
      <div className="grid size-[88px] place-items-center rounded-full" style={{ background: `conic-gradient(#16a34a ${percentage * 3.6}deg, #e8eaf2 0deg)` }}><div className="grid size-[68px] place-items-center rounded-full bg-white text-xl font-black text-green-700">{percentage}%</div></div>
    </div>
  </section>;
}

function IntermediatePracticeProgress({ answeredCount, totalCount }) {
  const safeTotal = Math.max(0, Number(totalCount) || 0);
  const safeAnswered = Math.min(safeTotal, Math.max(0, Number(answeredCount) || 0));
  const percentage = safeTotal ? Math.round((safeAnswered / safeTotal) * 100) : 0;
  return <section className="rounded-[26px] border border-violet-100 bg-white p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="flex items-center gap-2 text-xl font-black text-violet-700"><TrendingUp className="size-6" />Tu avance</h2><div className="mt-4 grid grid-cols-[92px_minmax(0,1fr)] items-center gap-4"><div className="grid size-[88px] place-items-center rounded-full" style={{ background: `conic-gradient(#6d28d9 ${percentage * 3.6}deg, #e8e5f5 0deg)` }}><div className="grid size-[68px] place-items-center rounded-full bg-white text-xl font-black text-violet-700">{percentage}%</div></div><div className="min-w-0"><p className="text-2xl font-black text-nt-text-primary">{safeAnswered} / {safeTotal}</p><p className="text-sm font-bold text-slate-600">ejercicios</p><div className="mt-3 flex h-3 overflow-hidden rounded-full bg-violet-100">{Array.from({ length: safeTotal }, (_, index) => <span key={index} className={`h-full flex-1 border-r border-white last:border-r-0 ${index < safeAnswered ? "bg-gradient-to-r from-violet-600 to-purple-700" : "bg-transparent"}`} />)}</div></div></div></section>;
}

function AdvancedPracticeProgress({ answeredCount, totalCount }) {
  const safeTotal = Math.max(0, Number(totalCount) || 0);
  const safeAnswered = Math.min(safeTotal, Math.max(0, Number(answeredCount) || 0));
  const percentage = safeTotal ? Math.round((safeAnswered / safeTotal) * 100) : 0;
  return <section className="rounded-[26px] border border-red-100 bg-white p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="flex items-center gap-2 text-xl font-black text-red-700"><TrendingUp className="size-6" />Tu avance</h2><div className="mt-4 grid grid-cols-[92px_minmax(0,1fr)] items-center gap-4"><div className="grid size-[88px] place-items-center rounded-full" style={{ background: `conic-gradient(#dc2626 ${percentage * 3.6}deg, #f3e5e7 0deg)` }}><div className="grid size-[68px] place-items-center rounded-full bg-white text-xl font-black text-red-700">{percentage}%</div></div><div className="min-w-0"><p className="text-2xl font-black text-nt-text-primary">{safeAnswered} / {safeTotal}</p><p className="text-sm font-bold text-slate-600">ejercicios</p><div className="mt-3 flex h-3 overflow-hidden rounded-full bg-red-100">{Array.from({ length: safeTotal }, (_, index) => <span key={index} className={`h-full flex-1 border-r border-white last:border-r-0 ${index < safeAnswered ? "bg-gradient-to-r from-red-500 to-red-700" : "bg-transparent"}`} />)}</div></div></div></section>;
}

function PracticeExercises() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId: nestedLevelId } = useParams();
  const learningModuloId = nestedLevelId ?? moduleId;
  const routeModule = location.state?.module;
  const routeLevel = location.state?.level;
  const [exercises, setExercises] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showFeedback, setShowFeedback] = useState(false);
  const [points, setPoints] = useState(0);
  const [answeredItems, setAnsweredItems] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [attemptError, setAttemptError] = useState("");
  const [showAchievementModal, setShowAchievementModal] = useState(false);

  const module = routeModule ?? { id: moduleId, title: "Módulo" };
  const level = routeLevel ?? { id: learningModuloId, name: "Nivel" };
  const moduleTitle = getTitle(routeModule, "Módulo");
  const rawLevelTitle = getTitle(routeLevel, "Nivel");
  const levelTitle = inferLevelName(rawLevelTitle);
  const isBasicLevel = levelTitle === "Básico" || Number(learningModuloId) === 6;
  const isIntermediateLevel = levelTitle === "Intermedio" || Number(learningModuloId) === 7;
  const isAdvancedLevel = levelTitle === "Avanzado" || Number(learningModuloId) === 8;
  const backLevelId = routeLevel?.id ?? routeLevel?.levelId ?? nestedLevelId;
  const backModuleId = routeModule?.id ?? (nestedLevelId ? moduleId : null);
  const backPath =
    backModuleId && backLevelId
      ? `/module/${backModuleId}/level/${backLevelId}`
      : "/student-dashboard";

  useEffect(() => {
    setLoadError("");
    getLearningContent(learningModuloId)
      .then((content) => {
        if (Array.isArray(content.ejercicios) && content.ejercicios.length > 0) {
          setExercises(content.ejercicios.map(mapExercise));
          setCurrentIndex(0);
          setSelectedAnswer(null);
          setShowFeedback(false);
          setAnsweredItems({});
          return;
        }
        setExercises([]);
        setLoadError("Este nivel todavía no tiene ejercicios de práctica disponibles.");
      })
      .catch(() => {
        setExercises([]);
        setLoadError("No pudimos cargar la práctica desde el servidor. Intenta nuevamente en unos minutos.");
      })
      .finally(() => setIsLoading(false));
  }, [learningModuloId]);

  const currentExercise = exercises[currentIndex];
  const isCorrect = currentExercise ? selectedAnswer === currentExercise.correctAnswer : false;
  const progress = exercises.length ? ((currentIndex + 1) / exercises.length) * 100 : 0;

  const handleAnswer = (index) => {
    if (showFeedback) return;

    const numericExerciseId = Number(currentExercise.id);

    setSelectedAnswer(index);
    setShowFeedback(true);
    setAnsweredItems((prev) => ({
      ...prev,
      [currentIndex]: {
        exercise_id: Number.isFinite(numericExerciseId) ? numericExerciseId : currentExercise.id,
        selected_answer_index: index,
      },
    }));

    if (index === currentExercise.correctAnswer) {
      setPoints((prev) => prev + currentExercise.points);
    }
  };

  const submitAttemptIfReady = async () => {
    const studentId = getStudentId();
    const answersPayload = Object.values(answeredItems).filter((item) => item.exercise_id);

    if (!studentId || !answersPayload.length) return;

    try {
      setAttemptError("");
      const result = await submitPracticeAttempt({
        student_id: Number(studentId),
        modulo_id: Number(learningModuloId),
        answers: answersPayload,
      });
      setPoints(result.points_earned ?? points);
      return result;
    } catch {
      setAttemptError("No se pudo guardar tu progreso de practica. Tu avance visual se mantiene en esta pantalla.");
      return null;
    }
  };

  const handleNext = async () => {
    if (currentIndex < exercises.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setSelectedAnswer(null);
      setShowFeedback(false);
    } else {
      const result = await submitAttemptIfReady();
      if (isBasicLevel && result?.practice_completed) {
        setShowAchievementModal(true);
        return;
      }
      navigate(`/final-exam/${learningModuloId}`, { state: { module, level } });
    }
  };

  const continueAfterAchievement = () => {
    setShowAchievementModal(false);
    navigate(`/final-exam/${learningModuloId}`, { state: { module, level } });
  };

  const handlePrevious = () => {
    if (currentIndex === 0) return;
    const previousIndex = currentIndex - 1;
    const previousAnswer = answeredItems[previousIndex]?.selected_answer_index;
    setCurrentIndex(previousIndex);
    setSelectedAnswer(previousAnswer ?? null);
    setShowFeedback(previousAnswer !== undefined);
  };

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true },
    { label: "Mis logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="grid min-h-[450px] place-items-center"><div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" /></div>
      </StudentLayout>
    );
  }

  if (loadError || !currentExercise) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <section className="flex min-h-[400px] items-center justify-center">
          <Card className="w-full rounded-[32px] border border-white/80 bg-white/90 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
            <CardContent className="p-8">
              <h1 className="text-2xl font-black text-nt-text-primary">Práctica no disponible</h1>
              <p className="mt-3 text-sm font-semibold leading-6 text-nt-text-secondary">{loadError || "No encontramos ejercicios para este nivel."}</p>
              <Button
                type="button"
                className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 text-sm font-black text-white hover:bg-blue-700"
                onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}
              >
                Volver a actividades
              </Button>
            </CardContent>
          </Card>
        </section>
      </StudentLayout>
    );
  }

  if (isBasicLevel) {
    return (
      <StudentLayout
        sidebar={<AppSidebar items={sidebarItems} />}
        contentClassName="space-y-3"
        rightPanel={<>
          <BasicPracticeProgress answeredCount={Object.keys(answeredItems).length} totalCount={exercises.length} />
          <section className="overflow-hidden rounded-[26px] border border-emerald-100 bg-gradient-to-br from-emerald-50 to-amber-50 p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="flex items-center gap-2 text-xl font-black text-green-700"><Sprout className="size-6" />NEO te acompaña</h2><div className="mt-4 grid items-center gap-3 sm:grid-cols-[120px_minmax(0,1fr)] 2xl:grid-cols-[45%_55%]"><img src="/assets/neo_acompaña.png" alt="NEO acompañando la práctica" className="mx-auto h-40 w-full object-contain drop-shadow-md" /><div className="relative rounded-[20px] border border-white bg-white/90 p-4 text-sm font-bold leading-6 text-nt-text-primary shadow-sm"><strong className="block text-base">¡Tú puedes!</strong><span className="mt-1 block">Piensa con calma antes de responder.</span><span className="mt-2 block">No pasa nada si te equivocas.</span></div></div></section>
        </>}
      >
        <section className="overflow-hidden rounded-[28px] border border-emerald-100 bg-white shadow-[0_20px_55px_rgba(16,185,129,0.14)]">
          <div className="relative min-h-[260px] overflow-hidden bg-gradient-to-br from-emerald-50 via-teal-50 to-sky-50 p-5 sm:p-7">
            <div className="relative z-10 max-w-[58%] min-w-[280px]"><BackButton onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}>Volver</BackButton><div className="mt-6"><Badge className="bg-green-100 text-green-700 hover:bg-green-100">Práctica guiada</Badge><h1 className="mt-3 text-3xl font-black leading-tight text-green-700 sm:text-4xl">Práctica - Básico</h1><h2 className="mt-1 text-xl font-black text-nt-text-primary">Fracciones I: Lo esencial</h2><p className="mt-4 text-sm font-bold leading-6 text-slate-700 sm:text-base">Es momento de practicar lo aprendido.<br />Aquí puedes equivocarte.<br /><strong className="text-green-700">NEO te ayudará cuando lo necesites.</strong></p></div></div>
            <img src="/assets/neo_practica.png" alt="NEO practicando fracciones" className="absolute bottom-0 right-0 h-[94%] w-[55%] object-contain object-bottom drop-shadow-[0_18px_28px_rgba(16,185,129,0.18)]" />
          </div>

          <div className="p-4 sm:p-6">
            <div className="flex items-center justify-between gap-3 text-sm font-black text-green-700"><span>Ejercicio {currentIndex + 1} de {exercises.length}</span><span>{Math.round(progress)}%</span></div>
            <Progress value={progress} className="mt-2 h-3 bg-slate-200 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-emerald-500 [&_[data-slot=progress-indicator]]:to-green-600" />

            <Card className="mt-4 rounded-[24px] border border-emerald-100 bg-white p-0 shadow-[0_12px_32px_rgba(30,58,138,0.09)]"><CardContent className="p-5 sm:p-7"><p className="flex items-center gap-2 text-sm font-black text-green-700"><span className="grid size-7 place-items-center rounded-full bg-emerald-500 text-white"><HelpCircle className="size-5" /></span>Pregunta</p><h2 className="mx-auto mt-4 max-w-3xl text-center text-2xl font-black leading-9 text-nt-text-primary"><FractionText>{currentExercise.question}</FractionText></h2><div className="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{currentExercise.options.map((option, index) => {
              let optionClass = "group flex min-h-[112px] flex-col items-center justify-center gap-2 rounded-[18px] border-2 bg-white p-4 text-center font-black text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-emerald-500 hover:shadow-md";
              if (showFeedback && index === currentExercise.correctAnswer) optionClass += " border-emerald-500 bg-emerald-50";
              if (showFeedback && selectedAnswer === index && selectedAnswer !== currentExercise.correctAnswer) optionClass += " border-red-400 bg-red-50";
              return <button key={index} type="button" className={optionClass} onClick={() => handleAnswer(index)}><span className={`grid size-9 place-items-center rounded-full text-sm text-white ${showFeedback && selectedAnswer === index && selectedAnswer !== currentExercise.correctAnswer ? "bg-red-500" : "bg-green-600"}`}>{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center justify-center gap-1 text-lg"><FractionText>{option}</FractionText></span></button>;
            })}</div></CardContent></Card>

            {showFeedback && <div className={`mt-4 flex items-start gap-3 rounded-[20px] border p-4 ${isCorrect ? "border-emerald-200 bg-emerald-50" : "border-orange-200 bg-orange-50"}`}><span className={`grid size-10 shrink-0 place-items-center rounded-full text-white ${isCorrect ? "bg-emerald-500" : "bg-orange-500"}`}>{isCorrect ? <CheckCircle2 className="size-6" /> : <XCircle className="size-6" />}</span><div><h3 className="font-black text-nt-text-primary">{isCorrect ? "Correcto" : "Respuesta incorrecta"}</h3><p className="mt-1 text-sm font-bold leading-6 text-slate-600">{currentExercise.explanation ?? "Revisa la alternativa correcta y vuelve a intentarlo."}</p></div></div>}
            {attemptError && <p className="mt-4 rounded-[18px] border border-orange-200 bg-orange-50 px-4 py-3 text-sm font-bold text-orange-700">{attemptError}</p>}
            <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-between"><Button type="button" variant="outline" disabled={currentIndex === 0} onClick={handlePrevious} className="h-12 rounded-[16px] border-2 border-green-600 px-8 font-black text-green-700"><ArrowLeft className="size-5" />Anterior</Button>{showFeedback && <Button type="button" onClick={handleNext} className="h-12 rounded-[16px] bg-gradient-to-r from-emerald-500 to-green-600 px-10 font-black text-white shadow-lg shadow-emerald-200">{currentIndex === exercises.length - 1 ? "Ir al examen final" : "Siguiente"}<ArrowRight className="size-5" /></Button>}</div>
          </div>
        </section>

        {showAchievementModal && <div className="fixed inset-0 z-[100] grid place-items-center bg-slate-950/35 p-4 backdrop-blur-sm" role="dialog" aria-modal="true" aria-labelledby="practice-achievement-title"><section className="w-full max-w-md rounded-[30px] border border-amber-200 bg-white p-7 text-center shadow-[0_28px_80px_rgba(30,58,138,0.28)]"><img src="/assets/manos_a_la_practica.png" alt="Insignia Manos a la práctica" className="mx-auto h-36 w-full object-contain" /><h2 id="practice-achievement-title" className="mt-3 text-3xl font-black text-green-700">¡Lo lograste!</h2><p className="mt-2 font-bold text-slate-600">Ganaste una nueva insignia</p><div className="mx-auto mt-4 rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3"><strong className="text-lg font-black text-amber-800">Manos a la práctica</strong><p className="mt-1 text-sm font-bold text-slate-700">Aprobaste tu primera práctica.</p></div><Button type="button" onClick={continueAfterAchievement} className="mt-6 h-12 w-full rounded-[16px] bg-gradient-to-r from-emerald-500 to-green-600 font-black text-white"><Check className="size-5" />Continuar</Button></section></div>}
      </StudentLayout>
    );
  }

  if (isIntermediateLevel) {
    const answeredCount = Object.keys(answeredItems).length;
    const neoMessage = !showFeedback
      ? "Recuerda aplicar el método que aprendiste en teoría."
      : isCorrect
        ? "¡Muy bien! Sigue usando el mismo método."
        : "No pasa nada. Revisa el paso y vuelve a intentarlo en el siguiente ejercicio.";

    return (
      <StudentLayout
        sidebar={<AppSidebar items={sidebarItems} />}
        contentClassName="space-y-3"
        rightPanel={<>
          <IntermediatePracticeProgress answeredCount={answeredCount} totalCount={exercises.length} />
          <section className="overflow-hidden rounded-[26px] border border-violet-100 bg-gradient-to-br from-violet-50 to-indigo-50 p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="text-xl font-black text-violet-700">NEO te acompaña</h2><div className="mt-4 grid items-center gap-3 sm:grid-cols-[120px_minmax(0,1fr)] 2xl:grid-cols-[45%_55%]"><img src="/assets/neo_acompaña_intermedio.png" alt="NEO acompañando la práctica intermedia" className="mx-auto h-40 w-full object-contain drop-shadow-md" /><p className="rounded-[20px] border border-white bg-white/90 p-4 text-sm font-bold leading-6 text-nt-text-primary shadow-sm">{neoMessage}</p></div></section>
          <section className="rounded-[26px] border border-violet-100 bg-white p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="flex items-center gap-2 text-xl font-black text-violet-700"><Flag className="size-6" />Tu objetivo</h2><div className="mt-4 grid gap-3">{["Resolver operaciones con fracciones.", "Aplicar lo aprendido en teoría.", "Llegar preparado al examen final."].map((item) => <p key={item} className="flex items-start gap-2 text-sm font-bold leading-5 text-slate-700"><Target className="mt-0.5 size-4 shrink-0 text-violet-600" />{item}</p>)}</div></section>
        </>}
      >
        <section className="overflow-hidden rounded-[28px] border border-violet-100 bg-white shadow-[0_20px_55px_rgba(109,40,217,0.14)]">
          <div className="relative min-h-[260px] overflow-hidden bg-gradient-to-br from-violet-50 via-purple-50 to-indigo-50 p-5 sm:p-7">
            <div className="relative z-10 max-w-[58%] min-w-[280px]"><BackButton onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}>Volver</BackButton><div className="mt-7"><h1 className="text-3xl font-black leading-tight text-violet-700 sm:text-4xl">Práctica - Intermedio</h1><p className="mt-3 max-w-lg text-base font-bold leading-7 text-nt-text-primary sm:text-lg">Ahora aplicarás lo aprendido resolviendo ejercicios más desafiantes.</p></div></div>
            <img src="/assets/neo_practica_intermedio.png" alt="NEO practicando operaciones intermedias" className="absolute bottom-0 right-0 h-[96%] w-[56%] object-contain object-bottom drop-shadow-[0_18px_28px_rgba(109,40,217,0.18)]" />
          </div>
          <div className="p-4 sm:p-6">
            <div className="flex flex-wrap items-center justify-between gap-3"><span className="font-black text-violet-700">Ejercicio {currentIndex + 1} de {exercises.length}</span><Badge className="rounded-full bg-violet-100 px-4 py-2 text-violet-700 hover:bg-violet-100">Dificultad: Media</Badge></div>
            <Progress value={progress} className="mt-3 h-3 bg-violet-100 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-violet-600 [&_[data-slot=progress-indicator]]:to-purple-700" />
            <Card className="mt-5 rounded-[24px] border border-violet-100 bg-white p-0 shadow-[0_12px_32px_rgba(30,58,138,0.09)]"><CardContent className="p-5 sm:p-7"><p className="flex items-center gap-2 text-sm font-black text-violet-700"><span className="grid size-7 place-items-center rounded-full bg-violet-600 text-white"><HelpCircle className="size-5" /></span>Pregunta</p><h2 className="mx-auto mt-4 max-w-3xl text-center text-2xl font-black leading-9 text-nt-text-primary"><FractionText>{currentExercise.question}</FractionText></h2><div className="mt-7 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{currentExercise.options.map((option, index) => {
              let optionClass = "group flex min-h-[112px] flex-col items-center justify-center gap-2 rounded-[18px] border-2 border-violet-100 bg-white p-4 text-center font-black text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-violet-500 hover:shadow-md";
              if (showFeedback && index === currentExercise.correctAnswer) optionClass += " border-emerald-500 bg-emerald-50";
              if (showFeedback && selectedAnswer === index && selectedAnswer !== currentExercise.correctAnswer) optionClass += " border-red-400 bg-red-50";
              const letterTone = showFeedback && selectedAnswer === index && selectedAnswer !== currentExercise.correctAnswer ? "bg-red-500" : showFeedback && index === currentExercise.correctAnswer ? "bg-emerald-500" : "bg-violet-600";
              return <button key={index} type="button" className={optionClass} onClick={() => handleAnswer(index)}><span className={`grid size-9 place-items-center rounded-full text-sm text-white ${letterTone}`}>{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center justify-center gap-1 text-lg"><FractionText>{option}</FractionText></span></button>;
            })}</div></CardContent></Card>
            {showFeedback && <div className={`mt-4 flex items-start gap-3 rounded-[20px] border p-4 ${isCorrect ? "border-emerald-200 bg-emerald-50" : "border-orange-200 bg-orange-50"}`}><span className={`grid size-10 shrink-0 place-items-center rounded-full text-white ${isCorrect ? "bg-emerald-500" : "bg-orange-500"}`}>{isCorrect ? <CheckCircle2 className="size-6" /> : <XCircle className="size-6" />}</span><div><h3 className="font-black text-nt-text-primary">{isCorrect ? "Correcto" : "Respuesta incorrecta"}</h3><p className="mt-1 text-sm font-bold leading-6 text-slate-600">{currentExercise.explanation ?? "Revisa la alternativa correcta y vuelve a intentarlo."}</p></div></div>}
            {attemptError && <p className="mt-4 rounded-[18px] border border-orange-200 bg-orange-50 px-4 py-3 text-sm font-bold text-orange-700">{attemptError}</p>}
            <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-between"><Button type="button" variant="outline" disabled={currentIndex === 0} onClick={handlePrevious} className="h-12 rounded-[16px] border-2 border-violet-600 px-8 font-black text-violet-700"><ArrowLeft className="size-5" />Anterior</Button>{showFeedback && <Button type="button" onClick={handleNext} className="h-12 rounded-[16px] bg-gradient-to-r from-violet-600 to-purple-700 px-10 font-black text-white shadow-lg shadow-violet-200">{currentIndex === exercises.length - 1 ? "Ir al examen final" : "Siguiente"}<ArrowRight className="size-5" /></Button>}</div>
          </div>
        </section>
      </StudentLayout>
    );
  }

  if (isAdvancedLevel) {
    const answeredCount = Object.keys(answeredItems).length;
    return (
      <StudentLayout
        sidebar={<AppSidebar items={sidebarItems} />}
        contentClassName="space-y-3"
        rightPanel={<>
          <AdvancedPracticeProgress answeredCount={answeredCount} totalCount={exercises.length} />
          <section className="overflow-hidden rounded-[26px] border border-red-100 bg-gradient-to-br from-red-50 to-rose-50 p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="text-xl font-black text-red-700">Consejo de NEO</h2><div className="mt-4 grid items-center gap-3 sm:grid-cols-[120px_minmax(0,1fr)] 2xl:grid-cols-[45%_55%]"><img src="/assets/neo_acompaña_avanzado.png" alt="NEO aconsejando en la práctica avanzada" className="mx-auto h-40 w-full object-contain drop-shadow-md" /><p className="rounded-[20px] border border-white bg-white/90 p-4 text-sm font-bold leading-6 text-nt-text-primary shadow-sm">Analiza el problema antes de operar. Busca la estrategia más eficiente. Comprueba tu respuesta.</p></div></section>
          <section className="rounded-[26px] border border-red-100 bg-white p-5 shadow-[0_14px_34px_rgba(30,58,138,0.11)]"><h2 className="flex items-center gap-2 text-xl font-black text-red-700"><Flag className="size-6" />Desafío</h2><p className="mt-4 flex items-start gap-2 text-sm font-bold leading-6 text-slate-700"><Target className="mt-0.5 size-5 shrink-0 text-red-600" />Completa la práctica aplicando lo aprendido en teoría.</p></section>
        </>}
      >
        <section className="overflow-hidden rounded-[28px] border border-red-100 bg-white shadow-[0_20px_55px_rgba(220,38,38,0.14)]">
          <div className="relative min-h-[260px] overflow-hidden bg-gradient-to-br from-red-50 via-rose-50 to-orange-50 p-5 sm:p-7"><div className="relative z-10 max-w-[58%] min-w-[280px]"><BackButton onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}>Volver</BackButton><div className="mt-7"><h1 className="text-3xl font-black leading-tight text-red-700 sm:text-4xl">Práctica - Avanzado</h1><p className="mt-3 max-w-lg text-base font-bold leading-7 text-nt-text-primary sm:text-lg">Resuelve desafíos utilizando todo lo aprendido.</p></div></div><img src="/assets/neo_practica_avanzado.png" alt="NEO resolviendo desafíos avanzados" className="absolute bottom-0 right-0 h-[96%] w-[56%] object-contain object-bottom drop-shadow-[0_18px_28px_rgba(220,38,38,0.18)]" /></div>
          <div className="p-4 sm:p-6">
            <div className="flex flex-wrap items-center justify-between gap-3"><span className="font-black text-red-700">Problema {currentIndex + 1} de {exercises.length}</span><Badge className="rounded-full bg-red-100 px-4 py-2 text-red-700 hover:bg-red-100">Nivel: Experto</Badge></div>
            <Progress value={progress} className="mt-3 h-3 bg-red-100 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-red-500 [&_[data-slot=progress-indicator]]:to-red-700" />
            <Card className="mt-5 rounded-[24px] border border-red-100 bg-white p-0 shadow-[0_12px_32px_rgba(30,58,138,0.09)]"><CardContent className="p-5 sm:p-7"><p className="flex items-center gap-2 text-sm font-black text-red-700"><span className="grid size-7 place-items-center rounded-full bg-red-600 text-white"><HelpCircle className="size-5" /></span>Problema</p><h2 className="mx-auto mt-4 max-w-3xl text-center text-2xl font-black leading-9 text-nt-text-primary"><FractionText>{currentExercise.question}</FractionText></h2>{currentExercise.image && <div className="mx-auto mt-5 flex max-w-2xl justify-center overflow-hidden rounded-[20px] border border-red-100 bg-red-50/60 p-3"><img src={currentExercise.image} alt="Representación del problema" className="max-h-64 w-full object-contain" /></div>}<div className="mt-7 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{currentExercise.options.map((option, index) => {
              let optionClass = "group flex min-h-[112px] flex-col items-center justify-center gap-2 rounded-[18px] border-2 border-red-100 bg-white p-4 text-center font-black text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-red-500 hover:shadow-md";
              if (showFeedback && index === currentExercise.correctAnswer) optionClass += " border-emerald-500 bg-emerald-50";
              if (showFeedback && selectedAnswer === index && selectedAnswer !== currentExercise.correctAnswer) optionClass += " border-red-500 bg-red-50";
              const letterTone = showFeedback && index === currentExercise.correctAnswer ? "bg-emerald-500" : "bg-red-600";
              return <button key={index} type="button" className={optionClass} onClick={() => handleAnswer(index)}><span className={`grid size-9 place-items-center rounded-full text-sm text-white ${letterTone}`}>{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center justify-center gap-1 text-lg"><FractionText>{option}</FractionText></span></button>;
            })}</div></CardContent></Card>
            {showFeedback && <div className={`mt-4 flex items-start gap-3 rounded-[20px] border p-4 ${isCorrect ? "border-emerald-200 bg-emerald-50" : "border-orange-200 bg-orange-50"}`}><span className={`grid size-10 shrink-0 place-items-center rounded-full text-white ${isCorrect ? "bg-emerald-500" : "bg-orange-500"}`}>{isCorrect ? <CheckCircle2 className="size-6" /> : <XCircle className="size-6" />}</span><div><h3 className="font-black text-nt-text-primary">{isCorrect ? "Correcto" : "Respuesta incorrecta"}</h3><p className="mt-1 text-sm font-bold leading-6 text-slate-600">{currentExercise.explanation ?? "Revisa la alternativa correcta y vuelve a intentarlo."}</p></div></div>}
            {attemptError && <p className="mt-4 rounded-[18px] border border-orange-200 bg-orange-50 px-4 py-3 text-sm font-bold text-orange-700">{attemptError}</p>}
            <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-between"><Button type="button" variant="outline" disabled={currentIndex === 0} onClick={handlePrevious} className="h-12 rounded-[16px] border-2 border-red-600 px-8 font-black text-red-700"><ArrowLeft className="size-5" />Anterior</Button>{showFeedback && <Button type="button" onClick={handleNext} className="h-12 rounded-[16px] bg-gradient-to-r from-red-500 to-red-700 px-10 font-black text-white shadow-lg shadow-red-200">{currentIndex === exercises.length - 1 ? "Ir al examen final" : "Siguiente"}<ArrowRight className="size-5" /></Button>}</div>
          </div>
        </section>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      rightPanel={
        <LearningProgressPanel studentId={getStudentId()} moduloId={learningModuloId} />
      }
    >
      <section>
        <BackButton className="mb-4" onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}>
          Volver a actividades
        </BackButton>

        <div>
          <Card className="rounded-[32px] border border-white/80 bg-white/90 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
            <CardContent className="p-5 sm:p-7 lg:p-8">
              <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
                <div>
                  <Badge className="mb-3 h-6 rounded-full bg-nt-purple/10 px-3 text-[11px] font-black uppercase tracking-wide text-nt-purple hover:bg-nt-purple/10">
                    {moduleTitle} - {levelTitle}
                  </Badge>
                  <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
                    Práctica
                  </h1>
                  <p className="mt-2 max-w-2xl text-sm font-semibold leading-6 text-nt-text-secondary sm:text-base">
                    Resuelve las preguntas y pon a prueba lo que aprendiste.
                  </p>
                </div>

                <div className="flex w-fit items-center gap-3 rounded-[24px] border border-nt-purple-light/40 bg-nt-purple/8 px-5 py-4 text-nt-purple shadow-sm">
                  <Star className="size-6 fill-nt-yellow text-nt-yellow" aria-hidden="true" />
                  <div>
                    <span className="block text-xs font-black uppercase tracking-wide text-nt-text-secondary">
                      Puntos
                    </span>
                    <strong className="text-3xl font-black leading-none">{points}</strong>
                  </div>
                </div>
              </div>

              <div className="mt-7 rounded-[24px] border border-white/80 bg-nt-sky/45 p-4">
                <div className="mb-2 flex items-center justify-between text-xs font-black text-nt-text-secondary sm:text-sm">
                  <span>
                    Pregunta {currentIndex + 1}/{exercises.length}
                  </span>
                  <span>{Math.round(progress)}%</span>
                </div>
                <Progress
                  value={progress}
                  className="h-3 bg-white [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-nt-blue [&_[data-slot=progress-indicator]]:to-nt-purple"
                />
              </div>

              <Card className="mt-6 rounded-[30px] border border-nt-border bg-white p-0 shadow-[0_18px_45px_rgba(37,99,235,0.1)]">
                <CardContent className="p-5 sm:p-7">
                  <h2 className="text-xl font-black leading-8 text-nt-text-primary sm:text-2xl">
                    {currentExercise.question}
                  </h2>

                  <div className="mt-6 grid gap-3">
                    {currentExercise.options.map((option, index) => {
                      let optionClass =
                        "group flex min-h-[76px] items-center gap-3 rounded-[24px] border-2 bg-white p-4 text-left text-sm font-bold leading-5 text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-nt-purple hover:shadow-md sm:text-base";

                      if (showFeedback && index === currentExercise.correctAnswer) {
                        optionClass += " border-nt-green bg-green-50";
                      }

                      if (
                        showFeedback &&
                        selectedAnswer === index &&
                        selectedAnswer !== currentExercise.correctAnswer
                      ) {
                        optionClass += " border-nt-red bg-red-50";
                      }

                      return (
                        <button
                          key={index}
                          className={optionClass}
                          onClick={() => handleAnswer(index)}
                        >
                          <span
                            className={`grid size-10 shrink-0 place-items-center rounded-full text-sm font-black transition ${
                              showFeedback && index === currentExercise.correctAnswer
                                ? "bg-nt-green text-white"
                                : showFeedback &&
                                    selectedAnswer === index &&
                                    selectedAnswer !== currentExercise.correctAnswer
                                  ? "bg-nt-red text-white"
                                  : "bg-nt-purple/10 text-nt-purple group-hover:bg-nt-purple group-hover:text-white"
                            }`}
                          >
                            {String.fromCharCode(65 + index)}
                          </span>
                          <span>{option}</span>
                        </button>
                      );
                    })}
                  </div>
                </CardContent>
              </Card>

              {showFeedback && (
                <Card
                  className={`mt-5 rounded-[28px] border-2 p-0 shadow-sm ${
                    isCorrect
                      ? "border-nt-green bg-green-50"
                      : "border-nt-orange bg-orange-50"
                  }`}
                >
                  <CardContent className="p-5 sm:p-6">
                    <div className="flex items-start gap-3">
                      <div
                        className={`grid size-11 shrink-0 place-items-center rounded-full text-white ${
                          isCorrect ? "bg-nt-green" : "bg-nt-orange"
                        }`}
                      >
                        {isCorrect ? (
                          <CheckCircle2 className="size-6" aria-hidden="true" />
                        ) : (
                          <XCircle className="size-6" aria-hidden="true" />
                        )}
                      </div>
                      <div>
                        <h3 className="text-lg font-black text-nt-text-primary">
                          {isCorrect ? "Correcto" : "Respuesta incorrecta"}
                        </h3>
                        <p className="mt-1 text-sm font-bold leading-6 text-nt-text-secondary">
                          {currentExercise.explanation ??
                            "Revisa la alternativa correcta y vuelve a intentarlo."}
                        </p>
                      </div>
                    </div>

                  </CardContent>
                </Card>
              )}

              {attemptError && (
                <p className="mt-4 rounded-[18px] border border-orange-200 bg-orange-50 px-4 py-3 text-sm font-bold text-orange-700">
                  {attemptError}
                </p>
              )}

              {showFeedback && (
                <Button
                  type="button"
                  className="mt-6 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                  onClick={handleNext}
                >
                  {currentIndex === exercises.length - 1
                    ? "Ir al examen final"
                    : "Siguiente"}
                  <ArrowRight className="size-4" aria-hidden="true" />
                </Button>
              )}
            </CardContent>
          </Card>
        </div>
      </section>

    </StudentLayout>
  );
}

export default PracticeExercises;
