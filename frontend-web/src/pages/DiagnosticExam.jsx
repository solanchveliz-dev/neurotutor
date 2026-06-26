import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CheckCircle2, Clock, Sparkles } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { diagnosticQuestions } from "../data/diagnosticQuestions";
import { getDiagnosticQuestions, submitDiagnostic, submitDiagnosticV2 } from "../services/diagnosticService";
import { getStudentId } from "../utils/auth";

function DiagnosticExam() {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isUsingFallback, setIsUsingFallback] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setIsLoading(true);
    getDiagnosticQuestions()
      .then((data) => {
        if (Array.isArray(data) && data.length > 0) {
          setQuestions(
            data.map((question) => ({
              id: question.id,
              textBeforeImage: question.text_before_image ?? question.textBeforeImage ?? "",
              textAfterImage: question.text_after_image ?? question.textAfterImage ?? "",
              image: question.image_url ?? question.imageUrl,
              options: question.options ?? [],
            }))
          );
          setIsUsingFallback(false);
        } else {
          setQuestions(diagnosticQuestions);
          setIsUsingFallback(true);
        }
      })
      .catch(() => {
        setQuestions(diagnosticQuestions);
        setIsUsingFallback(true);
      })
      .finally(() => setIsLoading(false));
  }, []);

  const currentQuestion = questions[currentIndex];
  const selectedAnswer = currentQuestion ? answers[currentQuestion.id] : undefined;
  const progress = questions.length ? ((currentIndex + 1) / questions.length) * 100 : 0;

  const handleSelect = (optionIndex) => {
    setAnswers({
      ...answers,
      [currentQuestion.id]: optionIndex,
    });
  };

  const getOptionClass = (index) =>
    `group flex min-h-[76px] w-full items-center gap-3 rounded-[22px] border-2 bg-white p-4 text-left text-sm font-bold leading-5 text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-nt-purple hover:shadow-md sm:text-base ${
      selectedAnswer === index
        ? "border-nt-purple bg-nt-purple/8 ring-4 ring-nt-purple/10"
        : "border-nt-border"
    }`;

  const calculateScore = (finalAnswers) => {
    return diagnosticQuestions.reduce((score, question) => {
      return finalAnswers[question.id] === question.correctAnswer
        ? score + 1
        : score;
    }, 0);
  };

  const getLevel = (score) => {
    if (score <= 4) return "Básico";
    if (score <= 7) return "Intermedio";
    return "Avanzado";
  };

  const handleNext = async () => {
    if (selectedAnswer === undefined) return;

    if (currentIndex < questions.length - 1) {
      setCurrentIndex(currentIndex + 1);
      return;
    }

    const studentId = getStudentId();

    if (!studentId) {
      setError("No se encontró el ID del estudiante. Inicia sesión nuevamente.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError("");

      if (isUsingFallback) {
        const labels = ["A", "B", "C", "D"];
        const respuestas = diagnosticQuestions.map((question) => {
          const answerIndex = answers[question.id];
          return labels[answerIndex];
        });
        const diagnosticResult = await submitDiagnostic(studentId, respuestas);
        localStorage.setItem("diagnosticResult", JSON.stringify(diagnosticResult));

        const score = calculateScore(answers);
        const level = getLevel(score);

        navigate("/diagnostic-result", {
          state: {
            score,
            total: diagnosticQuestions.length,
            level,
            answers,
            isFallback: true,
          },
        });
        return;
      }

      const diagnosticResult = await submitDiagnosticV2({
        student_id: Number(studentId),
        answers: questions.map((question) => ({
          question_id: question.id,
          selected_answer_index: answers[question.id],
        })),
      });

      navigate("/diagnostic-result", {
        state: {
          attemptId: diagnosticResult.attempt_id,
          scorePercentage: diagnosticResult.score_percentage,
          assignedLevel: diagnosticResult.assigned_level,
          correctAnswers: diagnosticResult.correct_answers,
          totalQuestions: diagnosticResult.total_questions,
          message: diagnosticResult.message,
        },
      });
    } catch {
      setError("No se pudo guardar el diagnóstico. Inténtalo nuevamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading || !currentQuestion) {
    return (
      <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4 py-8 text-nt-text-primary">
        <section className="relative z-10 mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </section>
      </main>
    );
  }

  return (
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute inset-0 bg-white/5" />
      <div className="pointer-events-none absolute right-3 top-[64%] z-0 hidden -translate-y-1/2 lg:block xl:right-8">
        <div className="relative mb-2 ml-auto w-fit rounded-[18px] bg-white px-4 py-2 text-center text-xs font-black leading-4 text-nt-text-primary shadow-[0_14px_32px_rgba(30,58,138,0.18)]">
          <span className="block">¡Tú puedes!</span>
          <span className="block">Confía en ti ⭐</span>
          <span className="absolute -bottom-2 right-8 size-4 rotate-45 bg-white" />
        </div>
        <img
          src="/assets/neo_diagnostic.png"
          alt=""
          aria-hidden="true"
          className="w-28 drop-shadow-[0_24px_36px_rgba(30,58,138,0.24)] xl:w-40"
        />
      </div>

      <section className="relative z-10 mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl flex-col items-center justify-center">
        <div className="mb-5 text-center">
          <Badge className="mb-3 h-7 rounded-full bg-white/82 px-4 text-xs font-black uppercase tracking-wide text-nt-blue shadow-sm ring-1 ring-nt-blue/10 hover:bg-white/82">
            Diagnóstico inicial
          </Badge>
          <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
            Mide tu punto de partida
          </h1>
          <div className="mt-3 flex flex-wrap items-center justify-center gap-2">
            <span className="inline-flex items-center gap-2 rounded-full bg-white/82 px-4 py-2 text-xs font-black text-nt-text-primary shadow-sm">
              <CheckCircle2 className="size-4 text-nt-green" aria-hidden="true" />
              Pregunta {currentIndex + 1} de {questions.length}
            </span>
            <span className="inline-flex items-center gap-2 rounded-full bg-white/82 px-4 py-2 text-xs font-black text-amber-700 shadow-sm">
              <Clock className="size-4" aria-hidden="true" />
              Tiempo estimado: 10 min
            </span>
          </div>
        </div>

        <div className="mb-4 flex w-full max-w-4xl items-center gap-3 px-1">
          <Progress
            value={progress}
            className="h-3 flex-1 bg-white/70 shadow-sm [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-nt-blue [&_[data-slot=progress-indicator]]:to-nt-purple"
          />
          <span className="w-10 text-right text-xs font-black text-nt-text-primary">
            {Math.round(progress)}%
          </span>
        </div>

        <Card className="w-full max-w-4xl rounded-[32px] border border-white/85 bg-white/88 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
          <CardContent className="p-5 sm:p-7 lg:p-8">
            {error && (
              <p className="mb-5 rounded-[18px] border border-red-200 bg-red-50 px-4 py-3 text-sm font-bold text-red-600">
                {error}
              </p>
            )}

            <div className="space-y-5">
              <p className="whitespace-pre-line text-center text-base font-black leading-8 text-slate-800 sm:text-xl">
                {currentQuestion.textBeforeImage}
              </p>

              {currentQuestion.image && (
                <div className="flex justify-center rounded-[26px] border border-nt-border bg-white p-3 shadow-sm">
                  <img
                    src={currentQuestion.image}
                    alt={`Pregunta ${currentQuestion.id}`}
                    className="max-h-80 w-full max-w-2xl rounded-[18px] object-contain"
                  />
                </div>
              )}

              {currentQuestion.textAfterImage && (
                <p className="whitespace-pre-line text-center text-base font-semibold leading-8 text-slate-800 sm:text-lg">
                  {currentQuestion.textAfterImage}
                </p>
              )}
            </div>

            <div className="mt-6 grid gap-3 md:grid-cols-2">
              {currentQuestion.options.map((option, index) => (
                <button
                  key={index}
                  className={getOptionClass(index)}
                  onClick={() => handleSelect(index)}
                >
                  <span
                    className={`grid size-9 shrink-0 place-items-center rounded-full text-sm font-black transition ${
                      selectedAnswer === index
                        ? "bg-nt-purple text-white"
                        : "bg-nt-purple/10 text-nt-purple group-hover:bg-nt-purple group-hover:text-white"
                    }`}
                  >
                    {String.fromCharCode(65 + index)}
                  </span>
                  <span>{option}</span>
                </button>
              ))}
            </div>

            <Button
              className="mt-6 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black uppercase tracking-wide text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
              disabled={selectedAnswer === undefined || isSubmitting}
              onClick={handleNext}
            >
              <Sparkles className="size-4" aria-hidden="true" />
              {currentIndex === questions.length - 1
                ? "Finalizar diagnóstico"
                : "Siguiente"}
            </Button>
          </CardContent>
        </Card>

        <div className="mt-4 flex w-full max-w-4xl items-center gap-3 rounded-[24px] border border-white/80 bg-white/78 p-4 text-nt-text-primary shadow-[0_18px_42px_rgba(37,99,235,0.14)] backdrop-blur-xl">
          <div className="grid size-10 shrink-0 place-items-center rounded-full bg-gradient-to-br from-nt-blue/10 to-nt-purple/10 shadow-sm">
            <img
              src="/assets/icon_hint.webp"
              alt=""
              aria-hidden="true"
              className="size-7 object-contain"
            />
          </div>
          <p className="text-sm font-bold leading-6 text-nt-text-primary">
            Consejo: Lee con calma la pregunta y selecciona una alternativa para continuar.
          </p>
        </div>
      </section>
    </main>
  );
}

export default DiagnosticExam;
