import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { diagnosticQuestions } from "../data/diagnosticQuestions";
import styles from "../styles/DiagnosticExam.module.css";

function DiagnosticExam() {
  const navigate = useNavigate();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});

  const currentQuestion = diagnosticQuestions[currentIndex];
  const selectedAnswer = answers[currentQuestion.id];
  const progress = ((currentIndex + 1) / diagnosticQuestions.length) * 100;

  const handleSelect = (optionIndex) => {
    setAnswers({
      ...answers,
      [currentQuestion.id]: optionIndex,
    });
  };

  const getOptionClass = (index) =>
    `${styles.optionButton} ${selectedAnswer === index ? styles.selected : ""}`;

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

  const handleNext = () => {
    if (selectedAnswer === undefined) return;

    if (currentIndex < diagnosticQuestions.length - 1) {
      setCurrentIndex(currentIndex + 1);
      return;
    }

    const score = calculateScore(answers);
    const level = getLevel(score);

    navigate("/diagnostic-result", {
      state: {
        score,
        total: diagnosticQuestions.length,
        level,
        answers,
      },
    });
  };

  return (
    <div className={styles.container}>
      <div className={styles.examCard}>
        <div className={styles.progressHeader}>
          <span>
            Pregunta {currentIndex + 1} de {diagnosticQuestions.length}
          </span>
          <span>Tiempo estimado: 10 min</span>
        </div>

        <div className={styles.progressBar}>
          <div
            className={styles.progressFill}
            style={{ width: `${progress}%` }}
          />
        </div>

        <div className={styles.tutorBox}>
          <div className={styles.avatar}>🧠</div>
          <p>
            Lee con calma la pregunta y selecciona una alternativa para
            continuar.
          </p>
        </div>

        <p className={styles.questionText}>{currentQuestion.textBeforeImage}</p>

        {currentQuestion.image && (
          <div className={styles.imageBox}>
            <img
              src={currentQuestion.image}
              alt={`Pregunta ${currentQuestion.id}`}
            />
          </div>
        )}

        {currentQuestion.textAfterImage && (
          <p className={styles.questionText}>{currentQuestion.textAfterImage}</p>
        )}

        <div className={styles.optionsGrid}>
          {currentQuestion.options.map((option, index) => (
            <button
              key={index}
              className={getOptionClass(index)}
              onClick={() => handleSelect(index)}
            >
              <span>{String.fromCharCode(65 + index)}</span>
              {option}
            </button>
          ))}
        </div>

        <button
          className={styles.primaryButton}
          disabled={selectedAnswer === undefined}
          onClick={handleNext}
        >
          {currentIndex === diagnosticQuestions.length - 1
            ? "Finalizar diagnóstico"
            : "Siguiente"}
        </button>
      </div>
    </div>
  );
}

export default DiagnosticExam;