import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { modulesData } from "../data/modulesData";
import {
  getExamPassed,
  getFinalExam,
  submitExamV2,
} from "../services/learningService";
import { getStudentId } from "../utils/auth";
import styles from "../styles/FinalExam.module.css";

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

function getLevelCode(level) {
  if (level?.name === "Intermedio") return "I";
  if (level?.name === "Avanzado") return "A";
  return "B";
}

function FinalExam() {
  const navigate = useNavigate();
  const { moduleId } = useParams();
  const [examQuestions, setExamQuestions] = useState(fallbackExamQuestions);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [finished, setFinished] = useState(false);
  const [submitResult, setSubmitResult] = useState(null);
  const [alreadyPassed, setAlreadyPassed] = useState(false);

  const module =
    modulesData.find((item) => String(item.id) === String(moduleId)) ??
    modulesData[0];
  const level =
    module?.levels.find((item) => item.unlocked) ?? module?.levels[0];

  useEffect(() => {
    const studentId = getStudentId();

    getFinalExam(moduleId)
      .then((questions) => {
        if (Array.isArray(questions) && questions.length > 0) {
          setExamQuestions(questions.map(mapExamQuestion));
          setCurrentIndex(0);
          setAnswers({});
          setFinished(false);
        }
      })
      .catch(() => setExamQuestions(fallbackExamQuestions));

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

    if (!studentId) return;

    try {
      const result = await submitExamV2({
        studentId: Number(studentId),
        moduloId: Number(moduleId),
        level: getLevelCode(level),
        score: percentage,
      });
      setSubmitResult(result);
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
      <div className={styles.container}>
        <div className={styles.card}>
          <h1>Examen no encontrado</h1>
          <button onClick={() => navigate("/learning-path")}>
            Volver a modulos
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
          <div className={styles.resultIcon}>{approved ? "NT" : "NT"}</div>

          <h1>{approved ? "Nivel aprobado" : "Necesitas repasar"}</h1>

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
            {submitResult?.message ??
              (alreadyPassed
                ? "Ya habias aprobado este examen anteriormente."
                : approved
                  ? "Excelente trabajo. Puedes continuar con el siguiente nivel."
                  : "Te recomendamos revisar la teoria y repetir los ejercicios antes de intentarlo otra vez.")}
          </p>

          <button onClick={() => navigate("/student-dashboard")}>
            Volver al panel
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
          <span className={styles.badge}>
            Examen final - {module.title} - {level.name}
          </span>
          <h1>Demuestra lo aprendido</h1>
          <p>
            Durante el examen final no esta disponible el Tutor IA. Responde
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
