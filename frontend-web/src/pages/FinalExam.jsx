import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, ArrowRight, Check, CheckCircle2, Clock3, HelpCircle, ShieldCheck, Trophy } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import {
  getFinalExam,
  getLevelDetails,
  getModuleDetails,
  submitFinalExamAttempt,
} from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";
import AchievementUnlockedModal from "../components/student/AchievementUnlockedModal";

function mapExamQuestion(question) {
  return {
    id: question.id,
    question: question.question,
    imageUrl: question.image_url,
    options: question.options ?? [],
  };
}

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

function FinalExam() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId } = useParams();
  const routeModule = location.state?.module;
  const routeLevel = location.state?.level;
  const [module, setModule] = useState(null);
  const [level, setLevel] = useState(null);
  const [examQuestions, setExamQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [finished, setFinished] = useState(false);
  const [submitResult, setSubmitResult] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [submitError, setSubmitError] = useState("");
  const [unlockedAchievementCode, setUnlockedAchievementCode] = useState(null);

  const moduleTitle = module?.title;
  const levelName = inferLevelName(level?.level || level?.title);
  const isBasicLevel = levelName === "Básico" || Number(moduleId) === 6;
  const isIntermediateLevel = levelName === "Intermedio" || Number(moduleId) === 7;
  const isAdvancedLevel = levelName === "Avanzado" || Number(moduleId) === 8;
  const backLevelId = level?.id ?? routeLevel?.id ?? routeLevel?.levelId;
  const backModuleId = level?.module_id ?? routeModule?.id;
  const backPath =
    backModuleId && backLevelId
      ? `/module/${backModuleId}/level/${backLevelId}`
      : "/student-dashboard";

  useEffect(() => {
    setIsLoading(true);
    setLoadError("");
    Promise.all([getFinalExam(moduleId), getLevelDetails(moduleId)])
      .then(async ([questions, levelData]) => {
        if (Array.isArray(questions) && questions.length > 0) {
          const moduleData = await getModuleDetails(levelData.module_id);
          setExamQuestions(questions.map(mapExamQuestion));
          setModule(moduleData);
          setLevel(levelData);
          setCurrentIndex(0);
          setAnswers({});
          setFinished(false);
          return;
        }
        throw new Error("El examen no tiene preguntas disponibles.");
      })
      .catch(() => {
        setExamQuestions([]);
        setModule(null);
        setLevel(null);
        setLoadError("No pudimos cargar el examen final y sus datos desde el servidor.");
      })
      .finally(() => setIsLoading(false));
  }, [moduleId]);

  const currentQuestion = examQuestions[currentIndex];
  const selectedAnswer = answers[currentIndex];
  const progress = examQuestions.length
    ? ((currentIndex + 1) / examQuestions.length) * 100
    : 0;

  const handleSelect = (index) => {
    setAnswers({
      ...answers,
      [currentIndex]: index,
    });
  };

  const submitExam = async () => {
    const studentId = getStudentId();

    if (!studentId || !Number.isFinite(Number(moduleId))) {
      setSubmitError("No pudimos identificar al estudiante o al nivel del examen.");
      return;
    }

    setIsSubmitting(true);
    setSubmitError("");
    try {
      const result = await submitFinalExamAttempt({
        student_id: Number(studentId),
        modulo_id: Number(moduleId),
        answers: examQuestions.map((question, index) => ({
          question_id: Number(question.id),
          selected_answer_index: answers[index],
        })),
      });
      let refreshedProgress = null;
      try {
        refreshedProgress = await getModuleProgress(studentId, moduleId);
      } catch {
        refreshedProgress = null;
      }
      setSubmitResult({ ...result, moduleProgress: refreshedProgress });
      setUnlockedAchievementCode(result.unlocked_achievement_codes?.[0] ?? null);
      setFinished(true);
    } catch {
      setSubmitError("No pudimos enviar el examen. Tus respuestas siguen guardadas para que puedas reintentar.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleNext = () => {
    if (selectedAnswer === undefined) return;

    if (currentIndex < examQuestions.length - 1) {
      setCurrentIndex(currentIndex + 1);
    } else {
      submitExam();
    }
  };

  const handlePrevious = () => {
    if (currentIndex > 0) setCurrentIndex((index) => index - 1);
  };

  if (isLoading) {
    return (
      <main className="grid min-h-screen place-items-center bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)]">
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
      </main>
    );
  }

  if (!module || !level || loadError || examQuestions.length === 0) {
    return (
      <main className="min-h-screen bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
        <section className="mx-auto flex min-h-[calc(100vh-4rem)] max-w-xl items-center justify-center">
          <Card className="w-full rounded-[32px] border border-white/80 bg-white/90 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
            <CardContent className="p-8">
              <h1 className="text-2xl font-black text-nt-text-primary">Examen no disponible</h1>
              <p className="mt-3 text-sm font-semibold text-nt-text-secondary">
                {loadError || "No pudimos identificar el módulo y nivel seleccionados."}
              </p>
              <Button
                type="button"
                className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 text-sm font-black text-white hover:bg-blue-700"
                onClick={() => navigate(backPath, { state: { module, level } })}
              >
                Volver a actividades
              </Button>
            </CardContent>
          </Card>
        </section>
      </main>
    );
  }

  if (finished) {
    const score = submitResult.correct_answers;
    const totalQuestions = submitResult.total_questions;
    const percentage = submitResult.score_percentage;
    const approved = submitResult.passed;
    const achievementModal = <AchievementUnlockedModal code={unlockedAchievementCode} onClose={() => setUnlockedAchievementCode(null)} />;

    if (unlockedAchievementCode) {
      return achievementModal;
    }

    if (isBasicLevel) {
      return <main className="grid min-h-screen place-items-center bg-[linear-gradient(180deg,rgba(223,244,255,0.18),rgba(191,231,255,0.12)),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed p-4 text-nt-text-primary"><Card className="w-full max-w-3xl rounded-[32px] border border-white/90 bg-white/95 p-0 text-center shadow-[0_28px_80px_rgba(30,58,138,0.22)]"><CardContent className="p-7 sm:p-10"><div className={`mx-auto grid size-20 place-items-center rounded-[26px] text-white shadow-lg ${approved ? "bg-gradient-to-br from-emerald-500 to-green-600" : "bg-gradient-to-br from-orange-400 to-red-500"}`}>{approved ? <Trophy className="size-10" /> : <ShieldCheck className="size-10" />}</div><h1 className={`mt-5 text-3xl font-black sm:text-4xl ${approved ? "text-green-700" : "text-orange-700"}`}>{approved ? "¡Examen aprobado!" : "Aún no aprobaste"}</h1><p className="mx-auto mt-3 max-w-xl text-base font-bold leading-7 text-slate-600">{approved ? "Completaste el Nivel Básico y puedes avanzar al siguiente nivel." : "Necesitas reforzar un poco más antes de avanzar."}</p><div className={`mx-auto mt-6 w-fit rounded-[22px] px-8 py-4 text-4xl font-black text-white ${approved ? "bg-green-600" : "bg-orange-500"}`}>{percentage}%</div><p className="mt-3 text-sm font-bold text-slate-600">{score} de {totalQuestions} respuestas correctas</p>{approved ? <Button type="button" onClick={() => navigate(backPath, { state: { module, level } })} className="mt-7 h-13 w-full rounded-[16px] bg-gradient-to-r from-emerald-500 to-green-600 font-black text-white">Ir al siguiente nivel<ArrowRight className="size-5" /></Button> : <div className="mt-7 grid gap-3 sm:grid-cols-2"><Button type="button" variant="outline" onClick={() => navigate(`/module/${backModuleId}/level/${backLevelId}/theory`, { state: { module, level } })} className="h-13 rounded-[16px] border-2 border-green-600 font-black text-green-700">Repasar teoría</Button><Button type="button" onClick={() => { setFinished(false); setSubmitResult(null); setCurrentIndex(0); setAnswers({}); setSubmitError(""); }} className="h-13 rounded-[16px] bg-gradient-to-r from-emerald-500 to-green-600 font-black text-white">Intentar nuevamente</Button></div>}</CardContent></Card></main>;
    }

    if (isIntermediateLevel) {
      return <main className="grid min-h-screen place-items-center bg-[linear-gradient(180deg,rgba(223,244,255,0.18),rgba(191,231,255,0.12)),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed p-4 text-nt-text-primary"><Card className="w-full max-w-3xl rounded-[32px] border border-white/90 bg-white/95 p-0 text-center shadow-[0_28px_80px_rgba(30,58,138,0.22)]"><CardContent className="p-7 sm:p-10"><div className={`mx-auto grid size-20 place-items-center rounded-[26px] text-white shadow-lg ${approved ? "bg-gradient-to-br from-violet-500 to-purple-700" : "bg-gradient-to-br from-orange-400 to-red-500"}`}>{approved ? <Trophy className="size-10" /> : <ShieldCheck className="size-10" />}</div><h1 className={`mt-5 text-3xl font-black sm:text-4xl ${approved ? "text-violet-700" : "text-orange-700"}`}>{approved ? "¡Examen aprobado!" : "Aún no aprobaste"}</h1><p className="mx-auto mt-3 max-w-xl text-base font-bold leading-7 text-slate-600">{approved ? "Completaste el Nivel Intermedio y puedes avanzar al Nivel Avanzado." : "Necesitas reforzar un poco más antes de avanzar."}</p><div className={`mx-auto mt-6 w-fit rounded-[22px] px-8 py-4 text-4xl font-black text-white ${approved ? "bg-violet-600" : "bg-orange-500"}`}>{percentage}%</div><p className="mt-3 text-sm font-bold text-slate-600">{score} de {totalQuestions} respuestas correctas</p>{approved ? <Button type="button" onClick={() => navigate(backPath, { state: { module, level } })} className="mt-7 h-13 w-full rounded-[16px] bg-gradient-to-r from-violet-600 to-purple-700 font-black text-white">Ir al Nivel Avanzado<ArrowRight className="size-5" /></Button> : <div className="mt-7 grid gap-3 sm:grid-cols-2"><Button type="button" variant="outline" onClick={() => navigate(`/module/${backModuleId}/level/${backLevelId}/theory`, { state: { module, level } })} className="h-13 rounded-[16px] border-2 border-violet-600 font-black text-violet-700">Repasar teoría</Button><Button type="button" onClick={() => { setFinished(false); setSubmitResult(null); setCurrentIndex(0); setAnswers({}); setSubmitError(""); }} className="h-13 rounded-[16px] bg-gradient-to-r from-violet-600 to-purple-700 font-black text-white">Intentar nuevamente</Button></div>}</CardContent></Card></main>;
    }

    if (isAdvancedLevel) {
      return <main className="grid min-h-screen place-items-center bg-[linear-gradient(180deg,rgba(223,244,255,0.18),rgba(191,231,255,0.12)),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed p-4 text-nt-text-primary"><Card className="w-full max-w-3xl rounded-[32px] border border-white/90 bg-white/95 p-0 text-center shadow-[0_28px_80px_rgba(30,58,138,0.22)]"><CardContent className="p-7 sm:p-10"><div className={`mx-auto grid size-20 place-items-center rounded-[26px] text-white shadow-lg ${approved ? "bg-gradient-to-br from-red-500 to-red-800" : "bg-gradient-to-br from-orange-400 to-red-500"}`}>{approved ? <Trophy className="size-10" /> : <ShieldCheck className="size-10" />}</div><h1 className={`mt-5 text-3xl font-black sm:text-4xl ${approved ? "text-red-700" : "text-orange-700"}`}>{approved ? "¡Nivel avanzado completado!" : "Aún no aprobaste"}</h1><p className="mx-auto mt-3 max-w-xl text-base font-bold leading-7 text-slate-600">{approved ? "Has completado el último nivel y demostraste dominio de las fracciones." : "Necesitas reforzar algunos temas antes de completar el nivel avanzado."}</p><div className={`mx-auto mt-6 w-fit rounded-[22px] px-8 py-4 text-4xl font-black text-white ${approved ? "bg-red-700" : "bg-orange-500"}`}>{percentage}%</div><p className="mt-3 text-sm font-bold text-slate-600">{score} de {totalQuestions} respuestas correctas</p>{approved ? <Button type="button" onClick={() => navigate('/achievements')} className="mt-7 h-13 w-full rounded-[16px] bg-gradient-to-r from-red-600 to-red-800 font-black text-white">Ver mis logros<ArrowRight className="size-5" /></Button> : <div className="mt-7 grid gap-3 sm:grid-cols-2"><Button type="button" variant="outline" onClick={() => navigate(`/module/${backModuleId}/level/${backLevelId}/theory`, { state: { module, level } })} className="h-13 rounded-[16px] border-2 border-red-600 font-black text-red-700">Repasar teoría</Button><Button type="button" onClick={() => { setFinished(false); setSubmitResult(null); setCurrentIndex(0); setAnswers({}); setSubmitError(""); }} className="h-13 rounded-[16px] bg-gradient-to-r from-red-600 to-red-800 font-black text-white">Intentar nuevamente</Button></div>}</CardContent></Card></main>;
    }

    return (
      <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
        <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
        <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

        <section className="relative mx-auto grid min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center gap-5 lg:grid-cols-[minmax(0,1fr)_320px]">
          <Card className="w-full rounded-[32px] border border-white/80 bg-white/88 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
            <CardContent className="p-6 sm:p-8">
              <div
                className={`mx-auto mb-5 grid size-20 place-items-center rounded-[28px] text-white shadow-[0_18px_38px_rgba(37,99,235,0.24)] ${
                  approved
                    ? "bg-gradient-to-br from-nt-green to-emerald-500"
                    : "bg-gradient-to-br from-nt-orange to-nt-red"
                }`}
              >
                {approved ? (
                  <Trophy className="size-10" aria-hidden="true" />
                ) : (
                  <ShieldCheck className="size-10" aria-hidden="true" />
                )}
              </div>

              <Badge
                className={`mx-auto mb-3 h-6 rounded-full px-3 text-[11px] font-black uppercase tracking-wide hover:bg-current/10 ${
                  approved
                    ? "bg-nt-green/12 text-green-700"
                    : "bg-nt-orange/15 text-orange-700"
                }`}
              >
                Examen final
              </Badge>

              <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
                {approved ? "Nivel aprobado" : "Necesitas repasar"}
              </h1>

              <p className="mt-3 text-sm font-semibold leading-6 text-nt-text-secondary sm:text-base">
                Obtuviste <strong>{score}</strong> de{" "}
                <strong>{totalQuestions}</strong> respuestas correctas.
              </p>

              <div
                className={`mx-auto mt-6 w-fit rounded-[24px] px-8 py-4 text-4xl font-black text-white shadow-lg ${
                  approved ? "bg-nt-green" : "bg-nt-red"
                }`}
              >
                {percentage}%
              </div>

              <p className="mx-auto mt-5 max-w-md text-sm font-bold leading-6 text-nt-text-secondary">
                {submitResult.message}
              </p>

              <Button
                type="button"
                className="mt-7 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                onClick={() => navigate(backPath, { state: { module, level } })}
              >
                {backModuleId && backLevelId ? "Volver a actividades" : "Volver al panel"}
                <ArrowRight className="size-4" aria-hidden="true" />
              </Button>
            </CardContent>
          </Card>
          <LearningProgressPanel
            studentId={getStudentId()}
            moduloId={moduleId}
            progress={submitResult?.moduleProgress}
            title="Progreso después del examen"
          />
        </section>
      </main>
    );
  }

  if (isBasicLevel) {
    const roundedProgress = Math.round(progress);
    return (
      <main className="min-h-screen bg-[linear-gradient(180deg,rgba(223,244,255,0.12),rgba(191,231,255,0.08)),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed p-3 text-nt-text-primary sm:p-5 lg:p-7">
        <Card className="mx-auto w-full max-w-[1450px] overflow-hidden rounded-[32px] border border-white/90 bg-white/95 p-0 shadow-[0_28px_80px_rgba(30,58,138,0.2)]"><CardContent className="p-4 sm:p-6">
          <header className="grid items-center gap-3 sm:grid-cols-[auto_minmax(0,1fr)_auto_112px]"><BackButton onClick={() => navigate(backPath, { state: { module, level } })}>Volver</BackButton><h1 className="text-center text-2xl font-black text-green-700 sm:text-3xl">Examen Final - Nivel Básico</h1><p className="text-center font-black text-nt-text-primary sm:text-right">Pregunta {currentIndex + 1} de {examQuestions.length}</p><div className="mx-auto grid size-[104px] place-items-center rounded-full" style={{ background: `conic-gradient(#16a34a ${roundedProgress * 3.6}deg, #d8efdc 0deg)` }}><div className="grid size-[82px] place-items-center rounded-full bg-white text-center"><strong className="text-2xl font-black text-green-700">{currentIndex + 1}</strong><span className="-mt-2 text-[11px] font-black leading-3 text-nt-text-primary">de {examQuestions.length}<br />Preguntas</span></div></div></header>

          <section className="relative mt-3 min-h-[240px] overflow-hidden rounded-[26px] border border-emerald-100 bg-gradient-to-br from-emerald-50 via-sky-50 to-blue-100 p-6 sm:p-8"><div className="relative z-10 max-w-[48%] min-w-[250px]"><p className="text-4xl font-black text-green-700 sm:text-5xl">EXAMEN FINAL</p><h2 className="mt-2 text-2xl font-black text-teal-600 sm:text-3xl">Nivel Básico</h2><p className="mt-4 max-w-md text-base font-bold leading-7 text-nt-text-primary sm:text-lg">Demuestra todo lo que aprendiste sobre las fracciones.</p></div><img src="/assets/examen_basico.png" alt="Examen final del nivel básico" className="absolute bottom-0 right-0 h-full w-[58%] object-contain object-bottom" /></section>

          <section className="mt-4 grid items-center gap-3 rounded-[20px] border border-emerald-200 bg-emerald-50/60 p-4 md:grid-cols-[150px_repeat(4,minmax(0,1fr))]"><h2 className="flex items-center gap-2 text-lg font-black text-green-700"><ShieldCheck className="size-8" />Reglas del examen</h2>{["Lee cada pregunta.", "Puedes cambiar tu respuesta antes de finalizar.", "Obtendrás tu resultado al terminar."].map((rule) => <p key={rule} className="flex items-start gap-2 border-emerald-200 text-sm font-bold leading-5 text-slate-700 md:border-l md:pl-4"><Check className="mt-0.5 size-5 shrink-0 rounded-full bg-green-600 p-1 text-white" />{rule}</p>)}<p className="flex items-start gap-2 border-emerald-200 text-sm font-bold leading-5 text-slate-700 md:border-l md:pl-4"><Clock3 className="size-5 shrink-0 text-green-700" />Tiempo estimado: 20 minutos.</p></section>

          <section className="mt-4 rounded-[24px] border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex items-center justify-between gap-3 text-sm font-black"><span className="text-green-700">Pregunta {currentIndex + 1} / {examQuestions.length}</span><span className="text-green-700">{roundedProgress}% completado</span></div><Progress value={progress} className="mt-3 h-3 bg-slate-200 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-emerald-500 [&_[data-slot=progress-indicator]]:to-green-600" /><div className="mt-5 rounded-[20px] border border-emerald-100 p-5"><p className="flex items-center gap-2 text-sm font-black text-green-700"><span className="grid size-7 place-items-center rounded-full bg-green-600 text-white"><HelpCircle className="size-5" /></span>Pregunta</p><h2 className="mx-auto mt-4 max-w-4xl text-center text-2xl font-black leading-9 text-nt-text-primary"><FractionText>{currentQuestion.question}</FractionText></h2>{currentQuestion.imageUrl && <div className="mx-auto mt-5 flex max-w-3xl justify-center overflow-hidden rounded-[18px] bg-emerald-50/50 p-3"><img src={currentQuestion.imageUrl} alt="Apoyo visual de la pregunta" className="max-h-64 w-full object-contain" /></div>}<div className="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{currentQuestion.options.map((option, index) => { const isSelected = selectedAnswer === index; return <button key={index} type="button" onClick={() => handleSelect(index)} className={`flex min-h-[100px] items-center justify-center gap-3 rounded-[18px] border-2 p-4 font-black shadow-sm transition hover:-translate-y-0.5 hover:border-green-500 ${isSelected ? "border-green-600 bg-emerald-50 ring-4 ring-green-100" : "border-emerald-100 bg-white"}`}><span className={`grid size-10 shrink-0 place-items-center rounded-full text-white ${isSelected ? "bg-green-700" : "bg-green-600"}`}>{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center justify-center gap-1 text-lg"><FractionText>{option}</FractionText></span></button>; })}</div></div>
            {submitError && <p className="mt-4 rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">{submitError}</p>}
            <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-between"><Button type="button" variant="outline" disabled={currentIndex === 0 || isSubmitting} onClick={handlePrevious} className="h-13 rounded-[16px] border-2 border-green-600 px-9 font-black text-green-700"><ArrowLeft className="size-5" />Anterior</Button><Button type="button" disabled={selectedAnswer === undefined || isSubmitting} onClick={handleNext} className="h-13 rounded-[16px] bg-gradient-to-r from-emerald-500 to-green-600 px-10 font-black text-white shadow-lg shadow-emerald-200">{isSubmitting ? "Enviando respuestas..." : currentIndex === examQuestions.length - 1 ? "Finalizar examen" : "Siguiente"}{currentIndex === examQuestions.length - 1 ? <CheckCircle2 className="size-5" /> : <ArrowRight className="size-5" />}</Button></div>
          </section>
        </CardContent></Card>
      </main>
    );
  }

  if (isIntermediateLevel) {
    const roundedProgress = Math.round(progress);
    return <main className="min-h-screen bg-[linear-gradient(180deg,rgba(223,244,255,0.12),rgba(191,231,255,0.08)),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed p-3 text-nt-text-primary sm:p-5 lg:p-7"><Card className="mx-auto w-full max-w-[1450px] overflow-hidden rounded-[32px] border border-white/90 bg-white/95 p-0 shadow-[0_28px_80px_rgba(30,58,138,0.2)]"><CardContent className="p-4 sm:p-6">
      <header className="grid items-center gap-3 sm:grid-cols-[auto_minmax(0,1fr)_auto_112px]"><BackButton onClick={() => navigate(backPath, { state: { module, level } })}>Volver</BackButton><h1 className="text-center text-2xl font-black text-violet-700 sm:text-3xl">Examen Final - Nivel Intermedio</h1><p className="text-center font-black text-nt-text-primary sm:text-right">Pregunta {currentIndex + 1} de {examQuestions.length}</p><div className="mx-auto grid size-[104px] place-items-center rounded-full" style={{ background: `conic-gradient(#6d28d9 ${roundedProgress * 3.6}deg, #e8def8 0deg)` }}><div className="grid size-[82px] place-items-center rounded-full bg-white text-center"><strong className="text-2xl font-black text-violet-700">{currentIndex + 1}</strong><span className="-mt-2 text-[11px] font-black leading-3">de {examQuestions.length}<br />Preguntas</span></div></div></header>
      <section className="relative mt-3 min-h-[260px] overflow-hidden rounded-[26px] border border-violet-100 bg-gradient-to-br from-violet-50 via-purple-50 to-indigo-100 p-6 sm:p-8"><div className="relative z-10 max-w-[48%] min-w-[250px]"><p className="text-4xl font-black text-violet-800 sm:text-5xl">EXAMEN FINAL</p><h2 className="mt-2 text-2xl font-black text-violet-600 sm:text-3xl">Nivel Intermedio</h2><p className="mt-4 max-w-md text-base font-bold leading-7 sm:text-lg">Es momento de aplicar estrategias y resolver problemas.</p></div><img src="/assets/examen_intermedio.png" alt="Examen final del nivel intermedio" className="absolute bottom-0 right-0 h-full w-[60%] object-contain object-bottom" /></section>
      <section className="mt-4 grid items-center gap-3 rounded-[20px] border border-violet-200 bg-violet-50/60 p-4 md:grid-cols-[150px_repeat(4,minmax(0,1fr))]"><h2 className="flex items-center gap-2 text-lg font-black text-violet-700"><ShieldCheck className="size-8" />Reglas del examen</h2>{["Lee cada pregunta con atención.", "Puedes cambiar tu respuesta antes de finalizar.", "Obtendrás tu resultado al terminar."].map((rule) => <p key={rule} className="flex items-start gap-2 text-sm font-bold leading-5 text-slate-700 md:border-l md:border-violet-200 md:pl-4"><Check className="mt-0.5 size-5 shrink-0 rounded-full bg-violet-600 p-1 text-white" />{rule}</p>)}<p className="flex items-start gap-2 text-sm font-bold leading-5 text-slate-700 md:border-l md:border-violet-200 md:pl-4"><Clock3 className="size-5 shrink-0 text-violet-700" />Tiempo estimado: 20 minutos.</p></section>
      <section className="mt-4 rounded-[24px] border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex items-center justify-between gap-3 text-sm font-black text-violet-700"><span>Pregunta {currentIndex + 1} / {examQuestions.length}</span><span>{roundedProgress}% completado</span></div><Progress value={progress} className="mt-3 h-3 bg-violet-100 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-violet-600 [&_[data-slot=progress-indicator]]:to-purple-700" /><div className="mt-5 rounded-[20px] border border-violet-100 p-5"><p className="flex items-center gap-2 text-sm font-black text-violet-700"><span className="grid size-7 place-items-center rounded-full bg-violet-600 text-white"><HelpCircle className="size-5" /></span>Pregunta</p><h2 className="mx-auto mt-4 max-w-4xl text-center text-2xl font-black leading-9"><FractionText>{currentQuestion.question}</FractionText></h2>{currentQuestion.imageUrl && <div className="mx-auto mt-5 flex max-w-3xl justify-center overflow-hidden rounded-[18px] bg-violet-50/50 p-3"><img src={currentQuestion.imageUrl} alt="Apoyo visual de la pregunta" className="max-h-64 w-full object-contain" /></div>}<div className="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{currentQuestion.options.map((option, index) => { const isSelected = selectedAnswer === index; return <button key={index} type="button" onClick={() => handleSelect(index)} className={`flex min-h-[100px] items-center justify-center gap-3 rounded-[18px] border-2 p-4 font-black shadow-sm transition hover:-translate-y-0.5 hover:border-violet-500 ${isSelected ? "border-violet-600 bg-violet-50 ring-4 ring-violet-100" : "border-violet-100 bg-white"}`}><span className="grid size-10 shrink-0 place-items-center rounded-full bg-violet-600 text-white">{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center justify-center gap-1 text-lg"><FractionText>{option}</FractionText></span></button>; })}</div></div>
      {submitError && <p className="mt-4 rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">{submitError}</p>}<div className="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-between"><Button type="button" variant="outline" disabled={currentIndex === 0 || isSubmitting} onClick={handlePrevious} className="h-13 rounded-[16px] border-2 border-violet-600 px-9 font-black text-violet-700"><ArrowLeft className="size-5" />Anterior</Button><Button type="button" disabled={selectedAnswer === undefined || isSubmitting} onClick={handleNext} className="h-13 rounded-[16px] bg-gradient-to-r from-violet-600 to-purple-700 px-10 font-black text-white shadow-lg shadow-violet-200">{isSubmitting ? "Enviando respuestas..." : currentIndex === examQuestions.length - 1 ? "Finalizar examen" : "Siguiente"}{currentIndex === examQuestions.length - 1 ? <CheckCircle2 className="size-5" /> : <ArrowRight className="size-5" />}</Button></div></section>
    </CardContent></Card></main>;
  }

  if (isAdvancedLevel) {
    const roundedProgress = Math.round(progress);
    return <main className="min-h-screen bg-[linear-gradient(180deg,rgba(223,244,255,0.12),rgba(191,231,255,0.08)),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed p-3 text-nt-text-primary sm:p-5 lg:p-7"><Card className="mx-auto w-full max-w-[1450px] overflow-hidden rounded-[32px] border border-white/90 bg-white/95 p-0 shadow-[0_28px_80px_rgba(30,58,138,0.2)]"><CardContent className="p-4 sm:p-6">
      <header className="grid items-center gap-3 sm:grid-cols-[auto_minmax(0,1fr)_auto_112px]"><BackButton onClick={() => navigate(backPath, { state: { module, level } })}>Volver</BackButton><h1 className="text-center text-2xl font-black text-red-800 sm:text-3xl">Examen Final - Nivel Avanzado</h1><p className="text-center font-black sm:text-right">Pregunta {currentIndex + 1} de {examQuestions.length}</p><div className="mx-auto grid size-[104px] place-items-center rounded-full" style={{ background: `conic-gradient(#c90012 ${roundedProgress * 3.6}deg, #f5dfe1 0deg)` }}><div className="grid size-[82px] place-items-center rounded-full bg-white text-center"><strong className="text-2xl font-black text-red-700">{currentIndex + 1}</strong><span className="-mt-2 text-[11px] font-black leading-3">de {examQuestions.length}<br />Preguntas</span></div></div></header>
      <section className="relative mt-3 min-h-[280px] overflow-hidden rounded-[26px] border border-red-800 bg-gradient-to-br from-[#51080d] via-[#8f1118] to-[#d73a2f] p-6 text-white shadow-inner sm:p-8"><div className="relative z-10 max-w-[48%] min-w-[250px]"><p className="text-4xl font-black sm:text-5xl">EXAMEN FINAL</p><h2 className="mt-2 text-2xl font-black text-amber-300 sm:text-3xl">Nivel Avanzado</h2><p className="mt-4 max-w-md text-base font-bold leading-7 sm:text-lg">Acepta el desafío y demuestra que dominas las fracciones.</p></div><img src="/assets/examen_avanzado.png" alt="Examen final del nivel avanzado" className="absolute bottom-0 right-0 h-full w-[62%] object-contain object-bottom" /></section>
      <section className="mt-4 grid items-center gap-3 rounded-[20px] border border-red-200 bg-red-50/60 p-4 md:grid-cols-[150px_repeat(4,minmax(0,1fr))]"><h2 className="flex items-center gap-2 text-lg font-black text-red-700"><ShieldCheck className="size-8" />Reglas del examen</h2>{["Lee cada pregunta con atención.", "Puedes cambiar tu respuesta antes de finalizar.", "Obtendrás tu resultado al terminar."].map((rule) => <p key={rule} className="flex items-start gap-2 text-sm font-bold leading-5 text-slate-700 md:border-l md:border-red-200 md:pl-4"><Check className="mt-0.5 size-5 shrink-0 rounded-full bg-red-600 p-1 text-white" />{rule}</p>)}<p className="flex items-start gap-2 text-sm font-bold leading-5 text-slate-700 md:border-l md:border-red-200 md:pl-4"><Clock3 className="size-5 shrink-0 text-red-700" />Tiempo estimado: 25 minutos.</p></section>
      <section className="mt-4 rounded-[24px] border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex items-center justify-between gap-3 text-sm font-black text-red-700"><span>Pregunta {currentIndex + 1} / {examQuestions.length}</span><span>{roundedProgress}% completado</span></div><Progress value={progress} className="mt-3 h-3 bg-red-100 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-red-500 [&_[data-slot=progress-indicator]]:to-red-800" /><div className="mt-5 rounded-[20px] border border-red-100 p-5"><p className="flex items-center gap-2 text-sm font-black text-red-700"><span className="grid size-7 place-items-center rounded-full bg-red-700 text-white"><HelpCircle className="size-5" /></span>Pregunta</p><h2 className="mx-auto mt-4 max-w-4xl text-center text-2xl font-black leading-9"><FractionText>{currentQuestion.question}</FractionText></h2>{currentQuestion.imageUrl && <div className="mx-auto mt-5 flex max-w-3xl justify-center overflow-hidden rounded-[18px] bg-red-50/50 p-3"><img src={currentQuestion.imageUrl} alt="Apoyo visual de la pregunta" className="max-h-64 w-full object-contain" /></div>}<div className="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{currentQuestion.options.map((option, index) => { const isSelected = selectedAnswer === index; return <button key={index} type="button" onClick={() => handleSelect(index)} className={`flex min-h-[100px] items-center justify-center gap-3 rounded-[18px] border-2 p-4 font-black shadow-sm transition hover:-translate-y-0.5 hover:border-red-500 ${isSelected ? "border-red-700 bg-red-50 ring-4 ring-red-100" : "border-red-100 bg-white"}`}><span className="grid size-10 shrink-0 place-items-center rounded-full bg-red-700 text-white">{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center justify-center gap-1 text-lg"><FractionText>{option}</FractionText></span></button>; })}</div></div>
      {submitError && <p className="mt-4 rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">{submitError}</p>}<div className="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-between"><Button type="button" variant="outline" disabled={currentIndex === 0 || isSubmitting} onClick={handlePrevious} className="h-13 rounded-[16px] border-2 border-red-600 px-9 font-black text-red-700"><ArrowLeft className="size-5" />Anterior</Button><Button type="button" disabled={selectedAnswer === undefined || isSubmitting} onClick={handleNext} className="h-13 rounded-[16px] bg-gradient-to-r from-red-600 to-red-800 px-10 font-black text-white shadow-lg shadow-red-200">{isSubmitting ? "Enviando respuestas..." : currentIndex === examQuestions.length - 1 ? "Finalizar examen" : "Siguiente"}{currentIndex === examQuestions.length - 1 ? <CheckCircle2 className="size-5" /> : <ArrowRight className="size-5" />}</Button></div></section>
    </CardContent></Card></main>;
  }

  return (
    <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto w-full max-w-5xl">
        <BackButton className="mb-4" onClick={() => navigate(backPath, { state: { module, level } })}>
          Volver a actividades
        </BackButton>

        <Card className="rounded-[32px] border border-white/80 bg-white/88 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
          <CardContent className="p-5 sm:p-7 lg:p-8">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
              <div>
                <Badge className="mb-3 h-6 rounded-full bg-red-50 px-3 text-[11px] font-black uppercase tracking-wide text-red-700 hover:bg-red-50">
                  Examen final - {moduleTitle} - {levelName}
                </Badge>
                <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
                  Demuestra lo aprendido
                </h1>
                <p className="mt-2 max-w-2xl text-sm font-semibold leading-6 text-nt-text-secondary sm:text-base">
                  Durante el examen final no está disponible el Tutor IA. Responde
                  todas las preguntas para finalizar.
                </p>
              </div>

              <div className="flex w-fit items-center gap-3 rounded-[24px] border border-red-200 bg-red-50 px-5 py-4 text-red-700 shadow-sm">
                <ShieldCheck className="size-6" aria-hidden="true" />
                <div>
                  <span className="block text-xs font-black uppercase tracking-wide text-red-600">
                    Modo examen
                  </span>
                  <strong className="text-sm font-black leading-none">Sin ayudas</strong>
                </div>
              </div>
            </div>

            <div className="mt-7">
              <div className="mb-2 flex items-center justify-between text-xs font-black text-nt-text-secondary sm:text-sm">
                <span>
                  Pregunta {currentIndex + 1}/{examQuestions.length}
                </span>
                <span>{Math.round(progress)}%</span>
              </div>
              <Progress
                value={progress}
                className="h-3 bg-nt-sky [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-nt-blue [&_[data-slot=progress-indicator]]:to-nt-purple"
              />
            </div>

            <Card className="mt-6 rounded-[28px] border border-nt-border bg-white p-0 shadow-sm">
              <CardContent className="p-5 sm:p-6">
                <h2 className="text-xl font-black leading-8 text-nt-text-primary sm:text-2xl">
                  {currentQuestion.question}
                </h2>

                {currentQuestion.imageUrl && (
                  <img
                    src={currentQuestion.imageUrl}
                    alt="Apoyo visual de la pregunta"
                    className="mx-auto mt-5 max-h-72 w-full rounded-[20px] object-contain"
                  />
                )}

                <div className="mt-5 grid gap-3 md:grid-cols-2">
                  {currentQuestion.options.map((option, index) => {
                    const isSelected = selectedAnswer === index;

                    return (
                      <button
                        key={index}
                        className={`group flex min-h-[72px] items-center gap-3 rounded-[22px] border-2 bg-white p-4 text-left text-sm font-bold leading-5 text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-nt-purple hover:shadow-md sm:text-base ${
                          isSelected
                            ? "border-nt-purple bg-nt-purple/8 ring-4 ring-nt-purple/10"
                            : "border-nt-border"
                        }`}
                        onClick={() => handleSelect(index)}
                      >
                        <span
                          className={`grid size-9 shrink-0 place-items-center rounded-full text-sm font-black transition ${
                            isSelected
                              ? "bg-nt-purple text-white"
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

            {submitError && (
              <p className="mt-4 rounded-[18px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">
                {submitError}
              </p>
            )}

            <Button
              type="button"
              className="mt-6 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
              disabled={selectedAnswer === undefined || isSubmitting}
              onClick={handleNext}
            >
              {isSubmitting
                ? "Enviando respuestas..."
                : currentIndex === examQuestions.length - 1
                ? "Finalizar examen"
                : "Siguiente"}
              {currentIndex === examQuestions.length - 1 ? (
                <CheckCircle2 className="size-4" aria-hidden="true" />
              ) : (
                <ArrowRight className="size-4" aria-hidden="true" />
              )}
            </Button>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

export default FinalExam;
