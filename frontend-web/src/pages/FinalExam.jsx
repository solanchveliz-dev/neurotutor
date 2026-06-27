import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { ArrowRight, CheckCircle2, ShieldCheck, Trophy, XCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import { modulesData } from "../data/modulesData";
import {
  getFinalExam,
  submitFinalExamAttempt,
} from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

function mapExamQuestion(question) {
  return {
    id: question.id,
    question: question.question,
    imageUrl: question.image_url,
    options: question.options ?? [],
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

function FinalExam() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId } = useParams();
  const routeModule = location.state?.module;
  const routeLevel = location.state?.level;
  const [examQuestions, setExamQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [finished, setFinished] = useState(false);
  const [submitResult, setSubmitResult] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [submitError, setSubmitError] = useState("");

  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(moduleId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => item.unlocked) ?? fallbackModule?.levels[0];
  const module = routeModule ?? fallbackModule;
  const level = routeLevel ?? fallbackLevel;
  const moduleTitle = getTitle(routeModule, getTitle(fallbackModule, "Modulo"));
  const levelName = inferLevelName(getTitle(routeLevel, getTitle(fallbackLevel, "Nivel")));
  const backLevelId = routeLevel?.id ?? routeLevel?.levelId;
  const backModuleId = routeModule?.id;
  const backPath =
    backModuleId && backLevelId
      ? `/module/${backModuleId}/level/${backLevelId}`
      : "/student-dashboard";

  useEffect(() => {
    setIsLoading(true);
    setLoadError("");
    getFinalExam(moduleId)
      .then((questions) => {
        if (Array.isArray(questions) && questions.length > 0) {
          setExamQuestions(questions.map(mapExamQuestion));
          setCurrentIndex(0);
          setAnswers({});
          setFinished(false);
          return;
        }
        throw new Error("El examen no tiene preguntas disponibles.");
      })
      .catch(() => {
        setExamQuestions([]);
        setLoadError("No pudimos cargar el examen final desde el servidor.");
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
                onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}
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
                onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}
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

  return (
    <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto w-full max-w-5xl">
        <BackButton className="mb-4" onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}>
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
