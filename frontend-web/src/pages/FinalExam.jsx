import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { modulesData } from "../data/modulesData";
import styles from "../styles/FinalExam.module.css";

function FinalExam() {
  const navigate = useNavigate();
  const { moduleId, levelId } = useParams();

  const module = modulesData.find((item) => item.id === moduleId);
  const level = module?.levels.find((item) => item.id === levelId);

  const examQuestions = [
    {
      question: "¿Qué representa el denominador de una fracción?",
      options: [
        "Las partes tomadas",
        "El total de partes iguales",
        "La respuesta",
        "El número mayor",
      ],
      correctAnswer: 1,
    },
    {
      question: "¿Cuál fracción es equivalente a 1/2?",
      options: ["2/4", "1/3", "3/5", "4/5"],
      correctAnswer: 0,
    },
    {
      question: "Si una figura se divide en 4 partes iguales y tomas 3, ¿qué fracción tienes?",
      options: ["1/4", "4/3", "3/4", "2/4"],
      correctAnswer: 2,
    },
    {
      question: "¿Cuál es el numerador en 7/10?",
      options: ["10", "7", "17", "3"],
      correctAnswer: 1,
    },
    {
      question: "¿Qué fracción representa un entero completo?",
      options: ["1/4", "2/8", "4/4", "3/5"],
      correctAnswer: 2,
    },
  ];

  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [finished, setFinished] = useState(false);

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

  const handleNext = () => {
    if (selectedAnswer === undefined) return;

    if (currentIndex < examQuestions.length - 1) {
      setCurrentIndex(currentIndex + 1);
    } else {
      setFinished(true);
    }
  };

  if (!module || !level) {
    return (
      <div className={styles.container}>
        <div className={styles.card}>
          <h1>Examen no encontrado</h1>
          <button onClick={() => navigate("/learning-path")}>
            Volver a módulos
          </button>
        </div>
      </div>
    );
  }

  if (finished) {
    const score = calculateScore();
    const percentage = Math.round((score / examQuestions.length) * 100);
    const approved = percentage >= 70;

    return (
      <div className={styles.container}>
        <div className={styles.resultCard}>
          <div className={styles.resultIcon}>{approved ? "🏆" : "📚"}</div>

          <h1>{approved ? "¡Nivel aprobado!" : "Necesitas repasar"}</h1>

          <p>
            Obtuviste <strong>{score}</strong> de{" "}
            <strong>{examQuestions.length}</strong> respuestas correctas.
          </p>

          <div
            className={`${styles.resultBadge} ${
              approved ? styles.approved : styles.failed
            }`}
          >
            {percentage}%
          </div>

          <p className={styles.message}>
            {approved
              ? "Excelente trabajo. Puedes continuar con el siguiente nivel."
              : "Te recomendamos revisar la teoría y repetir los ejercicios antes de intentarlo otra vez."}
          </p>

          <button onClick={() => navigate("/learning-path")}>
            Volver a la ruta de aprendizaje
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
          onClick={() => navigate(`/module/${moduleId}/${levelId}`)}
        >
          ← Volver al módulo
        </button>

        <div className={styles.header}>
          <span className={styles.badge}>
            Examen final · {module.title} · {level.name}
          </span>
          <h1>Demuestra lo aprendido</h1>
          <p>
            Durante el examen final no está disponible el Tutor IA. Responde
            todas las preguntas para finalizar.
          </p>
        </div>

        <div className={styles.progressInfo}>
          <span>
            Pregunta {currentIndex + 1}/{examQuestions.length}
          </span>
          <span>{Math.round(progress)}%</span>
        </div>

        <div className={styles.progressBar}>
          <div
            className={styles.progressFill}
            style={{ width: `${progress}%` }}
          />
        </div>

        <section className={styles.questionBox}>
          <h2>{currentQuestion.question}</h2>

          <div className={styles.optionsGrid}>
            {currentQuestion.options.map((option, index) => (
              <button
                key={index}
                className={`${styles.optionButton} ${
                  selectedAnswer === index ? styles.selected : ""
                }`}
                onClick={() => handleSelect(index)}
              >
                <span>{String.fromCharCode(65 + index)}</span>
                {option}
              </button>
            ))}
          </div>
        </section>

        <button
          className={styles.nextButton}
          disabled={selectedAnswer === undefined}
          onClick={handleNext}
        >
          {currentIndex === examQuestions.length - 1
            ? "Finalizar examen"
            : "Siguiente"}
        </button>
      </div>
    </div>
  );
}

export default FinalExam;