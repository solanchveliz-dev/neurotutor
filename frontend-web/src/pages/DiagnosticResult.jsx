import { useLocation, useNavigate } from "react-router-dom";
import styles from "../styles/DiagnosticResult.module.css";

function getStoredDiagnosticResult() {
  try {
    const result = localStorage.getItem("diagnosticResult");
    return result ? JSON.parse(result) : null;
  } catch {
    return null;
  }
}

function DiagnosticResult() {
  const navigate = useNavigate();
  const location = useLocation();
  const storedResult = getStoredDiagnosticResult();

  const score = location.state?.score ?? 0;
  const total = location.state?.total ?? 10;
  const percentage = Math.round((score / total) * 100);

  const levelMap = {
    BASICO: "Basico",
    INTERMEDIO: "Intermedio",
    AVANZADO: "Avanzado",
  };

  const level = levelMap[storedResult?.nivel] ?? location.state?.level ?? "Basico";
  const message =
    storedResult?.mensaje ?? "Diagnostico completado correctamente.";

  const levelConfig = {
    Basico: {
      icon: "NT",
      emoji: "Nivel",
      className: styles.basic,
    },
    Intermedio: {
      icon: "NT",
      emoji: "Nivel",
      className: styles.intermediate,
    },
    Avanzado: {
      icon: "NT",
      emoji: "Nivel",
      className: styles.advanced,
    },
  };

  const config = levelConfig[level] || levelConfig.Basico;

  return (
    <div className={styles.container}>
      <div className={`${styles.iconCircle} ${config.className}`}>
        {config.icon}
      </div>

      <h1>Evaluacion completada</h1>
      <p className={styles.subtitle}>Has terminado tu evaluacion inicial</p>

      <div className={styles.card}>
        <p className={styles.label}>Tu nivel detectado es</p>

        <div className={`${styles.levelBadge} ${config.className}`}>
          <span>{config.emoji}</span>
          {level.toUpperCase()}
        </div>

        <div className={styles.score}>
          <strong>{score}</strong>
          <span>/ {total}</span>
        </div>

        <p className={styles.scoreText}>
          respuestas correctas ({percentage}%)
        </p>

        <div className={styles.progressBar}>
          <div
            className={`${styles.progressFill} ${config.className}`}
            style={{ width: `${percentage}%` }}
          />
        </div>

        <div className={styles.messageBox}>{message}</div>
      </div>

      <button
        className={styles.startButton}
        onClick={() => navigate("/student-dashboard")}
      >
        Continuar
      </button>
    </div>
  );
}

export default DiagnosticResult;
