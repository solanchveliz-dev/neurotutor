import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { getDiagnosticQuestions, submitDiagnosticV2 } from "../services/diagnosticService";
import { getStudentId } from "../utils/auth";

function FractionText({ children }) {
  const parts = String(children ?? "").split(/(\d+\s*\/\s*\d+)/g);
  return parts.map((part, index) => {
    const fraction = part.match(/^(\d+)\s*\/\s*(\d+)$/);
    if (!fraction) return <span key={index}>{part}</span>;
    return <span key={index} className="inline-grid min-w-8 align-middle text-center text-lg font-black leading-none"><span className="border-b-2 border-current px-1 pb-0.5">{fraction[1]}</span><span className="px-1 pt-0.5">{fraction[2]}</span></span>;
  });
}

function DiagnosticExam() {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
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
        } else {
          setQuestions([]);
          setError("El diagnóstico todavía no tiene preguntas disponibles.");
        }
      })
      .catch(() => {
        setQuestions([]);
        setError("No pudimos cargar el diagnóstico desde el servidor. Intenta nuevamente en unos minutos.");
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
    `group flex min-h-[74px] w-full items-center gap-3 rounded-[18px] border bg-white px-4 py-3 text-left text-base font-bold leading-6 text-nt-text-primary shadow-[0_6px_18px_rgba(76,29,149,0.08)] transition hover:border-nt-purple hover:shadow-md ${
      selectedAnswer === index
        ? "border-nt-purple bg-violet-50 ring-2 ring-nt-purple/10"
        : "border-violet-100"
    }`;

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
          unlockedAchievementCodes: diagnosticResult.unlocked_achievement_codes,
        },
      });
    } catch {
      setError("No se pudo guardar el diagnóstico. Inténtalo nuevamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4 py-8 text-nt-text-primary">
        <section className="relative z-10 mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </section>
      </main>
    );
  }

  if (error || !currentQuestion) {
    return (
      <main className="relative grid min-h-screen place-items-center bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4 py-8 text-nt-text-primary">
        <Card className="w-full max-w-xl rounded-[28px] border border-white/85 bg-white/92 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
          <CardContent className="p-8">
            <h1 className="text-2xl font-black">Diagnóstico no disponible</h1>
            <p className="mt-3 text-sm font-semibold leading-6 text-nt-text-secondary">{error || "No encontramos preguntas disponibles."}</p>
            <Button className="mt-6 rounded-[18px] bg-nt-blue px-5 text-white" onClick={() => window.location.reload()}>Reintentar</Button>
          </CardContent>
        </Card>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gradient-to-br from-[#bfe7ff] via-[#dff4ff] to-[#b9e2ff] p-3 text-nt-text-primary sm:p-5 lg:p-7">
      <Card className="mx-auto min-h-[calc(100vh-1.5rem)] w-full max-w-[1500px] overflow-hidden rounded-[32px] border border-white/90 bg-white p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] sm:min-h-[calc(100vh-2.5rem)] lg:min-h-[calc(100vh-3.5rem)]">
        <CardContent className="grid min-h-[inherit] p-0 lg:grid-cols-[32%_68%]">
          <aside className="relative flex min-w-0 flex-col overflow-hidden bg-gradient-to-br from-white via-sky-50 to-blue-50 p-5 sm:p-7 lg:p-8">
            <Button type="button" variant="outline" onClick={() => navigate(-1)} className="w-fit rounded-[16px] border-white bg-white px-4 font-black text-nt-blue shadow-md"><ArrowLeft className="size-5" />Volver</Button>
            <div className="mt-7 text-center">
              <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl"><span className="block">EXAMEN</span><span className="block bg-gradient-to-r from-nt-blue to-nt-purple bg-clip-text text-transparent">DIAGNÓSTICO</span></h1>
              <p className="mt-3 text-lg font-black text-nt-blue"><span className="text-amber-400">★</span> Descubramos tu nivel <span className="text-amber-400">★</span></p>
            </div>
            <img src="/assets/apuntes.png" alt="Apuntes del examen diagnóstico" className="mx-auto mt-5 h-52 w-full max-w-xs object-contain drop-shadow-[0_16px_22px_rgba(37,99,235,0.16)] lg:h-60" />
            <p className="mx-auto mt-3 max-w-sm rounded-[20px] border border-blue-100 bg-white/75 px-5 py-4 text-center text-sm font-bold leading-6 shadow-sm">Este examen nos ayudará a saber cuál es tu punto de partida para recomendarte el mejor camino de aprendizaje.</p>
            <div className="mt-auto grid items-end gap-3 pt-6 sm:grid-cols-[150px_minmax(0,1fr)] lg:grid-cols-[42%_58%]">
              <img src="/assets/neo_check.png" alt="NEO dando ánimo" className="mx-auto h-44 w-full object-contain drop-shadow-[0_16px_24px_rgba(37,99,235,0.18)]" />
              <div className="relative mb-4 rounded-[24px] border border-violet-100 bg-violet-50/90 p-5 text-center shadow-sm before:absolute before:-left-3 before:top-1/2 before:size-6 before:-translate-y-1/2 before:rotate-45 before:border-b before:border-l before:border-violet-100 before:bg-violet-50"><h2 className="font-black text-nt-text-primary"><span className="text-nt-purple">★</span> NEO dice</h2><p className="mt-3 text-sm font-bold leading-6">Lee con calma.<br />No importa equivocarte.</p></div>
            </div>
          </aside>

          <section className="flex min-w-0 flex-col bg-white">
            <header className="border-b border-violet-100 px-5 py-5 sm:px-8 lg:px-10">
              <h2 className="text-xl font-black sm:text-2xl">Pregunta {currentIndex + 1} de {questions.length}</h2>
              <div className="mt-4 flex items-center gap-4"><Progress value={progress} className="h-3 flex-1 bg-violet-100 [&_[data-slot=progress-indicator]]:bg-gradient-to-r [&_[data-slot=progress-indicator]]:from-nt-blue [&_[data-slot=progress-indicator]]:to-nt-purple" /><span className="shrink-0 text-sm font-black text-nt-purple">{Math.round(progress)}% completado</span></div>
            </header>
            <div className="flex flex-1 flex-col px-5 py-6 sm:px-8 lg:px-10">
              <p className="font-black text-nt-purple">Pregunta</p>
              <div className="mt-2 space-y-2">
                <p className="whitespace-pre-line text-xl font-black leading-8 text-nt-text-primary sm:text-2xl">{currentQuestion.textBeforeImage}</p>
              </div>
              {currentQuestion.image && <div className="mt-5 flex min-h-48 justify-center overflow-hidden rounded-[22px] border border-violet-100 bg-gradient-to-br from-violet-50 to-sky-50 p-3"><img src={currentQuestion.image} alt={`Pregunta ${currentQuestion.id}`} className="max-h-72 w-full object-contain" /></div>}
              {currentQuestion.textAfterImage && <p className="mt-3 whitespace-pre-line text-lg font-bold leading-7 text-nt-text-primary">{currentQuestion.textAfterImage}</p>}
              <div className="mt-4 grid gap-3">{currentQuestion.options.map((option, index) => <button key={index} type="button" className={getOptionClass(index)} onClick={() => handleSelect(index)}><span className={`size-6 shrink-0 rounded-full border-2 p-1 ${selectedAnswer === index ? "border-nt-purple" : "border-violet-200"}`}><span className={`block size-full rounded-full ${selectedAnswer === index ? "bg-nt-purple" : "bg-transparent"}`} /></span><span className="grid size-10 shrink-0 place-items-center rounded-full bg-gradient-to-br from-nt-blue to-nt-purple font-black text-white">{String.fromCharCode(65 + index)}</span><span className="flex flex-wrap items-center gap-1"><FractionText>{option}</FractionText></span></button>)}</div>
              <div className="mt-auto flex flex-col gap-3 pt-7 sm:flex-row sm:justify-between">
                <Button type="button" variant="outline" disabled={currentIndex === 0 || isSubmitting} onClick={() => setCurrentIndex((index) => Math.max(0, index - 1))} className="h-14 rounded-[16px] border-2 border-nt-purple px-8 font-black text-nt-purple"><ArrowLeft className="size-5" />Anterior</Button>
                <Button type="button" disabled={selectedAnswer === undefined || isSubmitting} onClick={handleNext} className="h-14 rounded-[16px] bg-gradient-to-r from-nt-blue to-nt-purple px-10 font-black text-white shadow-lg shadow-violet-200">{currentIndex === questions.length - 1 ? "Finalizar diagnóstico" : "Siguiente"}<ArrowRight className="size-5" /></Button>
              </div>
            </div>
          </section>
        </CardContent>
      </Card>
    </main>
  );
}

export default DiagnosticExam;
