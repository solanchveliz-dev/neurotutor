import { useLocation, useNavigate } from "react-router-dom";
import { diagnosticQuestions } from "../data/diagnosticQuestions";
import styles from "../styles/DiagnosticReview.module.css";

function DiagnosticReview() {
  const navigate = useNavigate();
  const location = useLocation();

  const answers = location.state?.answers ?? {};
  const score = location.state?.score ?? 0;
  const total = location.state?.total ?? diagnosticQuestions.length;
  const level = location.state?.level ?? "Básico";

  return (
    <div className={styles.container}>
      <div className={styles.headerCard}>
        <h1>Revisión de resultados</h1>
        <p>
          Nivel detectado: <strong>{level}</strong> · Puntaje:{" "}
          <strong>
            {score}/{total}
          </strong>
        </p>
      </div>

      <div className={styles.list}>
        {diagnosticQuestions.map((question, index) => {
          const selectedIndex = answers[question.id];
          const isCorrect = selectedIndex === question.correctAnswer;

          return (
            <div
              key={question.id}
              className={`${styles.questionCard} ${
                isCorrect ? styles.correct : styles.incorrect
              }`}
            >
              <div className={styles.questionHeader}>
                <h2>Pregunta {index + 1}</h2>
                <span>{isCorrect ? "Correcta ✅" : "Incorrecta ❌"}</span>
              </div>

              <p className={styles.questionText}>{question.textBeforeImage}</p>

              {question.image && (
                <div className={styles.imageBox}>
                  <img src={question.image} alt={`Pregunta ${question.id}`} />
                </div>
              )}

              {question.textAfterImage && (
                <p className={styles.questionText}>
                  {question.textAfterImage}
                </p>
              )}

              <div className={styles.answerBox}>
                <p>
                  <strong>Tu respuesta:</strong>{" "}
                  {selectedIndex !== undefined
                    ? question.options[selectedIndex]
                    : "No respondida"}
                </p>

                <p>
                  <strong>Respuesta correcta:</strong>{" "}
                  {question.options[question.correctAnswer]}
                </p>

                <p>
                  <strong>Explicación:</strong>{" "}
                  {question.explanation ??
                    "Compara el enunciado con la alternativa correcta y revisa el procedimiento paso a paso para reforzar este tema."}
                </p>
              </div>
            </div>
          );
        })}
      </div>

      <button
        className={styles.startButton}
        onClick={() => navigate("/student-dashboard")}
      >
        Comenzar a Aprender 🚀
      </button>
    </div>
  );
}

export default DiagnosticReview;