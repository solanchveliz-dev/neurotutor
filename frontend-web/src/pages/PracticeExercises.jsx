import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, ArrowRight, Bot, CheckCircle2, Lightbulb, Star, XCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { modulesData } from "../data/modulesData";
import { getLearningContent } from "../services/learningService";

const fallbackExercises = [
  {
    question: "Cual fraccion representa la mitad de una pizza?",
    options: ["1/4", "1/2", "2/3", "3/4"],
    correctAnswer: 1,
    explanation: "La mitad significa dividir el todo en 2 partes iguales y tomar 1 parte.",
    points: 10,
  },
  {
    question: "Si tienes 3/4 de una barra de chocolate, que indica el numero 4?",
    options: [
      "Las partes que tomaste",
      "El total de partes iguales",
      "El numero de chocolates",
      "La respuesta final",
    ],
    correctAnswer: 1,
    explanation: "El denominador indica en cuantas partes iguales se divide el todo.",
    points: 10,
  },
  {
    question: "Cual es el numerador en la fraccion 5/8?",
    options: ["8", "5", "13", "3"],
    correctAnswer: 1,
    explanation: "El numerador es el numero de arriba. En 5/8, el numerador es 5.",
    points: 10,
  },
];

function mapExercise(exercise) {
  return {
    question: exercise.question,
    options: exercise.options ?? [],
    correctAnswer: exercise.correctAnswerIndex,
    explanation: exercise.tutorExplanation,
    points: exercise.points ?? 10,
  };
}

function PracticeExercises() {
  const navigate = useNavigate();
  const { moduleId } = useParams();
  const [exercises, setExercises] = useState(fallbackExercises);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showFeedback, setShowFeedback] = useState(false);
  const [points, setPoints] = useState(0);

  const module =
    modulesData.find((item) => String(item.id) === String(moduleId)) ??
    modulesData[0];
  const level =
    module?.levels.find((item) => item.unlocked) ?? module?.levels[0];

  useEffect(() => {
    getLearningContent(moduleId)
      .then((content) => {
        if (Array.isArray(content.ejercicios) && content.ejercicios.length > 0) {
          setExercises(content.ejercicios.map(mapExercise));
          setCurrentIndex(0);
          setSelectedAnswer(null);
          setShowFeedback(false);
        }
      })
      .catch(() => setExercises(fallbackExercises));
  }, [moduleId]);

  const currentExercise = exercises[currentIndex];
  const isCorrect = selectedAnswer === currentExercise.correctAnswer;
  const progress = ((currentIndex + 1) / exercises.length) * 100;

  const handleAnswer = (index) => {
    if (showFeedback) return;

    setSelectedAnswer(index);
    setShowFeedback(true);

    if (index === currentExercise.correctAnswer) {
      setPoints((prev) => prev + currentExercise.points);
    }
  };

  const handleNext = () => {
    if (currentIndex < exercises.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setSelectedAnswer(null);
      setShowFeedback(false);
    } else {
      navigate(`/final-exam/${moduleId}`);
    }
  };

  if (!module || !level) {
    return (
      <main className="min-h-screen bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
        <section className="mx-auto flex min-h-[calc(100vh-4rem)] max-w-xl items-center justify-center">
          <Card className="w-full rounded-[32px] border border-white/80 bg-white/90 p-0 text-center shadow-[0_24px_70px_rgba(37,99,235,0.18)]">
            <CardContent className="p-8">
              <h1 className="text-2xl font-black text-nt-text-primary">Ejercicios no encontrados</h1>
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

  return (
    <main className="min-h-screen overflow-hidden bg-[radial-gradient(circle_at_top_left,#ffffff_0,#dff4ff_34%,#bfe7ff_100%)] px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto w-full max-w-5xl">
        <Button
          type="button"
          variant="ghost"
          className="mb-4 h-10 rounded-[18px] bg-white/75 px-4 text-sm font-black text-nt-blue shadow-sm hover:bg-white hover:text-nt-purple"
          onClick={() => navigate(`/module/${moduleId}`)}
        >
          <ArrowLeft className="size-4" aria-hidden="true" />
          Volver al módulo
        </Button>

        <Card className="rounded-[32px] border border-white/80 bg-white/88 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
          <CardContent className="p-5 sm:p-7 lg:p-8">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
              <div>
                <Badge className="mb-3 h-6 rounded-full bg-nt-purple/10 px-3 text-[11px] font-black uppercase tracking-wide text-nt-purple hover:bg-nt-purple/10">
                  {module.title} - Nivel {level.name}
                </Badge>
                <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
                  Ejercicios prácticos
                </h1>
                <p className="mt-2 text-sm font-semibold leading-6 text-nt-text-secondary sm:text-base">
                  Resuelve los ejercicios para reforzar tu aprendizaje.
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

            <div className="mt-7">
              <div className="mb-2 flex items-center justify-between text-xs font-black text-nt-text-secondary sm:text-sm">
                <span>
                  Ejercicio {currentIndex + 1}/{exercises.length}
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
                  {currentExercise.question}
                </h2>

                <div className="mt-5 grid gap-3 md:grid-cols-2">
                  {currentExercise.options.map((option, index) => {
                    let optionClass =
                      "group flex min-h-[72px] items-center gap-3 rounded-[22px] border-2 bg-white p-4 text-left text-sm font-bold leading-5 text-nt-text-primary shadow-sm transition hover:-translate-y-0.5 hover:border-nt-purple hover:shadow-md sm:text-base";

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
                          className={`grid size-9 shrink-0 place-items-center rounded-full text-sm font-black transition ${
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

                  {!isCorrect && (
                    <div className="mt-5 flex flex-col gap-4 rounded-[24px] border border-white/80 bg-white/85 p-4 sm:flex-row">
                      <div className="grid size-12 shrink-0 place-items-center rounded-full bg-gradient-to-br from-nt-blue to-nt-purple text-white shadow-lg shadow-nt-purple/20">
                        <Bot className="size-6" aria-hidden="true" />
                      </div>
                      <div>
                        <strong className="text-sm font-black text-nt-text-primary">
                          Tutor IA
                        </strong>
                        <p className="mt-1 text-sm font-bold text-nt-text-secondary">
                          ¿Quieres que practiquemos juntos?
                        </p>

                        <div className="mt-3 flex flex-wrap gap-2">
                          <button className="rounded-full bg-nt-purple/10 px-3 py-2 text-xs font-black text-nt-purple">
                            Explícame, no entendí
                          </button>
                          <button className="rounded-full bg-nt-blue/10 px-3 py-2 text-xs font-black text-nt-blue">
                            Dame una pista
                          </button>
                          <button className="rounded-full bg-nt-green/12 px-3 py-2 text-xs font-black text-green-700">
                            Ponme un ejemplo diferente
                          </button>
                        </div>
                      </div>
                    </div>
                  )}
                </CardContent>
              </Card>
            )}

            {showFeedback && (
              <Button
                type="button"
                className="mt-6 h-12 w-full rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
                onClick={handleNext}
              >
                {currentIndex === exercises.length - 1
                  ? "Ir al examen final"
                  : "Siguiente ejercicio"}
                <ArrowRight className="size-4" aria-hidden="true" />
              </Button>
            )}
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

export default PracticeExercises;
