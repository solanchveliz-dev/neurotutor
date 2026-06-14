import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { modulesData } from "../data/modulesData";
import { getLearningContent } from "../services/learningService";
import styles from "../styles/PracticeExercises.module.css";

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
      <div className={styles.container}>
        <div className={styles.card}>
          <h1>Ejercicios no encontrados</h1>
          <button onClick={() => navigate("/learning-path")}>
            Volver a modulos
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <button
          className={styles.backButton}
          onClick={() => navigate(`/module/${moduleId}`)}
        >
          Volver al modulo
        </button>

        <div className={styles.header}>
          <div>
            <span className={styles.badge}>
              {module.title} - Nivel {level.name}
            </span>
            <h1>Ejercicios practicos</h1>
            <p>Resuelve los ejercicios para reforzar tu aprendizaje.</p>
          </div>

          <div className={styles.pointsBox}>
            <span>Puntos</span>
            <strong>{points}</strong>
          </div>
        </div>

        <div className={styles.progressInfo}>
          <span>
            Ejercicio {currentIndex + 1}/{exercises.length}
          </span>
          <span>{Math.round(progress)}%</span>
        </div>

        <div className={styles.progressBar}>
          <div
            className={styles.progressFill}
            style={{ width: `${progress}%` }}
          />
        </div>

        <section className={styles.exerciseBox}>
          <h2>{currentExercise.question}</h2>

          <div className={styles.optionsGrid}>
            {currentExercise.options.map((option, index) => {
              let optionClass = styles.optionButton;

              if (showFeedback && index === currentExercise.correctAnswer) {
                optionClass += ` ${styles.correct}`;
              }

              if (
                showFeedback &&
                selectedAnswer === index &&
                selectedAnswer !== currentExercise.correctAnswer
              ) {
                optionClass += ` ${styles.incorrect}`;
              }

              return (
                <button
                  key={index}
                  className={optionClass}
                  onClick={() => handleAnswer(index)}
                >
                  <span>{String.fromCharCode(65 + index)}</span>
                  {option}
                </button>
              );
            })}
          </div>
        </section>

        {showFeedback && (
          <div
            className={`${styles.feedbackBox} ${
              isCorrect ? styles.feedbackCorrect : styles.feedbackIncorrect
            }`}
          >
            <h3>{isCorrect ? "Correcto" : "Respuesta incorrecta"}</h3>
            <p>
              {currentExercise.explanation ??
                "Revisa la alternativa correcta y vuelve a intentarlo."}
            </p>

            {!isCorrect && (
              <div className={styles.aiTutor}>
                <div className={styles.tutorAvatar}>NT</div>
                <div>
                  <strong>Tutor IA</strong>
                  <p>Quieres que practiquemos juntos?</p>

                  <div className={styles.aiButtons}>
                    <button>Explicame, no entendi</button>
                    <button>Dame una pista</button>
                    <button>Ponme un ejemplo diferente</button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {showFeedback && (
          <button className={styles.nextButton} onClick={handleNext}>
            {currentIndex === exercises.length - 1
              ? "Ir al examen final"
              : "Siguiente ejercicio"}
          </button>
        )}
      </div>
    </div>
  );
}

export default PracticeExercises;
