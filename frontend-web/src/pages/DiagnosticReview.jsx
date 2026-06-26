import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ArrowRight, CheckCircle2, XCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { diagnosticQuestions } from "../data/diagnosticQuestions";
import { getDiagnosticReview, getLatestDiagnosticReview } from "../services/diagnosticService";
import { getStudentId } from "../utils/auth";

const normalizeReviewQuestion = (item) => ({
  id: item.question_id,
  textBeforeImage: item.text_before_image ?? "",
  textAfterImage: item.text_after_image ?? "",
  image: item.image_url,
  options: item.options ?? [],
  correctAnswer: item.correct_answer_index,
  selectedAnswer: item.selected_answer_index,
  correct: item.correct,
  explanation: item.explanation,
});

function DiagnosticReview() {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedQuestionIndex, setSelectedQuestionIndex] = useState(0);
  const [review, setReview] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [reviewError, setReviewError] = useState("");

  useEffect(() => {
    if (location.state?.isFallback) return;

    const attemptId = location.state?.attemptId;
    const studentId = getStudentId();

    if (!attemptId && !studentId) return;

    setIsLoading(true);
    setReviewError("");

    const request = attemptId ? getDiagnosticReview(attemptId) : getLatestDiagnosticReview(studentId);

    request
      .then((data) => setReview(data))
      .catch(() => setReviewError("No se pudo cargar la revision desde el servidor."))
      .finally(() => setIsLoading(false));
  }, [location.state]);

  const backendQuestions = review?.questions?.map(normalizeReviewQuestion) ?? [];
  const isBackendReview = backendQuestions.length > 0;
  const questions = isBackendReview ? backendQuestions : diagnosticQuestions;
  const answers = location.state?.answers ?? {};
  const score = review?.correct_answers ?? location.state?.score ?? 0;
  const total = review?.total_questions ?? location.state?.total ?? questions.length;
  const percentage = total ? Math.round((score / total) * 100) : 0;
  const incorrectCount = total - score;
  const selectedQuestion = questions[selectedQuestionIndex] ?? questions[0];
  const selectedIndex = isBackendReview ? selectedQuestion?.selectedAnswer : answers[selectedQuestion?.id];
  const isCorrect = isBackendReview
    ? selectedQuestion?.correct
    : selectedIndex === selectedQuestion?.correctAnswer;

  const getOptionClass = (optionIndex) => {
    const isUserAnswer = selectedIndex === optionIndex;
    const isCorrectAnswer = selectedQuestion?.correctAnswer === optionIndex;

    if (isCorrectAnswer) return "border-green-300 bg-green-50 text-green-800 ring-2 ring-green-100";
    if (isUserAnswer && !isCorrectAnswer) return "border-red-300 bg-red-50 text-red-800 ring-2 ring-red-100";
    return "border-nt-border bg-white text-nt-text-primary";
  };

  return (
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center bg-no-repeat px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute inset-0 bg-white/5" />
      <section className="relative mx-auto w-full max-w-7xl">
        <header className="mb-6 text-center">
          <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
            Ver respuestas
          </h1>
          <p className="mt-2 text-sm font-bold text-nt-text-secondary sm:text-base">
            {reviewError || "Revisa tus respuestas y aprende de cada pregunta."}
          </p>
        </header>

        {isLoading && (
          <div className="mb-5 flex justify-center">
            <div className="h-10 w-10 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
          </div>
        )}

        <div className="grid gap-5 lg:grid-cols-[88px_minmax(0,1fr)_280px] lg:items-start">
          <Card className="rounded-[30px] border border-white/80 bg-white/82 p-0 shadow-[0_18px_46px_rgba(37,99,235,0.14)] backdrop-blur-xl">
            <CardContent className="p-4">
              <div className="flex gap-2 overflow-x-auto pb-1 lg:flex-col lg:overflow-visible lg:pb-0">
                {questions.map((question, index) => {
                  const questionAnswer = isBackendReview ? question.selectedAnswer : answers[question.id];
                  const questionCorrect = isBackendReview ? question.correct : questionAnswer === question.correctAnswer;
                  const isSelected = selectedQuestionIndex === index;

                  return (
                    <button
                      key={question.id}
                      type="button"
                      onClick={() => setSelectedQuestionIndex(index)}
                      className="relative shrink-0"
                      aria-label={`Ver pregunta ${index + 1}`}
                    >
                      <span
                        className={`grid size-11 place-items-center rounded-full text-sm font-black text-white shadow-sm transition ${
                          questionCorrect ? "bg-green-500" : "bg-red-500"
                        } ${isSelected ? "ring-4 ring-nt-blue/20" : ""}`}
                      >
                        {index + 1}
                      </span>
                    </button>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          <Card className="rounded-[34px] border border-white/85 bg-white/90 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
            <CardContent className="p-5 sm:p-7">
              <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <p className="text-xs font-black uppercase tracking-wide text-nt-text-secondary">
                  Pregunta {selectedQuestionIndex + 1} de {total}
                </p>
                <span
                  className={`inline-flex w-fit items-center gap-2 rounded-full px-3 py-1.5 text-xs font-black ${
                    isCorrect ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"
                  }`}
                >
                  {isCorrect ? <CheckCircle2 className="size-4" /> : <XCircle className="size-4" />}
                  {isCorrect ? "Correcta" : "Incorrecta"}
                </span>
              </div>

              <div className="space-y-4">
                <p className="whitespace-pre-line text-base font-black leading-8 text-slate-800 sm:text-lg">
                  {selectedQuestion?.textBeforeImage}
                </p>

                {selectedQuestion?.image && (
                  <div className="flex justify-center rounded-[26px] border border-nt-border bg-white p-3 shadow-sm">
                    <img
                      src={selectedQuestion.image}
                      alt={`Pregunta ${selectedQuestion.id}`}
                      className="max-h-72 w-full max-w-2xl rounded-[16px] object-contain"
                    />
                  </div>
                )}

                {selectedQuestion?.textAfterImage && (
                  <p className="whitespace-pre-line text-base font-semibold leading-8 text-slate-800 sm:text-lg">
                    {selectedQuestion.textAfterImage}
                  </p>
                )}
              </div>

              <div className="mt-6 grid gap-3 md:grid-cols-2">
                {selectedQuestion?.options.map((option, index) => {
                  const isUserAnswer = selectedIndex === index;
                  const isCorrectAnswer = selectedQuestion.correctAnswer === index;

                  return (
                    <div
                      key={index}
                      className={`rounded-[22px] border-2 p-4 text-sm font-bold leading-6 shadow-sm ${getOptionClass(index)}`}
                    >
                      <div className="mb-2 flex items-center gap-2">
                        <span className="grid size-8 shrink-0 place-items-center rounded-full bg-white text-xs font-black text-nt-text-primary shadow-sm">
                          {String.fromCharCode(65 + index)}
                        </span>
                        <div className="flex flex-wrap gap-2">
                          {isUserAnswer && (
                            <span className={`rounded-full px-2 py-1 text-[10px] font-black uppercase ${
                              isCorrectAnswer ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
                            }`}>
                              Tu respuesta
                            </span>
                          )}
                          {isCorrectAnswer && (
                            <span className="rounded-full bg-green-100 px-2 py-1 text-[10px] font-black uppercase text-green-700">
                              Correcta
                            </span>
                          )}
                        </div>
                      </div>
                      <p>{option}</p>
                    </div>
                  );
                })}
              </div>

              <div className="mt-6 rounded-[26px] border border-nt-blue/15 bg-nt-sky/65 p-5">
                <p className="text-xs font-black uppercase tracking-wide text-nt-blue">Explicacion</p>
                <p className="mt-2 text-sm font-bold leading-6 text-nt-text-secondary">
                  {selectedQuestion?.explanation ??
                    "Compara el enunciado con la alternativa correcta y revisa el procedimiento paso a paso para reforzar este tema."}
                </p>
              </div>
            </CardContent>
          </Card>

          <Card className="rounded-[34px] border border-white/85 bg-white/86 p-0 shadow-[0_20px_58px_rgba(37,99,235,0.16)] backdrop-blur-xl">
            <CardContent className="p-5">
              <div className="text-center">
                <p className="text-5xl font-black text-nt-blue">{percentage}%</p>
                <p className="mt-1 text-sm font-black text-nt-text-primary">rendimiento</p>
              </div>

              <div className="mt-5 grid gap-3">
                <div className="rounded-[22px] bg-green-50 p-4">
                  <p className="text-2xl font-black text-green-700">{score}</p>
                  <p className="text-xs font-black uppercase tracking-wide text-green-700">correctas</p>
                </div>
                <div className="rounded-[22px] bg-red-50 p-4">
                  <p className="text-2xl font-black text-red-700">{incorrectCount}</p>
                  <p className="text-xs font-black uppercase tracking-wide text-red-700">incorrectas</p>
                </div>
                <div className="rounded-[22px] bg-nt-sky/70 p-4">
                  <p className="text-2xl font-black text-nt-text-primary">{total}</p>
                  <p className="text-xs font-black uppercase tracking-wide text-nt-text-secondary">respondidas</p>
                </div>
              </div>

              <Button
                type="button"
                className="mt-5 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple px-5 text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.22)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                onClick={() => navigate("/student-dashboard")}
              >
                Continuar al dashboard
                <ArrowRight className="size-4" />
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </main>
  );
}

export default DiagnosticReview;
