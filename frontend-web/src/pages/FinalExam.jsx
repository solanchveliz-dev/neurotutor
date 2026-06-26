import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, ArrowRight, CheckCircle2, ShieldCheck, Trophy, XCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { modulesData } from "../data/modulesData";
import {
  getExamPassed,
  getFinalExam,
  submitExamV2,
} from "../services/learningService";
import { getModuleProgress } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const fallbackExamQuestions = [
  {
    question: "Que representa el denominador de una fraccion?",
    options: [
      "Las partes tomadas",
      "El total de partes iguales",
      "La respuesta",
      "El numero mayor",
    ],
    correctAnswer: 1,
  },
  {
    question: "Cual fraccion es equivalente a 1/2?",
    options: ["2/4", "1/3", "3/5", "4/5"],
    correctAnswer: 0,
  },
  {
    question: "Si una figura se divide en 4 partes iguales y tomas 3, que fraccion tienes?",
    options: ["1/4", "4/3", "3/4", "2/4"],
    correctAnswer: 2,
  },
  {
    question: "Cual es el numerador en 7/10?",
    options: ["10", "7", "17", "3"],
    correctAnswer: 1,
  },
  {
    question: "Que fraccion representa un entero completo?",
    options: ["1/4", "2/8", "4/4", "3/5"],
    correctAnswer: 2,
  },
];

function mapExamQuestion(question) {
  return {
    question: question.question,
    options: question.options ?? [],
    correctAnswer: question.correctAnswerIndex,
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

function getLevelCode(level) {
  const levelName = inferLevelName(getTitle(level));

  if (levelName === "Intermedio") return "I";
  if (levelName === "Avanzado") return "A";
  return "B";
}

function FinalExam() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId } = useParams();
  const routeModule = location.state?.module;
  const routeLevel = location.state?.level;
  const [examQuestions, setExamQuestions] = useState(fallbackExamQuestions);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [finished, setFinished] = useState(false);
  const [submitResult, setSubmitResult] = useState(null);
  const [alreadyPassed, setAlreadyPassed] = useState(false);
  const [isUsingFallback, setIsUsingFallback] = useState(true);

  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(moduleId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => item.unlocked) ?? fallbackModule?.levels[0];
  const module = routeModule ?? fallbackModule;
  const level = routeLevel ?? fallbackLevel;
  const moduleTitle = getTitle(routeModule, getTitle(fallbackModule, "Modulo"));
  const levelName = inferLevelName(getTitle(routeLevel, getTitle(fallbackLevel, "Nivel")));
  const levelForSubmit = routeLevel ?? fallbackLevel;
  const backLevelId = routeLevel?.id ?? routeLevel?.levelId;
  const backModuleId = routeModule?.id ?? moduleId;
  const backPath =
    backModuleId && backLevelId
      ? `/module/${backModuleId}/level/${backLevelId}`
      : `/module/${moduleId}`;

  useEffect(() => {
    const studentId = getStudentId();

    getFinalExam(moduleId)
      .then((questions) => {
        if (Array.isArray(questions) && questions.length > 0) {
          setExamQuestions(questions.map(mapExamQuestion));
          setIsUsingFallback(false);
          setCurrentIndex(0);
          setAnswers({});
          setFinished(false);
        }
      })
      .catch(() => {
        setExamQuestions(fallbackExamQuestions);
        setIsUsingFallback(true);
      });

    if (studentId) {
      getExamPassed(studentId, moduleId)
        .then((result) => setAlreadyPassed(result.alreadyPassed === true))
        .catch(() => setAlreadyPassed(false));
    }
  }, [moduleId]);

  const currentQuestion = examQuestions[currentIndex];
  const selectedAnswer = answers[currentIndex];
  const progress = ((currentIndex + 1) / examQuestions.length) * 100;

  const handleSelect = (index) => {
    setAnswers({
      ...answers,
      [currentIndex]: index,
    });
  };

  const calculateScore = () => {
    return examQuestions.reduce((score, question, index) => {
      return answers[index] === question.correctAnswer ? score + 1 : score;
    }, 0);
  };

  const submitExam = async () => {
    const studentId = getStudentId();
    const score = calculateScore();
    const percentage = Math.round((score / examQuestions.length) * 100);

    if (!studentId || !Number.isFinite(Number(moduleId)) || isUsingFallback) {
      setSubmitResult({
        message: "Modo demo: el resultado no se envio al servidor.",
      });
      return;
    }

    try {
      const result = await submitExamV2({
        studentId: Number(studentId),
        moduloId: Number(moduleId),
        level: getLevelCode(levelForSubmit),
        score: percentage,
      });
      let refreshedProgress = null;
      try {
        refreshedProgress = await getModuleProgress(studentId, moduleId);
      } catch {
        refreshedProgress = null;
      }
      setSubmitResult({ ...result, moduleProgress: refreshedProgress });
    } catch {
      setSubmitResult(null);
    }
  };

  const handleNext = () => {
    if (selectedAnswer === undefined) return;

    if (currentIndex < examQuestions.length - 1) {
      setCurrentIndex(currentIndex + 1);
    } else {
      setFinished(true);
      submitExam();
    }
  };

  if (!module || !level) {
    return (
      <main className="min-h-screen bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
        <section className="mx-auto flex min-h-[calc(100vh-4rem)] max-w-xl items-center justify-center">
          <Card className="w-full rounded-[32px] border border-white/80 bg-white/90 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
            <CardContent className="p-8">
              <h1 className="text-2xl font-black text-nt-text-primary">Examen no encontrado</h1>
              <Button
                type="button"
                className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 text-sm font-black text-white hover:bg-blue-700"
                onClick={() => navigate("/learning-path")}
              >
                Volver a módulos
              </Button>
            </CardContent>
          </Card>
        </section>
      </main>
    );
  }

  if (finished) {
    const score = calculateScore();
    const percentage = Math.round((score / examQuestions.length) * 100);
    const approved = percentage >= 70;

    return (
      <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
        <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
        <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

        <section className="relative mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-2xl items-center justify-center">
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
                <strong>{examQuestions.length}</strong> respuestas correctas.
              </p>

              <div
                className={`mx-auto mt-6 w-fit rounded-[24px] px-8 py-4 text-4xl font-black text-white shadow-lg ${
                  approved ? "bg-nt-green" : "bg-nt-red"
                }`}
              >
                {percentage}%
              </div>

              <p className="mx-auto mt-5 max-w-md text-sm font-bold leading-6 text-nt-text-secondary">
                {submitResult?.message ??
                  (alreadyPassed
                    ? "Ya habías aprobado este examen anteriormente."
                    : approved
                      ? "Excelente trabajo. Puedes continuar con el siguiente nivel."
                      : "Te recomendamos revisar la teoría y repetir los ejercicios antes de intentarlo otra vez.")}
              </p>

              <Button
                type="button"
                className="mt-7 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                onClick={() => navigate("/student-dashboard")}
              >
                Volver al panel
                <ArrowRight className="size-4" aria-hidden="true" />
              </Button>
            </CardContent>
          </Card>
        </section>
      </main>
    );
  }

  return (
    <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto w-full max-w-5xl">
        <Button
          type="button"
          variant="ghost"
          className="mb-4 h-10 rounded-[18px] bg-white/75 px-4 text-sm font-black text-nt-blue shadow-sm hover:bg-white hover:text-nt-purple"
          onClick={() => navigate(backPath, { state: { module: routeModule, level: routeLevel } })}
        >
          <ArrowLeft className="size-4" aria-hidden="true" />
          Volver al módulo
        </Button>

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

            <Button
              type="button"
              className="mt-6 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
              disabled={selectedAnswer === undefined}
              onClick={handleNext}
            >
              {currentIndex === examQuestions.length - 1
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
