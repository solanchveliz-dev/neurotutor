import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { ArrowRight, CheckCircle2, Star, XCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import LearningProgressPanel from "../components/student/LearningProgressPanel";
import { modulesData } from "../data/modulesData";
import { getLearningContent } from "../services/learningService";
import { submitPracticeAttempt } from "../services/progressService";
import { getStudentId } from "../utils/auth";

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
    id: exercise.id,
    question: exercise.question,
    options: exercise.options ?? [],
    correctAnswer: exercise.correctAnswerIndex,
    explanation: exercise.tutorExplanation,
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

function PracticeExercises() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId: nestedLevelId } = useParams();
  const learningModuloId = nestedLevelId ?? moduleId;
  const routeModule = location.state?.module;
  const routeLevel = location.state?.level;
  const [exercises, setExercises] = useState(fallbackExercises);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showFeedback, setShowFeedback] = useState(false);
  const [points, setPoints] = useState(0);
  const [answeredItems, setAnsweredItems] = useState({});
  const [isUsingFallback, setIsUsingFallback] = useState(true);
  const [attemptError, setAttemptError] = useState("");

  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(moduleId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => item.unlocked) ?? fallbackModule?.levels[0];
  const module = routeModule ?? fallbackModule;
  const level = routeLevel ?? fallbackLevel;
  const moduleTitle = getTitle(routeModule, getTitle(fallbackModule, "Modulo"));
  const rawLevelTitle = getTitle(routeLevel, getTitle(fallbackLevel, "Nivel"));
  const levelTitle = inferLevelName(rawLevelTitle);
  const backLevelId = routeLevel?.id ?? routeLevel?.levelId ?? nestedLevelId;
  const backModuleId = routeModule?.id ?? (nestedLevelId ? moduleId : null);
  const backPath =
    backModuleId && backLevelId
      ? `/module/${backModuleId}/level/${backLevelId}`
      : "/student-dashboard";

  useEffect(() => {
    getLearningContent(learningModuloId)
      .then((content) => {
        if (Array.isArray(content.ejercicios) && content.ejercicios.length > 0) {
          setExercises(content.ejercicios.map(mapExercise));
          setCurrentIndex(0);
          setSelectedAnswer(null);
          setShowFeedback(false);
          setAnsweredItems({});
          setIsUsingFallback(false);
        }
      })
      .catch(() => {
        setExercises(fallbackExercises);
        setIsUsingFallback(true);
      });
  }, [learningModuloId]);

  const currentExercise = exercises[currentIndex];
  const isCorrect = selectedAnswer === currentExercise.correctAnswer;
  const progress = ((currentIndex + 1) / exercises.length) * 100;

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

    if (!studentId || isUsingFallback || !answersPayload.length) return;

    try {
      setAttemptError("");
      const result = await submitPracticeAttempt({
        student_id: Number(studentId),
        modulo_id: Number(learningModuloId),
        answers: answersPayload,
      });
      setPoints(result.points_earned ?? points);
    } catch {
      setAttemptError("No se pudo guardar tu progreso de practica. Tu avance visual se mantiene en esta pantalla.");
    }
  };

  const handleNext = async () => {
    if (currentIndex < exercises.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setSelectedAnswer(null);
      setShowFeedback(false);
    } else {
      await submitAttemptIfReady();
      navigate(`/final-exam/${learningModuloId}`, { state: { module, level } });
    }
  };

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true, onClick: () => navigate(backPath, { state: { module, level } }) },
    { label: "Mis logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  if (!module || !level) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <section className="flex min-h-[400px] items-center justify-center">
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
