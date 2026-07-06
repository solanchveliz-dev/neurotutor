import { useCallback, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ArrowLeft, ArrowRight, CheckCircle2, ClipboardList, Grid2X2, Lightbulb, XCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
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
  topic: item.topic,
  explanation: item.explanation,
  advice: item.advice ?? item.tip ?? item.consejo ?? null,
});

function FractionText({ children }) {
  return String(children ?? "").split(/(\d+\s*\/\s*\d+)/g).map((part, index) => {
    const fraction = part.match(/^(\d+)\s*\/\s*(\d+)$/);
    if (!fraction) return <span key={index}>{part}</span>;
    return <span key={index} className="inline-grid min-w-8 align-middle text-center text-lg font-black leading-none"><span className="border-b-2 border-current px-1 pb-0.5">{fraction[1]}</span><span className="px-1 pt-0.5">{fraction[2]}</span></span>;
  });
}

function DiagnosticReview() {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedQuestionIndex, setSelectedQuestionIndex] = useState(0);
  const [review, setReview] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [reviewError, setReviewError] = useState("");
  const [filter, setFilter] = useState("all");
  const attemptId = location.state?.attemptId;

  const loadReview = useCallback(() => {
    const studentId = getStudentId();

    setIsLoading(true);
    setReviewError("");
    setReview(null);
    setSelectedQuestionIndex(0);

    if (!attemptId && !studentId) {
      setReviewError("No pudimos identificar el intento ni al estudiante conectado.");
      setIsLoading(false);
      return;
    }

    const request = attemptId ? getDiagnosticReview(attemptId) : getLatestDiagnosticReview(studentId);

    request
      .then((data) => setReview(data))
      .catch(() => setReviewError("No se pudo cargar la revisión desde el servidor."))
      .finally(() => setIsLoading(false));
  }, [attemptId]);

  useEffect(() => {
    loadReview();
  }, [loadReview]);

  const questions = Array.isArray(review?.questions)
    ? review.questions.map(normalizeReviewQuestion)
    : [];
  const score = review?.correct_answers ?? 0;
  const total = review?.total_questions ?? 0;
  const percentage = review?.score_percentage ?? 0;
  const incorrectCount = total - score;
  const filteredQuestions = questions.filter((question) => filter === "all" || (filter === "correct" ? question.correct : !question.correct));
  const selectedQuestion = filteredQuestions.includes(questions[selectedQuestionIndex]) ? questions[selectedQuestionIndex] : filteredQuestions[0] ?? questions[0];
  const visibleSelectedIndex = questions.findIndex((question) => question.id === selectedQuestion?.id);
  const selectedIndex = selectedQuestion?.selectedAnswer;
  const isCorrect = selectedQuestion?.correct === true;
  const assignedLevel = review?.assigned_level;

  const getOptionClass = (optionIndex) => {
    const isUserAnswer = selectedIndex === optionIndex;
    const isCorrectAnswer = selectedQuestion?.correctAnswer === optionIndex;

    if (isCorrectAnswer) return "border-green-300 bg-green-50 text-green-800 ring-2 ring-green-100";
    if (isUserAnswer && !isCorrectAnswer) return "border-red-300 bg-red-50 text-red-800 ring-2 ring-red-100";
    return "border-nt-border bg-white text-nt-text-primary";
  };

  if (isLoading) {
    return (
      <main className="grid min-h-screen place-items-center bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4">
        <Card className="w-full max-w-lg rounded-[32px] border border-white/85 bg-white/90 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
          <CardContent className="p-8">
            <div className="mx-auto h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
            <p className="mt-4 font-black text-nt-text-primary">Cargando tu revisión...</p>
          </CardContent>
        </Card>
      </main>
    );
  }

  if (reviewError) {
    return (
      <main className="grid min-h-screen place-items-center bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4">
        <Card className="w-full max-w-lg rounded-[32px] border border-amber-200 bg-white/92 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
          <CardContent className="p-8">
            <h1 className="text-2xl font-black text-nt-text-primary">Revisión no disponible</h1>
            <p className="mt-3 text-sm font-semibold text-nt-text-secondary">{reviewError}</p>
            <Button type="button" className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 font-black text-white" onClick={loadReview}>
              Reintentar
            </Button>
          </CardContent>
        </Card>
      </main>
    );
  }

  if (questions.length === 0) {
    return (
      <main className="grid min-h-screen place-items-center bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center px-4">
        <Card className="w-full max-w-lg rounded-[32px] border border-white/85 bg-white/92 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
          <CardContent className="p-8">
            <h1 className="text-2xl font-black text-nt-text-primary">Sin respuestas para revisar</h1>
            <p className="mt-3 text-sm font-semibold text-nt-text-secondary">El servidor no devolvió preguntas para este intento.</p>
            <Button type="button" className="mt-5 h-11 rounded-[18px] bg-nt-blue px-5 font-black text-white" onClick={() => navigate("/student-dashboard")}>
              Volver al dashboard
            </Button>
          </CardContent>
        </Card>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center bg-fixed p-3 text-nt-text-primary sm:p-5">
      <Card className="mx-auto w-full max-w-[1500px] overflow-hidden rounded-[34px] border border-white/90 bg-white/94 p-0 shadow-[0_28px_80px_rgba(30,58,138,0.2)]"><CardContent className="p-4 sm:p-6">
        <header className="grid items-center gap-5 lg:grid-cols-[minmax(0,1fr)_520px]"><div className="grid items-center gap-4 sm:grid-cols-[150px_minmax(0,1fr)]"><img src="/assets/neo_lupa.png" alt="NEO revisando respuestas" className="mx-auto h-36 w-full object-contain drop-shadow-md" /><div><h1 className="text-3xl font-black text-nt-purple sm:text-4xl">Revisión del diagnóstico</h1><h2 className="mt-2 text-lg font-black">Descubre cuáles fueron tus respuestas</h2><p className="mt-3 max-w-xl text-sm font-semibold leading-6 text-slate-600">Revisa cada pregunta para comprender mejor tu punto de partida.</p></div></div><div className="grid grid-cols-3 gap-3">{[{ label: "Correctas", value: score, icon: CheckCircle2, tone: "text-green-600" }, { label: "Incorrectas", value: incorrectCount, icon: XCircle, tone: "text-red-600" }, { label: "Total", value: total, icon: ClipboardList, tone: "text-blue-600" }].map(({ label, value, icon: Icon, tone }) => <article key={label} className="grid min-h-32 place-items-center rounded-[20px] border border-slate-100 bg-white p-3 text-center shadow-sm"><Icon className={`size-9 ${tone}`} /><p className={`text-xs font-black ${tone}`}>{label}</p><strong className="text-2xl font-black">{value}</strong></article>)}</div></header>

        <div className="mt-5 grid grid-cols-3 gap-2 sm:max-w-2xl">{[{ key: "all", label: "Todos", icon: Grid2X2 }, { key: "correct", label: "Correctas", icon: CheckCircle2 }, { key: "incorrect", label: "Incorrectas", icon: XCircle }].map(({ key, label, icon: Icon }) => <button key={key} type="button" onClick={() => setFilter(key)} className={`flex items-center justify-center gap-2 rounded-[14px] border px-3 py-3 text-sm font-black transition ${filter === key ? "border-nt-purple bg-nt-purple text-white" : "border-violet-100 bg-white text-nt-text-primary"}`}><Icon className="size-4" />{label}</button>)}</div>

        <div className="mt-4 grid gap-4 lg:grid-cols-[230px_minmax(0,1fr)_290px]">
          <aside className="max-h-[650px] overflow-y-auto rounded-[24px] border border-slate-100 bg-slate-50/70 p-3"><div className="grid gap-2">{filteredQuestions.map((question) => { const originalIndex = questions.findIndex((item) => item.id === question.id); const selected = selectedQuestion?.id === question.id; return <button key={question.id} type="button" onClick={() => setSelectedQuestionIndex(originalIndex)} className={`flex items-center gap-3 rounded-[14px] border px-3 py-3 text-left text-sm font-black ${selected ? question.correct ? "border-green-400 bg-green-50 text-green-700" : "border-red-400 bg-red-50 text-red-700" : "border-slate-200 bg-white text-nt-text-primary"}`}>{question.correct ? <CheckCircle2 className="size-5 shrink-0 text-green-600" /> : <XCircle className="size-5 shrink-0 text-red-600" />}Pregunta {originalIndex + 1}</button>; })}{filteredQuestions.length === 0 && <p className="p-4 text-center text-sm font-bold text-slate-500">No hay preguntas en este filtro.</p>}</div></aside>

          <section className={`min-w-0 rounded-[24px] border-2 bg-white p-5 shadow-sm ${isCorrect ? "border-green-200" : "border-red-200"}`}><div className="flex flex-wrap items-center justify-between gap-2"><div><p className={`font-black ${isCorrect ? "text-green-700" : "text-red-700"}`}>Pregunta {visibleSelectedIndex + 1}</p>{selectedQuestion?.topic && <span className="mt-1 inline-flex rounded-full bg-violet-50 px-3 py-1 text-xs font-black text-violet-700">{selectedQuestion.topic}</span>}</div><span className={`flex items-center gap-2 rounded-full px-3 py-1.5 text-xs font-black ${isCorrect ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"}`}>{isCorrect ? <CheckCircle2 className="size-4" /> : <XCircle className="size-4" />}{isCorrect ? "Correcta" : "Incorrecta"}</span></div><p className="mt-4 whitespace-pre-line text-xl font-black leading-8"><FractionText>{selectedQuestion?.textBeforeImage}</FractionText></p>{selectedQuestion?.image && <div className="mt-4 flex justify-center overflow-hidden rounded-[18px] bg-sky-50 p-3"><img src={selectedQuestion.image} alt={`Pregunta ${selectedQuestion.id}`} className="max-h-64 w-full object-contain" /></div>}{selectedQuestion?.textAfterImage && <p className="mt-3 whitespace-pre-line text-base font-semibold leading-7"><FractionText>{selectedQuestion.textAfterImage}</FractionText></p>}
            <div className="mt-5 grid gap-3 sm:grid-cols-2">{selectedQuestion?.options.map((option, index) => { const user = selectedIndex === index; const correct = selectedQuestion.correctAnswer === index; return <article key={index} className={`rounded-[18px] border-2 p-3 text-sm font-bold ${getOptionClass(index)}`}><div className="flex items-center gap-2"><span className="grid size-8 shrink-0 place-items-center rounded-full bg-white shadow-sm">{String.fromCharCode(65 + index)}</span><FractionText>{option}</FractionText></div><div className="mt-2 flex flex-wrap gap-1">{user && <span className={`rounded-full px-2 py-1 text-[10px] font-black uppercase ${correct ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}>Tu respuesta</span>}{correct && <span className="rounded-full bg-green-100 px-2 py-1 text-[10px] font-black uppercase text-green-700">Respuesta correcta</span>}</div></article>; })}</div>
            <div className="mt-5 grid gap-3 sm:grid-cols-2"><article className={`rounded-[18px] border p-4 ${isCorrect ? "border-green-200 bg-green-50" : "border-red-200 bg-red-50"}`}><h3 className="font-black">Tu respuesta</h3><p className="mt-2 text-sm font-bold"><FractionText>{selectedQuestion?.options?.[selectedIndex] ?? "Sin respuesta"}</FractionText></p></article><article className="rounded-[18px] border border-green-200 bg-green-50 p-4"><h3 className="font-black text-green-700">Respuesta correcta</h3><p className="mt-2 text-sm font-bold"><FractionText>{selectedQuestion?.options?.[selectedQuestion.correctAnswer]}</FractionText></p></article></div><article className="mt-3 rounded-[18px] border border-blue-100 bg-blue-50 p-4"><h3 className="flex items-center gap-2 font-black text-blue-700"><Lightbulb className="size-5" />Explicación</h3><p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{selectedQuestion?.explanation || "No hay explicación disponible para esta pregunta."}</p></article>
          </section>

          <aside className="rounded-[24px] border border-violet-100 bg-violet-50/70 p-5 text-center shadow-sm"><h2 className="text-xl font-black text-nt-purple">Consejo de NEO</h2><img src="/assets/neo_ideas.png" alt="NEO compartiendo un consejo" className="mx-auto mt-4 h-44 w-full object-contain drop-shadow-md" /><p className="mt-4 rounded-[18px] bg-white p-4 text-sm font-bold leading-6 text-slate-700 shadow-sm">{selectedQuestion?.advice || "Revisa la explicación de esta pregunta para reforzar tu aprendizaje."}</p><div className="mt-5 grid gap-3"><Button type="button" variant="outline" onClick={() => navigate('/diagnostic-result', { state: { ...location.state, attemptId: review?.attempt_id, correctAnswers: score, totalQuestions: total, scorePercentage: percentage, assignedLevel } })} className="h-12 rounded-[16px] border-2 border-nt-purple font-black text-nt-purple"><ArrowLeft className="size-4" />Volver al resultado</Button><Button type="button" onClick={() => navigate('/student-dashboard')} className="h-12 rounded-[16px] bg-gradient-to-r from-nt-blue to-nt-purple font-black text-white">Comenzar mi ruta<ArrowRight className="size-4" /></Button></div></aside>
        </div>
      </CardContent></Card>
    </main>
  );
}

export default DiagnosticReview;
